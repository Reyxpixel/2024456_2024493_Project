package ui;

import javax.swing.*;

public class AdminDashboard extends JFrame {
    public AdminDashboard(String username) {

        setTitle("Admin Dashboard - " + username);
        setSize(400, 200);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLabel label = new JLabel("Welcome Admin " + username);
        add(label);
    }
}
