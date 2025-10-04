package ui;

import javax.swing.*;

public class StudentDashboard extends JFrame {
    public StudentDashboard(String username) {
        setTitle("Student Dashboard - " + username);
        setSize(400, 200);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLabel label = new JLabel("Welcome Student " + username);
        add(label);
    }
}
