package com.libraryapp.uts.Controller;

import com.libraryapp.uts.Model.Book;
import com.libraryapp.uts.Model.Loan;
import com.libraryapp.uts.Model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class LoanPanel extends JPanel {
    private JComboBox<Book> bookComboBox;
    private JButton loanButton;
    private JButton returnButton;
    private JList<Loan> loanList;
    private DefaultListModel<Loan> loanListModel;
    private User currentUser;

    public LoanPanel(User user) {
        this.currentUser = user;
        initializeComponents();
        setupLayout();
        loadBooks();
        loadLoans();
        setupListeners();
    }

    private void initializeComponents() {
        bookComboBox = new JComboBox<>();
        loanButton = new JButton("Pinjam");
        returnButton = new JButton("Kembalikan");
        loanListModel = new DefaultListModel<>();
        loanList = new JList<>(loanListModel);
    }

    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        // Row 1: Label and ComboBox
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        add(new JLabel("Pinjam Buku:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(bookComboBox, gbc);

        // Row 2: Loan and Return buttons
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(loanButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        add(returnButton, gbc);

        // Row 3: Loan List
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        add(new JScrollPane(loanList), gbc);
    }

    private void setupListeners() {
        loanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loanBook();
            }
        });

        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnBook();
            }
        });
    }

    private void loadBooks() {
        Set<Book> uniqueBooks = new HashSet<>();
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/perpustakaan", "root", "")) {
            String query = "SELECT DISTINCT title,author FROM books";
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    String title = resultSet.getString("title");
                    String author = resultSet.getString("author");
                    uniqueBooks.add(new Book(title, author, true));
                }
            }

            bookComboBox.removeAllItems();
            for (Book book : uniqueBooks) {
                bookComboBox.addItem(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadLoans() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/perpustakaan", "root", "")) {
            String query = "SELECT loans.id, books.title, books.author, loans.loan_date, loans.return_date, loans.isReturned " +
                    "FROM loans JOIN books ON loans.book_id = books.id WHERE loans.user_id = ? AND loans.isReturned = false";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, currentUser.getId());
                try (ResultSet resultSet = statement.executeQuery()) {
                    loanListModel.clear();
                    while (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        String title = resultSet.getString("title");
                        String author = resultSet.getString("author");
                        LocalDate loanDate = resultSet.getDate("loan_date").toLocalDate();
                        LocalDate returnDate = resultSet.getDate("return_date") != null ? resultSet.getDate("return_date").toLocalDate() : null;
                        boolean isReturned = resultSet.getBoolean("isReturned");
                        loanListModel.addElement(new Loan(id, title, author, loanDate, returnDate, isReturned));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loanBook() {
        Book selectedBook = (Book) bookComboBox.getSelectedItem();
        if (selectedBook != null) {
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/perpustakaan", "root", "")) {
                String query = "INSERT INTO loans (user_id, book_id, loan_date, isReturned) VALUES (?, ?, ?, false)";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setInt(1, currentUser.getId());
                    statement.setInt(2, selectedBook.getId());
                    statement.setDate(3, Date.valueOf(LocalDate.now()));
                    statement.executeUpdate();
                }

                String updateBookQuery = "UPDATE books SET available = false WHERE id = ?";
                try (PreparedStatement updateBookStatement = connection.prepareStatement(updateBookQuery)) {
                    updateBookStatement.setInt(1, selectedBook.getId());
                    updateBookStatement.executeUpdate();
                }

                loadBooks();
                loadLoans();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void returnBook() {
        Loan selectedLoan = loanList.getSelectedValue();
        if (selectedLoan != null) {
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/perpustakaan", "root", "")) {
                String query = "UPDATE loans SET return_date = ?, isReturned = true WHERE id = ?";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setDate(1, Date.valueOf(LocalDate.now()));
                    statement.setInt(2, selectedLoan.getId());
                    statement.executeUpdate();
                }

                String updateBookQuery = "UPDATE books SET available = true WHERE id = (SELECT book_id FROM loans WHERE id = ?)";
                try (PreparedStatement updateBookStatement = connection.prepareStatement(updateBookQuery)) {
                    updateBookStatement.setInt(1, selectedLoan.getId());
                    updateBookStatement.executeUpdate();
                }

                loadBooks();
                loadLoans();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
