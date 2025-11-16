package ui;

import model.User;

import javax.swing.*;

public class InstructorDashboard extends JFrame {
    private final User User;
    private String instructorName;

    public InstructorDashboard(User user) {
        this.User = user;
        setTitle("Instructor Dashboard - " + user.getUsername());
        setSize(400, 200);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLabel label = new JLabel("Welcome Instructor " + user.getUsername());
        add(label);
    }
}
