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
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
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
    private RoundedToggleButton maintenanceToggle;
    private boolean updatingMaintenanceToggle = false;
    private Image headerBackgroundImage;

    public AdminDashboard(User user) {
        this.user = user;
        this.erpDb = new erpDB();
        loadHeaderBackground();
        initUI();
        refreshAllData();
    }

    private void loadHeaderBackground() {
        try {
            File imgFile = new File("src/ui/iiitdbackground.png");
            if (!imgFile.exists()) {
                imgFile = new File("ui/iiitdbackground.png");
            }
            if (imgFile.exists()) {
                headerBackgroundImage = ImageIO.read(imgFile);
            }
        } catch (IOException e) {
            headerBackgroundImage = null;
        }
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
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (headerBackgroundImage != null) {
                    g2.drawImage(headerBackgroundImage, 0, 0, getWidth(), getHeight(), null);
                }
                g2.setColor(new Color(25, 25, 25, 200));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(20, 25, 20, 25));

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

        JButton changePasswordBtn = createButton("Change Password", new Color(52, 152, 219));
        changePasswordBtn.addActionListener(e -> openChangePasswordDialog());

        JButton logoutBtn = createButton("Logout", new Color(220, 53, 69));
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
        tabs.setUI(new RoundedTabbedPaneUI());
        tabs.setOpaque(false);
        tabs.setBorder(new EmptyBorder(10, 20, 0, 20));
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

        maintenanceStatusLabel = new JLabel("Maintenance mode status unknown");
        maintenanceStatusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        maintenanceStatusLabel.setForeground(new Color(60, 60, 60));

        maintenanceToggle = new RoundedToggleButton();
        maintenanceToggle.addActionListener(e -> {
            if (updatingMaintenanceToggle) {
                return;
            }
            setMaintenanceMode(maintenanceToggle.isSelected());
        });

        JPanel maintenancePanel = new JPanel(new BorderLayout(10, 0));
        maintenancePanel.setOpaque(false);
        maintenancePanel.add(maintenanceStatusLabel, BorderLayout.WEST);
        maintenancePanel.add(maintenanceToggle, BorderLayout.EAST);

        panel.add(maintenancePanel, BorderLayout.NORTH);
        panel.add(stats, BorderLayout.CENTER);
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

        JButton addBtn = createButton("Add Course", new Color(52, 152, 219));
        addBtn.addActionListener(e -> openCourseDialog(null));

        JButton editBtn = createButton("Edit Selected", new Color(108, 117, 125));
        editBtn.addActionListener(e -> {
            Course selected = getSelectedCourse();
            if (selected != null) {
                openCourseDialog(selected);
            }
        });

        JButton deleteBtn = createButton("Delete Selected", new Color(231, 76, 60));
        deleteBtn.addActionListener(e -> deleteCourse());

        JButton refreshBtn = createButton("Refresh", new Color(40, 167, 69));
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

        JButton registerBtn = createButton("Register Student", new Color(52, 152, 219));
        registerBtn.addActionListener(e -> openStudentRegistrationDialog());

        JButton deleteBtn = createButton("Delete Selected", new Color(231, 76, 60));
        deleteBtn.addActionListener(e -> deleteStudent());

        JButton refreshBtn = createButton("Refresh", new Color(40, 167, 69));
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

        JButton registerBtn = createButton("Register Instructor", new Color(52, 152, 219));
        registerBtn.addActionListener(e -> openInstructorRegistrationDialog());

        JButton deleteBtn = createButton("Delete Selected", new Color(231, 76, 60));
        deleteBtn.addActionListener(e -> deleteInstructor());

        JButton refreshBtn = createButton("Refresh", new Color(40, 167, 69));
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

    private JButton createButton(String text, Color color) {
        RoundedButton button = new RoundedButton(text, color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return button;
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

    private static class RoundedButton extends JButton {
        private final int cornerRadius = 22;
        private final Color baseColor;

        RoundedButton(String text, Color baseColor) {
            super(text);
            this.baseColor = baseColor;
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setMargin(new Insets(10, 22, 10, 22));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color paintColor = baseColor;
            if (!isEnabled()) {
                paintColor = baseColor.darker();
            } else if (getModel().isPressed()) {
                paintColor = baseColor.darker();
            } else if (getModel().isRollover()) {
                paintColor = baseColor.brighter();
            }
            g2.setColor(paintColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class RoundedToggleButton extends JToggleButton {
        private final int radius = 28;

        RoundedToggleButton() {
            super("Maintenance Off");
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setForeground(Color.WHITE);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setMargin(new Insets(12, 26, 12, 26));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color base = isSelected() ? new Color(231, 76, 60) : new Color(40, 167, 69);
            if (getModel().isRollover()) {
                base = base.brighter();
            }
            g2.setColor(base);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class RoundedTabbedPaneUI extends BasicTabbedPaneUI {
        private final Color selectedColor = Color.WHITE;
        private final Color unselectedColor = new Color(232, 235, 239);
        private final Color textColor = new Color(80, 80, 80);

        @Override
        protected void installDefaults() {
            super.installDefaults();
            tabAreaInsets.right = 20;
            tabInsets = new Insets(14, 28, 14, 28);
        }

        @Override
        protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
                                          int x, int y, int w, int h, boolean isSelected) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(isSelected ? selectedColor : unselectedColor);
            g2.fillRoundRect(x + 4, y + 4, w - 8, h - 8, 24, 24);
            g2.dispose();
        }

        @Override
        protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
                                      int x, int y, int w, int h, boolean isSelected) {
            // no border
        }

        @Override
        protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
            // skip default border to keep clean look
        }

        @Override
        protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics,
                                 int tabIndex, String title, Rectangle textRect, boolean isSelected) {
            Font oldFont = g.getFont();
            Font useFont = font.deriveFont(Font.BOLD, 14f);
            g.setFont(useFont);
            FontMetrics fm = g.getFontMetrics(useFont);
            int textWidth = fm.stringWidth(title);
            int textHeight = fm.getAscent();
            int x = textRect.x + (textRect.width - textWidth) / 2;
            int y = textRect.y + ((textRect.height - fm.getHeight()) / 2) + textHeight;
            g.setColor(isSelected ? new Color(26, 115, 232) : textColor);
            g.drawString(title, x, y);
            g.setFont(oldFont);
        }

        @Override
        protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
            return 48;
        }

        @Override
        protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
            return Math.max(140, super.calculateTabWidth(tabPlacement, tabIndex, metrics));
        }
    }
}
