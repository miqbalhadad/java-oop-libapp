package com.libraryapp.uts.Controller;

import com.libraryapp.uts.Model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DashboardPanel extends JPanel {
    private User currentUser;

    public DashboardPanel(User user) {
        this.currentUser = user;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        if ("admin".equals(currentUser.getRoles())) {
            add(new JLabel("Admin Dashboard"));

            // Panel for borrowed books count
            JPanel borrowedBooksPanel = new JPanel();
            borrowedBooksPanel.setLayout(new BorderLayout());
            borrowedBooksPanel.add(new JLabel("Borrowed Books: "), BorderLayout.WEST);
            JLabel borrowedBooksCountLabel = new JLabel();
            borrowedBooksPanel.add(borrowedBooksCountLabel, BorderLayout.CENTER);
            add(borrowedBooksPanel);

            // Panel for available books count
            JPanel availableBooksPanel = new JPanel();
            availableBooksPanel.setLayout(new BorderLayout());
            availableBooksPanel.add(new JLabel("Available Books: "), BorderLayout.WEST);
            JLabel availableBooksCountLabel = new JLabel();
            availableBooksPanel.add(availableBooksCountLabel, BorderLayout.CENTER);
            add(availableBooksPanel);

            // Panel for book details
            JPanel bookDetailsPanel = new JPanel();
            bookDetailsPanel.setLayout(new BorderLayout());
            bookDetailsPanel.add(new JLabel("Riwayat Peminjaman: "), BorderLayout.NORTH);

            // Table for book details
            DefaultTableModel bookDetailsTableModel = new DefaultTableModel(new Object[]{"Title", "Author", "Username", "Loan Date", "Return Date"}, 0);
            JTable bookDetailsTable = new JTable(bookDetailsTableModel);
            bookDetailsPanel.add(new JScrollPane(bookDetailsTable), BorderLayout.CENTER);
            add(bookDetailsPanel);

            // Load data
            loadAdminData(borrowedBooksCountLabel, availableBooksCountLabel, bookDetailsTableModel);

        } else {
            JPanel loanPanel = new LoanPanel(currentUser);
            add(loanPanel);
        }
    }

    private void loadAdminData(JLabel borrowedBooksCountLabel, JLabel availableBooksCountLabel, DefaultTableModel bookDetailsTableModel) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/perpustakaan", "root", "");

            // Query for borrowed books count
            String borrowedBooksQuery = "SELECT COUNT(*) AS count FROM loans WHERE isReturned = false";
            PreparedStatement borrowedBooksStatement = connection.prepareStatement(borrowedBooksQuery);
            ResultSet borrowedBooksResultSet = borrowedBooksStatement.executeQuery();
            if (borrowedBooksResultSet.next()) {
                borrowedBooksCountLabel.setText(String.valueOf(borrowedBooksResultSet.getInt("count")));
            }

            // Query for available books count
            String availableBooksQuery = "SELECT COUNT(*) AS count FROM books WHERE available = true";
            PreparedStatement availableBooksStatement = connection.prepareStatement(availableBooksQuery);
            ResultSet availableBooksResultSet = availableBooksStatement.executeQuery();
            if (availableBooksResultSet.next()) {
                availableBooksCountLabel.setText(String.valueOf(availableBooksResultSet.getInt("count")));
            }

            // Query for book details
            String bookDetailsQuery = "SELECT books.title, books.author, users.username, loans.loan_date, loans.return_date " +
                    "FROM loans JOIN books ON loans.book_id = books.id " +
                    "JOIN users ON loans.user_id = users.id WHERE loans.isReturned = false";
            PreparedStatement bookDetailsStatement = connection.prepareStatement(bookDetailsQuery);
            ResultSet bookDetailsResultSet = bookDetailsStatement.executeQuery();
            while (bookDetailsResultSet.next()) {
                String title = bookDetailsResultSet.getString("title");
                String author = bookDetailsResultSet.getString("author");
                String username = bookDetailsResultSet.getString("username");
                Date loanDate = bookDetailsResultSet.getDate("loan_date");
                Date returnDate = bookDetailsResultSet.getDate("return_date");

                bookDetailsTableModel.addRow(new Object[]{title, author, username, loanDate, returnDate});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
