package com.libraryapp.uts.Controller;

import com.libraryapp.uts.Model.User;
import com.libraryapp.uts.Model.UserTableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class UserManagementPanel extends JPanel {
    private JTextField userField;
    private JPasswordField passwordField;
    private JTextField roleField;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JTable userTable;
    private UserTableModel userTableModel;

    public UserManagementPanel() {
        setLayout(new BorderLayout(10, 10));

        // Panel for input fields
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        userField = new JTextField(20);
        inputPanel.add(userField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        inputPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(new JLabel("Roles:"), gbc);

        gbc.gridx = 1;
        roleField = new JTextField(20);
        inputPanel.add(roleField, gbc);

        // Panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        addButton = new JButton("Tambah User");
        updateButton = new JButton("Edit User");
        deleteButton = new JButton("Hapus User");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        inputPanel.add(buttonPanel, gbc);

        add(inputPanel, BorderLayout.NORTH);

        // Table for displaying users
        userTableModel = new UserTableModel(new ArrayList<>());
        userTable = new JTable(userTableModel);
        userTable.setFillsViewportHeight(true);
        userTable.setAutoCreateRowSorter(true); // Enable sorting

        JScrollPane scrollPane = new JScrollPane(userTable);
        add(scrollPane, BorderLayout.CENTER);

        loadUsers();

        // Event listeners for buttons
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addUser();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateUser();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteUser();
            }
        });

        userTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = userTable.getSelectedRow();
            if (selectedRow >= 0) {
                User selectedUser = userTableModel.getUser(userTable.convertRowIndexToModel(selectedRow));
                userField.setText(selectedUser.getUsername());
                roleField.setText(selectedUser.getRoles());
            }
        });
    }

    private void loadUsers() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/perpustakaan", "root", "")) {
            String query = "SELECT * FROM users";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String user = resultSet.getString("username");
                String password = resultSet.getString("password");
                String role = resultSet.getString("roles");
                userTableModel.addUser(new User(id, user, password, role));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addUser() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/perpustakaan", "root", "")) {
            String user = userField.getText();
            String password = new String(passwordField.getPassword());
            String roles = roleField.getText();

            String query = "INSERT INTO users (username, password, roles) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setString(1, user);
            statement.setString(2, password);
            statement.setString(3, roles);
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int id = generatedKeys.getInt(1);
                userTableModel.addUser(new User(id, user, password, roles));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateUser() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/perpustakaan", "root", "")) {
            int selectedRow = userTable.getSelectedRow();
            if (selectedRow >= 0) {
                User selectedUser = userTableModel.getUser(userTable.convertRowIndexToModel(selectedRow));
                String username = userField.getText();
                String password = new String(passwordField.getPassword());
                String roles = roleField.getText();

                String query = "UPDATE users SET username = ?, password = ?, roles = ? WHERE id = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, username);
                statement.setString(2, password);
                statement.setString(3, roles);
                statement.setInt(4, selectedUser.getId());
                statement.executeUpdate();

                userTableModel.updateUser(selectedRow, new User(selectedUser.getId(), username, password, roles));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteUser() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/perpustakaan", "root", "")) {
            int selectedRow = userTable.getSelectedRow();
            if (selectedRow >= 0) {
                User selectedUser = userTableModel.getUser(userTable.convertRowIndexToModel(selectedRow));
                String query = "DELETE FROM users WHERE id = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setInt(1, selectedUser.getId());
                statement.executeUpdate();

                userTableModel.removeUser(selectedRow);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reloadUsers() {
        userTableModel.clear();
        loadUsers();
    }
}
