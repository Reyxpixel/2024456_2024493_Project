package ui;

import model.User;

import javax.swing.*;
import java.awt.*;

public class StudentDashboard extends JFrame {

    private final User User;
    private String studentName;

    public StudentDashboard(User user) {
        this.User = user;
        setTitle("Student Dashboard - " + user.getUsername());
        this.studentName = user.getUsername();
        setTitle("Student Dashboard - " + studentName);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Course Catalog", new CatalogPanel());
        tabs.addTab("My Registrations", new RegistrationsPanel());
        tabs.addTab("Timetable", new TimetablePanel());
        tabs.addTab("Grades", new GradesPanel());

        add(tabs, BorderLayout.CENTER);

        // Logout button
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            dispose(); // close dashboard
            new LoginScreen().setVisible(true); // back to login
        });

        JPanel footer = new JPanel();
        footer.add(logoutBtn);

        add(footer, BorderLayout.SOUTH);
    }

    static class CatalogPanel extends JPanel {
        public CatalogPanel() {
            add(new JLabel("pull up them courses"));
        }
    }
    static class RegistrationsPanel extends JPanel {
        public RegistrationsPanel() {
            add(new JLabel("registerations and dat"));
        }
    }
    static class TimetablePanel extends JPanel {
        public TimetablePanel() {
            add(new JLabel("more liek guidelines we aint gon follow"));
        }
    }
    static class GradesPanel extends JPanel {
        public GradesPanel() {
            add(new JLabel("pull up them grades"));
        }
    }
}
