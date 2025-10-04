package ui;

import javax.swing.*;

public class InstructorDashboard extends JFrame {
    public InstructorDashboard(String username) {
        setTitle("Instructor Dashboard - " + username);
        setSize(400, 200);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLabel label = new JLabel("Welcome Instructor " + username);
        add(label);
    }
}
