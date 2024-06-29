package com.libraryapp.uts.Controller;

import com.libraryapp.uts.Model.Book;
import com.libraryapp.uts.Model.BookTableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BookManagementPanel extends JPanel {
    private JTextField titleField;
    private JTextField authorField;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JTable bookTable;
    private BookTableModel bookTableModel;

    public BookManagementPanel() {
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Title:"), gbc);

        gbc.gridx = 1;
        titleField = new JTextField(20);
        inputPanel.add(titleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Author:"), gbc);

        gbc.gridx = 1;
        authorField = new JTextField(20);
        inputPanel.add(authorField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        addButton = new JButton("Tambah Buku");
        updateButton = new JButton("Edit Buku");
        deleteButton = new JButton("Hapus Buku");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        inputPanel.add(buttonPanel, gbc);

        add(inputPanel, BorderLayout.NORTH);

        bookTableModel = new BookTableModel(new ArrayList<>());
        bookTable = new JTable(bookTableModel);
        add(new JScrollPane(bookTable), BorderLayout.CENTER);

        loadBooks();

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addBook();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateBook();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteBook();
            }
        });

        bookTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = bookTable.getSelectedRow();
            if (selectedRow >= 0) {
                Book selectedBook = bookTableModel.getBook(selectedRow);
                titleField.setText(selectedBook.getTitle());
                authorField.setText(selectedBook.getAuthor());
            }
        });
    }

    private void loadBooks() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/perpustakaan", "root", "")) {
            String query = "SELECT * FROM books";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            List<Book> books = new ArrayList<>();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                boolean available = resultSet.getBoolean("available");
                books.add(new Book(id, title, author, available));
            }

            bookTableModel.setBooks(books);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addBook() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/perpustakaan", "root", "")) {
            String title = titleField.getText();
            String author = authorField.getText();

            String query = "INSERT INTO books (title, author, available) VALUES (?, ?, true)";
            PreparedStatement statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setString(1, title);
            statement.setString(2, author);
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int id = generatedKeys.getInt(1);
                Book newBook = new Book(id, title, author, true);
                bookTableModel.addBook(newBook);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateBook() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/perpustakaan", "root", "")) {
            int selectedRow = bookTable.getSelectedRow();
            if (selectedRow >= 0) {
                Book selectedBook = bookTableModel.getBook(selectedRow);
                String title = titleField.getText();
                String author = authorField.getText();

                String query = "UPDATE books SET title = ?, author = ? WHERE id = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, title);
                statement.setString(2, author);
                statement.setInt(3, selectedBook.getId());
                statement.executeUpdate();

                selectedBook.setTitle(title);
                selectedBook.setAuthor(author);
                bookTableModel.updateBook(selectedRow, selectedBook);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteBook() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/perpustakaan", "root", "")) {
            int selectedRow = bookTable.getSelectedRow();
            if (selectedRow >= 0) {
                Book selectedBook = bookTableModel.getBook(selectedRow);
                String query = "DELETE FROM books WHERE id = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setInt(1, selectedBook.getId());
                statement.executeUpdate();

                bookTableModel.removeBook(selectedRow);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reloadBooks() {
        bookTableModel.clear();
        loadBooks();
    }
}
