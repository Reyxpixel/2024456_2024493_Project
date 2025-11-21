package ui;

import db.erpDB;
import model.Course;
import model.Instructor;
import model.Settings;
import model.Student;
import model.User;
import service.AuthService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboard extends JFrame {
    private final User user;
    private final erpDB erpDb;

    private JTable courseTable;
    private JTable studentTable;
    private JTable instructorTable;

    private JLabel studentCountLabel;
    private JLabel instructorCountLabel;
    private JLabel courseCountLabel;
    private JLabel maintenanceStatusLabel;
    private JToggleButton maintenanceToggle;
    private boolean updatingMaintenanceToggle = false;

    public AdminDashboard(User user) {
        this.user = user;
        this.erpDb = new erpDB();
        initUI();
        refreshAllData();
    }

    private void initUI() {
        setTitle("Admin Dashboard - " + user.getUsername());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 720);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(0, 10));

        add(createHeader(), BorderLayout.NORTH);
        add(createTabs(), BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(new EmptyBorder(15, 20, 15, 20));
        header.setBackground(new Color(33, 37, 41));

        JLabel title = new JLabel("IIITD ERP • Admin");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Welcome, " + user.getUsername());
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(220, 220, 220));

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(subtitle);

        JButton changePasswordBtn = new JButton("Change Password");
        changePasswordBtn.addActionListener(e -> openChangePasswordDialog());

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(220, 53, 69));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginScreen().setVisible(true);
        });

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        actions.add(changePasswordBtn);
        actions.add(logoutBtn);

        header.add(titlePanel, BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);
        return header;
    }

    private JTabbedPane createTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Home", createHomePanel());
        tabs.addTab("Courses", createCoursesPanel());
        tabs.addTab("Students", createStudentsPanel());
        tabs.addTab("Instructors", createInstructorsPanel());
        return tabs;
    }

    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel stats = new JPanel(new GridLayout(1, 3, 20, 0));
        studentCountLabel = new JLabel("-");
        instructorCountLabel = new JLabel("-");
        courseCountLabel = new JLabel("-");

        stats.add(createStatCard("Registered Students", studentCountLabel));
        stats.add(createStatCard("Registered Instructors", instructorCountLabel));
        stats.add(createStatCard("Active Courses", courseCountLabel));

        JPanel maintenancePanel = new JPanel();
        maintenancePanel.setLayout(new BoxLayout(maintenancePanel, BoxLayout.Y_AXIS));
        maintenancePanel.setBorder(new EmptyBorder(30, 0, 0, 0));

        maintenanceStatusLabel = new JLabel("Maintenance mode status unknown");
        maintenanceStatusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        maintenanceStatusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        maintenanceToggle = new JToggleButton("Maintenance Off");
        maintenanceToggle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        maintenanceToggle.setAlignmentX(Component.CENTER_ALIGNMENT);
        maintenanceToggle.addActionListener(e -> {
            if (updatingMaintenanceToggle) {
                return;
            }
            setMaintenanceMode(maintenanceToggle.isSelected());
        });

        maintenancePanel.add(maintenanceStatusLabel);
        maintenancePanel.add(Box.createVerticalStrut(10));
        maintenancePanel.add(maintenanceToggle);

        panel.add(stats, BorderLayout.NORTH);
        panel.add(maintenancePanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStatCard(String label, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        card.setBackground(new Color(245, 247, 250));

        JLabel title = new JLabel(label);
        title.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(new Color(33, 150, 243));

        card.add(title, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createCoursesPanel() {
        courseTable = buildTable(new String[]{"ID", "Code", "Title", "Credits"});

        JButton addBtn = new JButton("Add Course");
        addBtn.addActionListener(e -> openCourseDialog(null));

        JButton editBtn = new JButton("Edit Selected");
        editBtn.addActionListener(e -> {
            Course selected = getSelectedCourse();
            if (selected != null) {
                openCourseDialog(selected);
            }
        });

        JButton deleteBtn = new JButton("Delete Selected");
        deleteBtn.addActionListener(e -> deleteCourse());

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadCourses());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        actions.add(addBtn);
        actions.add(editBtn);
        actions.add(deleteBtn);
        actions.add(refreshBtn);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(new EmptyBorder(10, 10, 10, 10));
        wrapper.add(actions, BorderLayout.NORTH);
        wrapper.add(new JScrollPane(courseTable), BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createStudentsPanel() {
        studentTable = buildTable(new String[]{"ID", "Name", "Email", "Program"});

        JButton registerBtn = new JButton("Register Student");
        registerBtn.addActionListener(e -> openStudentRegistrationDialog());

        JButton deleteBtn = new JButton("Delete Selected");
        deleteBtn.addActionListener(e -> deleteStudent());

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadStudents());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        actions.add(registerBtn);
        actions.add(deleteBtn);
        actions.add(refreshBtn);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(new EmptyBorder(10, 10, 10, 10));
        wrapper.add(actions, BorderLayout.NORTH);
        wrapper.add(new JScrollPane(studentTable), BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createInstructorsPanel() {
        instructorTable = buildTable(new String[]{"ID", "Name", "Email", "Department"});

        JButton registerBtn = new JButton("Register Instructor");
        registerBtn.addActionListener(e -> openInstructorRegistrationDialog());

        JButton deleteBtn = new JButton("Delete Selected");
        deleteBtn.addActionListener(e -> deleteInstructor());

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadInstructors());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        actions.add(registerBtn);
        actions.add(deleteBtn);
        actions.add(refreshBtn);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(new EmptyBorder(10, 10, 10, 10));
        wrapper.add(actions, BorderLayout.NORTH);
        wrapper.add(new JScrollPane(instructorTable), BorderLayout.CENTER);
        return wrapper;
    }

    private JTable buildTable(String[] headers) {
        DefaultTableModel model = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setRowHeight(24);
        table.setFillsViewportHeight(true);
        return table;
    }

    private void refreshAllData() {
        loadCourses();
        loadStudents();
        loadInstructors();
        refreshStats();
    }

    private void loadCourses() {
        DefaultTableModel model = (DefaultTableModel) courseTable.getModel();
        model.setRowCount(0);
        List<Course> courses = erpDb.getAllCourses();
        if (courses == null) {
            return;
        }
        for (Course course : courses) {
            model.addRow(new Object[]{
                    course.getId(),
                    course.getCode(),
                    course.getTitle(),
                    course.getCredits()
            });
        }
    }

    private void loadStudents() {
        DefaultTableModel model = (DefaultTableModel) studentTable.getModel();
        model.setRowCount(0);
        List<Student> students = erpDb.getAllStudents();
        if (students == null) {
            return;
        }
        for (Student student : students) {
            model.addRow(new Object[]{
                    student.getId(),
                    student.getName(),
                    student.getEmail(),
                    student.getProgram()
            });
        }
    }

    private void loadInstructors() {
        DefaultTableModel model = (DefaultTableModel) instructorTable.getModel();
        model.setRowCount(0);
        List<Instructor> instructors = erpDb.getAllInstructors();
        if (instructors == null) {
            return;
        }
        for (Instructor instructor : instructors) {
            model.addRow(new Object[]{
                    instructor.getId(),
                    instructor.getName(),
                    instructor.getEmail(),
                    instructor.getDepartment()
            });
        }
    }

    private void refreshStats() {
        studentCountLabel.setText(String.valueOf(erpDb.getStudentCount()));
        instructorCountLabel.setText(String.valueOf(erpDb.getInstructorCount()));
        courseCountLabel.setText(String.valueOf(erpDb.getCourseCount()));

        boolean isMaintenance = isMaintenanceEnabled();
        updatingMaintenanceToggle = true;
        maintenanceToggle.setSelected(isMaintenance);
        maintenanceToggle.setText(isMaintenance ? "Maintenance On" : "Maintenance Off");
        maintenanceStatusLabel.setText(isMaintenance
                ? "Portal is currently in maintenance mode"
                : "Portal is live for students and instructors");
        updatingMaintenanceToggle = false;
    }

    private boolean isMaintenanceEnabled() {
        Settings setting = erpDb.getSetting("maintenance_mode");
        return setting != null && "ON".equalsIgnoreCase(setting.getValue());
    }

    private void setMaintenanceMode(boolean enabled) {
        boolean saved = erpDb.setSetting("maintenance_mode", enabled ? "ON" : "OFF");
        if (!saved) {
            JOptionPane.showMessageDialog(this, "Unable to update maintenance mode", "Error", JOptionPane.ERROR_MESSAGE);
        }
        refreshStats();
    }

    private void openCourseDialog(Course current) {
        JTextField codeField = new JTextField(current != null ? current.getCode() : "");
        JTextField titleField = new JTextField(current != null ? current.getTitle() : "");
        SpinnerNumberModel creditsModel = new SpinnerNumberModel(current != null ? current.getCredits() : 4, 1, 20, 1);
        JSpinner creditsSpinner = new JSpinner(creditsModel);

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Course Code:"));
        panel.add(codeField);
        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Credits:"));
        panel.add(creditsSpinner);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                current == null ? "Add Course" : "Edit Course",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String code = codeField.getText().trim();
        String title = titleField.getText().trim();
        int credits = (int) creditsSpinner.getValue();

        if (code.isEmpty() || title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Code and title are required.");
            return;
        }

        boolean success;
        if (current == null) {
            success = erpDb.addCourse(code, title, credits);
        } else {
            success = erpDb.updateCourse(current.getId(), code, title, credits);
        }

        if (!success) {
            JOptionPane.showMessageDialog(this, "Unable to save course.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        loadCourses();
        refreshStats();
    }

    private Course getSelectedCourse() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a course to edit.");
            return null;
        }
        DefaultTableModel model = (DefaultTableModel) courseTable.getModel();
        int id = (int) model.getValueAt(selectedRow, 0);
        String code = (String) model.getValueAt(selectedRow, 1);
        String title = (String) model.getValueAt(selectedRow, 2);
        int credits = (int) model.getValueAt(selectedRow, 3);
        return new Course(id, code, title, credits);
    }

    private void deleteCourse() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Select a course to delete.");
            return;
        }
        DefaultTableModel model = (DefaultTableModel) courseTable.getModel();
        int id = (int) model.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete selected course?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        if (!erpDb.deleteCourse(id)) {
            JOptionPane.showMessageDialog(this, "Unable to delete course.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        loadCourses();
        refreshStats();
    }

    private void openStudentRegistrationDialog() {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField programField = new JTextField();

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.add(new JLabel("Username:"));
        form.add(usernameField);
        form.add(new JLabel("Temporary Password:"));
        form.add(passwordField);
        form.add(new JLabel("Full Name:"));
        form.add(nameField);
        form.add(new JLabel("Email:"));
        form.add(emailField);
        form.add(new JLabel("Program:"));
        form.add(programField);

        int result = JOptionPane.showConfirmDialog(this, form, "Register Student", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String program = programField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.");
            return;
        }

        if (!AuthService.register(username, password, "student")) {
            JOptionPane.showMessageDialog(this, "Unable to create login for this student.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!erpDb.addStudent(name, email, program)) {
            AuthService.deleteUser(username);
            JOptionPane.showMessageDialog(this, "Unable to save student profile.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Student registered successfully.");
        loadStudents();
        refreshStats();
    }

    private void deleteStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Select a student to delete.");
            return;
        }
        DefaultTableModel model = (DefaultTableModel) studentTable.getModel();
        int id = (int) model.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete selected student profile?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        if (!erpDb.deleteStudent(id)) {
            JOptionPane.showMessageDialog(this, "Unable to delete student.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        loadStudents();
        refreshStats();
    }

    private void openInstructorRegistrationDialog() {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField departmentField = new JTextField();

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.add(new JLabel("Username:"));
        form.add(usernameField);
        form.add(new JLabel("Temporary Password:"));
        form.add(passwordField);
        form.add(new JLabel("Full Name:"));
        form.add(nameField);
        form.add(new JLabel("Email:"));
        form.add(emailField);
        form.add(new JLabel("Department:"));
        form.add(departmentField);

        int result = JOptionPane.showConfirmDialog(this, form, "Register Instructor", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String department = departmentField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.");
            return;
        }

        if (!AuthService.register(username, password, "instructor")) {
            JOptionPane.showMessageDialog(this, "Unable to create login for this instructor.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!erpDb.addInstructor(name, email, department)) {
            AuthService.deleteUser(username);
            JOptionPane.showMessageDialog(this, "Unable to save instructor profile.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Instructor registered successfully.");
        loadInstructors();
        refreshStats();
    }

    private void deleteInstructor() {
        int selectedRow = instructorTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Select an instructor to delete.");
            return;
        }
        DefaultTableModel model = (DefaultTableModel) instructorTable.getModel();
        int id = (int) model.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete selected instructor profile?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        if (!erpDb.deleteInstructor(id)) {
            JOptionPane.showMessageDialog(this, "Unable to delete instructor.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        loadInstructors();
        refreshStats();
    }

    private void openChangePasswordDialog() {
        JPasswordField currentField = new JPasswordField();
        JPasswordField newField = new JPasswordField();
        JPasswordField confirmField = new JPasswordField();

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Current Password:"));
        panel.add(currentField);
        panel.add(new JLabel("New Password:"));
        panel.add(newField);
        panel.add(new JLabel("Confirm New Password:"));
        panel.add(confirmField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Change Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String current = new String(currentField.getPassword());
        String next = new String(newField.getPassword());
        String confirm = new String(confirmField.getPassword());

        if (next.isEmpty() || !next.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "New passwords do not match or are empty.");
            return;
        }

        boolean changed = AuthService.changePassword(user.getUsername(), current, next);
        if (!changed) {
            JOptionPane.showMessageDialog(this, "Unable to change password. Verify your current password.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Password updated successfully.");
    }
}
