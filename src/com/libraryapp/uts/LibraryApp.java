/*
    Kelompok 2 Sistem Perpustakaan
    1. Muhammad Iqbal Hadad (2023210083)
    2. Wisnu Adi Pratama (2023210079)
    3. Suryadi Sapari (2023210082)
    4. Nendi Febrianto (2023210076)
    5. Ujang Tana M (2023210037)
    6. Ayu Wahidah (2023210036)
    7. Bagus Edy Sukoco (2023210058)
 */

package com.libraryapp.uts;

import com.libraryapp.uts.Controller.BookManagementPanel;
import com.libraryapp.uts.Controller.DashboardPanel;
import com.libraryapp.uts.Controller.LoginPanel;
import com.libraryapp.uts.Controller.UserManagementPanel;
import com.libraryapp.uts.Model.User;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class LibraryApp {
    private JFrame frame;
    private JTabbedPane tabbedPane;

    public LibraryApp() {
        frame = new JFrame("Library Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        tabbedPane = new JTabbedPane();

        // Add the login panel initially
        LoginPanel loginPanel = new LoginPanel();
        loginPanel.setLibraryApp(this); // Pass the instance of LibraryApp to loginPanel
        tabbedPane.addTab("Login", loginPanel);

        frame.add(tabbedPane);
        frame.setVisible(true);
    }

    public void showDashboard(User user) {
        tabbedPane.removeAll(); // Clear existing tabs

        if ("admin".equals(user.getRoles())) {
            // Add admin dashboard tab
            DashboardPanel adminDashboardPanel = new DashboardPanel(user);
            tabbedPane.addTab("Dashboard", adminDashboardPanel);

            BookManagementPanel bukuPanel = new BookManagementPanel();
            tabbedPane.addTab("Buku", bukuPanel);

            UserManagementPanel userPanel = new UserManagementPanel();
            tabbedPane.addTab("Kelola Akun", userPanel);

            // Add ChangeListener to tabbedPane to reload books when "Buku" tab is selected
            tabbedPane.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    if (tabbedPane.getSelectedComponent() == bukuPanel) {
                        bukuPanel.reloadBooks();
                    }
                }
            });
        } else {
            // Add user dashboard tab or any other logic for different roles
            DashboardPanel userDashboardPanel = new DashboardPanel(user);
            tabbedPane.addTab("Dashboard", userDashboardPanel);
        }

        // Add logout tab
        JPanel logoutPanel = new JPanel();
        JButton logoutButton = new JButton("Logout");
        logoutPanel.add(logoutButton);
        tabbedPane.addTab("Logout", logoutPanel);

        logoutButton.addActionListener(e -> {
            // Return to login screen
            tabbedPane.removeAll();
            LoginPanel loginPanel = new LoginPanel();
            loginPanel.setLibraryApp(this); // Pass the instance of LibraryApp to loginPanel
            tabbedPane.addTab("Login", loginPanel);
            frame.revalidate();
            frame.repaint();
        });

        // Refresh UI
        frame.revalidate();
        frame.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LibraryApp();
        });
    }
}
