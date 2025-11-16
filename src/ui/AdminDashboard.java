package ui;

import model.User;

import javax.swing.*;

public class AdminDashboard extends JFrame {
    private final User User;
    private String studentName;

    public AdminDashboard(User user) {
        this.User = user;
        setTitle("Admin Dashboard - " + user.getUsername());
        setSize(400, 200);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLabel label = new JLabel("Welcome Admin " + user.getUsername());
        add(label);
    }
}
