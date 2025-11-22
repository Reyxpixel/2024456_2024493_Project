package ui;

import db.erpDB;
import model.*;
import service.AuthService;
import service.ErpService;
import util.ThemeManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InstructorDashboard extends JFrame {
    private static final Color BG_LIGHT = new Color(240, 242, 245);
    private static final Color PANEL_ACCENT = new Color(0x42, 0xB0, 0xAC);
    private static final Color BUTTON_DEFAULT = new Color(235, 238, 243);
    private static final Color BUTTON_TEXT = new Color(60, 60, 60);
    private static final String[] NAV_ITEMS = {
            "Home", "Grading"
    };

    private final User user;
    private final erpDB erpDb;
    private Instructor instructor;
    private Image headerBackgroundImage;

    private JTable coursesTable;
    private JTable gradingTable;
    private RoundedToggleButton darkModeToggle;

    private CardLayout contentLayout;
    private JPanel contentPanel;
    private final Map<String, NavigationButton> navButtons = new LinkedHashMap<>();

    public InstructorDashboard(User user) {
        this.user = user;
        this.erpDb = new erpDB();
        String username = user.getUsername();

        // Try to find instructor by email
        this.instructor = ErpService.getInstructorByEmail(username);

        if (this.instructor == null) {
            // Try to find instructor by matching username with email prefix or name
            List<Instructor> allInstructors = erpDb.getAllInstructors();
            for (Instructor i : allInstructors) {
                String email = i.getEmail() != null ? i.getEmail().toLowerCase() : "";
                String name = i.getName() != null ? i.getName().toLowerCase() : "";
                String usernameLower = username.toLowerCase();

                if (email.equals(usernameLower) ||
                        email.startsWith(usernameLower + "@") ||
                        name.equals(usernameLower)) {
                    this.instructor = i;
                    break;
                }
            }
        }

        if (this.instructor == null) {
            JOptionPane.showMessageDialog(null, "Instructor profile not found. Please contact administrator.");
            dispose();
            new LoginScreen().setVisible(true);
            return;
        }

        ThemeManager.loadDarkModePreference();
        loadHeaderBackground();
        initUI();
        refreshAllData();
        applyTheme();
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
        setTitle("Instructor Dashboard - " + instructor.getName());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 760);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());
        getContentPane().setBackground(ThemeManager.getBackgroundColor());

        JPanel northWrapper = new JPanel(new BorderLayout());
        northWrapper.setOpaque(false);
        northWrapper.add(createHeader(), BorderLayout.NORTH);
        northWrapper.add(createNavigationBar(), BorderLayout.SOUTH);
        add(northWrapper, BorderLayout.NORTH);

        contentLayout = new CardLayout();
        contentPanel = new JPanel(contentLayout);
        contentPanel.setOpaque(false);
        contentPanel.add(createHomePanel(), "Home");
        contentPanel.add(createGradingPanel(), "Grading");
        add(contentPanel, BorderLayout.CENTER);

        setActiveSection("Home");
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (headerBackgroundImage != null) {
                    int imgW = headerBackgroundImage.getWidth(null);
                    int imgH = headerBackgroundImage.getHeight(null);
                    if (imgW > 0 && imgH > 0) {
                        double scale = Math.max((double) getWidth() / imgW, (double) getHeight() / imgH);
                        int drawW = (int) (imgW * scale);
                        int drawH = (int) (imgH * scale);
                        int x = (getWidth() - drawW) / 2;
                        int y = (getHeight() - drawH) / 2;
                        g2.drawImage(headerBackgroundImage, x, y, drawW, drawH, null);
                    }
                }
                g2.setColor(new Color(25, 25, 25, 160));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(PANEL_ACCENT);
                g2.fillRect(0, getHeight() - 4, getWidth(), 4);
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 80));
        header.setBorder(new EmptyBorder(0, 25, 0, 25));

        JLabel title = new JLabel("IIITD ERP • Instructor Portal");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        title.setVerticalAlignment(SwingConstants.CENTER);

        JLabel subtitle = new JLabel("Welcome, " + instructor.getName());
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(220, 220, 220));
        subtitle.setVerticalAlignment(SwingConstants.CENTER);

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.add(Box.createVerticalGlue());
        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(subtitle);
        titlePanel.add(Box.createVerticalGlue());

        // Dark mode toggle
        darkModeToggle = new RoundedToggleButton();
        darkModeToggle.setText(ThemeManager.isDarkMode() ? "🌙 Dark" : "☀️ Light");
        darkModeToggle.setSelected(ThemeManager.isDarkMode());
        darkModeToggle.addActionListener(e -> {
            ThemeManager.setDarkMode(darkModeToggle.isSelected());
            darkModeToggle.setText(ThemeManager.isDarkMode() ? "🌙 Dark" : "☀️ Light");
            applyTheme();
        });

        JButton changePasswordBtn = createButton("Change Password");
        changePasswordBtn.addActionListener(e -> openChangePasswordDialog());

        JButton logoutBtn = createButton("Logout", new Color(220, 53, 69));
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginScreen().setVisible(true);
        });

        JPanel actions = new JPanel();
        actions.setOpaque(false);
        actions.setLayout(new BoxLayout(actions, BoxLayout.X_AXIS));
        actions.add(Box.createHorizontalGlue());
        actions.add(darkModeToggle);
        actions.add(Box.createHorizontalStrut(10));
        actions.add(changePasswordBtn);
        actions.add(Box.createHorizontalStrut(10));
        actions.add(logoutBtn);

        header.setLayout(new BorderLayout());
        header.add(titlePanel, BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);

        return header;
    }

    private JPanel createNavigationBar() {
        JPanel nav = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 14));
        nav.setBackground(ThemeManager.getNavBackgroundColor());
        nav.setBorder(new EmptyBorder(0, 25, 0, 25));
        navButtons.clear();
        for (String key : NAV_ITEMS) {
            NavigationButton button = new NavigationButton(key, PANEL_ACCENT, BUTTON_DEFAULT);
            button.addActionListener(e -> setActiveSection(key));
            navButtons.put(key, button);
            nav.add(button);
        }
        return nav;
    }

    private void setActiveSection(String key) {
        contentLayout.show(contentPanel, key);
        navButtons.forEach((name, button) -> button.setSelected(name.equals(key)));
        if (key.equals("Grading")) {
            loadGradingCourses();
        }
    }

    private void applyTheme() {
        getContentPane().setBackground(ThemeManager.getBackgroundColor());
        updatePanelTheme(contentPanel);
        updateTableTheme(coursesTable);
        updateTableTheme(gradingTable);
        repaint();
        revalidate();
    }

    private void updatePanelTheme(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                if (panel.getBackground().equals(new Color(240, 242, 245)) || 
                    panel.getBackground().equals(new Color(30, 30, 30))) {
                    panel.setBackground(ThemeManager.getBackgroundColor());
                } else if (panel.getBackground().equals(Color.WHITE) || 
                           panel.getBackground().equals(new Color(45, 45, 45))) {
                    panel.setBackground(ThemeManager.getPanelColor());
                }
                updatePanelTheme(panel);
            } else if (comp instanceof Container) {
                updatePanelTheme((Container) comp);
            }
        }
    }

    private void updateTableTheme(JTable table) {
        if (table == null) return;
        table.setBackground(ThemeManager.getTableBackgroundColor());
        table.setForeground(ThemeManager.getTextColor());
        table.setGridColor(ThemeManager.getBorderColor());
        JTableHeader header = table.getTableHeader();
        if (header != null) {
            header.setBackground(ThemeManager.getTableHeaderColor());
            header.setForeground(ThemeManager.getTextColor());
        }
    }

    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ThemeManager.getBackgroundColor());
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        DefaultTableModel model = new DefaultTableModel(new String[]{"Course Code", "Title", "Credits", "Section", "Students Enrolled", "Capacity"}, 0);
        coursesTable = new JTable(model);
        styleTable(coursesTable);
        coursesTable.setRowHeight(34);

        JButton refreshBtn = createButton("Refresh");
        refreshBtn.addActionListener(e -> loadCourses());

        JPanel actions = createActionBar(refreshBtn);
        return buildModulePanel(actions, coursesTable);
    }

    private JPanel createGradingPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ThemeManager.getBackgroundColor());
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Course selection panel
        JPanel courseSelectionPanel = new RoundedPanel(28, ThemeManager.getPanelColor());
        courseSelectionPanel.setLayout(new BorderLayout());
        courseSelectionPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel selectLabel = new JLabel("Select Course to Grade:");
        selectLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        selectLabel.setForeground(new Color(50, 50, 50));

        DefaultComboBoxModel<Course> courseModel = new DefaultComboBoxModel<>();
        JComboBox<Course> courseCombo = new JComboBox<>(courseModel);
        styleComboBox(courseCombo);
        courseCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Course course) {
                    setText(course.getCode() + " • " + course.getTitle());
                } else if (value == null) {
                    setText("Select a course...");
                }
                return component;
            }
        });

        JButton loadStudentsBtn = createButton("Load Students", PANEL_ACCENT, Color.WHITE);
        loadStudentsBtn.addActionListener(e -> {
            Course selected = (Course) courseCombo.getSelectedItem();
            if (selected != null) {
                loadStudentsForGrading(selected);
            } else {
                showMessage("Please select a course first.", MessageType.INFO);
            }
        });

        JPanel coursePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        coursePanel.setOpaque(false);
        coursePanel.add(selectLabel);
        coursePanel.add(Box.createHorizontalStrut(10));
        coursePanel.add(courseCombo);
        coursePanel.add(Box.createHorizontalStrut(10));
        coursePanel.add(loadStudentsBtn);

        courseSelectionPanel.add(coursePanel, BorderLayout.NORTH);

        // Students grading table
        DefaultTableModel model = new DefaultTableModel(new String[]{"Student Name", "Email", "SGPA", "Expand"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };
        gradingTable = new JTable(model);
        styleTable(gradingTable);
        gradingTable.setRowHeight(34);
        attachButtonColumn(gradingTable, 3, "Expand", this::handleExpandGrading);

        JScrollPane scrollPane = new JScrollPane(gradingTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        courseSelectionPanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(courseSelectionPanel, BorderLayout.CENTER);

        // Store combo box for later use
        gradingTable.putClientProperty("courseCombo", courseCombo);

        return panel;
    }

    private void loadCourses() {
        DefaultTableModel model = (DefaultTableModel) coursesTable.getModel();
        model.setRowCount(0);
        List<Section> sections = erpDb.getSectionsByInstructor(instructor.getId());

        for (Section section : sections) {
            Course course = erpDb.getCourseById(section.getCourseId());
            int enrolled = erpDb.getEnrollmentCountForSection(section.getId());

            model.addRow(new Object[]{
                    course != null ? course.getCode() : "?",
                    course != null ? course.getTitle() : "?",
                    course != null ? course.getCredits() : 0,
                    section.getName(),
                    enrolled,
                    section.getCapacity()
            });
        }
    }

    private void loadGradingCourses() {
        JComboBox<Course> courseCombo = (JComboBox<Course>) gradingTable.getClientProperty("courseCombo");
        if (courseCombo == null) return;

        DefaultComboBoxModel<Course> model = (DefaultComboBoxModel<Course>) courseCombo.getModel();
        model.removeAllElements();

        List<Section> sections = erpDb.getSectionsByInstructor(instructor.getId());
        List<Course> courses = new ArrayList<>();
        java.util.Set<Integer> courseIds = new java.util.HashSet<>();
        for (Section section : sections) {
            Course course = erpDb.getCourseById(section.getCourseId());
            if (course != null && !courseIds.contains(course.getId())) {
                courses.add(course);
                courseIds.add(course.getId());
            }
        }

        for (Course course : courses) {
            model.addElement(course);
        }
    }

    private void loadStudentsForGrading(Course course) {
        DefaultTableModel model = (DefaultTableModel) gradingTable.getModel();
        model.setRowCount(0);

        // Get all sections for this course assigned to this instructor
        List<Section> allSections = erpDb.getSectionsByInstructor(instructor.getId());
        List<Section> courseSections = new ArrayList<>();
        for (Section section : allSections) {
            if (section.getCourseId() == course.getId()) {
                courseSections.add(section);
            }
        }

        List<Enrollment> allEnrollments = new ArrayList<>();
        for (Section section : courseSections) {
            allEnrollments.addAll(erpDb.getEnrollmentsBySection(section.getId()));
        }

        gradingTable.putClientProperty("enrollments", allEnrollments);

        for (Enrollment enrollment : allEnrollments) {
            Student student = erpDb.getStudentById(enrollment.getStudentId());
            if (student == null) continue;

            Grade grade = enrollment.getGradeId() != null
                    ? erpDb.getGradeById(enrollment.getGradeId())
                    : null;

            String sgpa = "-";
            if (grade != null && grade.getSGPA() != null) {
                sgpa = String.format("%.2f", grade.getSGPA());
            }

            model.addRow(new Object[]{
                    student.getName(),
                    student.getEmail(),
                    sgpa,
                    "Expand"
            });
        }
    }

    private void handleExpandGrading(int viewRow) {
        List<Enrollment> enrollments = (List<Enrollment>) gradingTable.getClientProperty("enrollments");
        if (enrollments == null || viewRow < 0) {
            return;
        }
        int modelRow = gradingTable.convertRowIndexToModel(viewRow);
        if (modelRow < 0 || modelRow >= enrollments.size()) {
            return;
        }
        Enrollment enrollment = enrollments.get(modelRow);
        openGradingDialog(enrollment);
    }

    private void openGradingDialog(Enrollment enrollment) {
        Student student = erpDb.getStudentById(enrollment.getStudentId());
        Section section = erpDb.getSectionById(enrollment.getSectionId());
        Course course = section != null ? erpDb.getCourseById(section.getCourseId()) : null;
        Grade grade = enrollment.getGradeId() != null
                ? erpDb.getGradeById(enrollment.getGradeId())
                : null;

        JDialog dialog = new JDialog(this, "Grade Student • " + (student != null ? student.getName() : "Unknown"), true);
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(this);

        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        infoPanel.add(new JLabel("Student:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel(student != null ? student.getName() : "-"), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        infoPanel.add(new JLabel("Course:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel(course != null ? course.getCode() + " • " + course.getTitle() : "-"), gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        infoPanel.add(new JLabel("Section:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel(section != null ? section.getName() : "-"), gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        infoPanel.add(new JLabel("SGPA:"), gbc);
        gbc.gridx = 1;
        RoundedTextField sgpaField = createInputField(grade != null && grade.getSGPA() != null
                ? String.format("%.2f", grade.getSGPA()) : "");
        sgpaField.setPreferredSize(new Dimension(150, 30));
        infoPanel.add(sgpaField, gbc);

        JLabel maxLabel = new JLabel("(Max: 10.1)");
        maxLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        maxLabel.setForeground(new Color(100, 100, 100));
        gbc.gridx = 2;
        infoPanel.add(maxLabel, gbc);

        JButton saveBtn = createButton("Save", PANEL_ACCENT, Color.WHITE);
        saveBtn.addActionListener(e -> {
            String sgpaText = sgpaField.getText().trim();
            if (sgpaText.isEmpty()) {
                showMessage("Please enter an SGPA value.", MessageType.ERROR);
                return;
            }

            try {
                float sgpa = Float.parseFloat(sgpaText);
                if (sgpa < 0 || sgpa > 10.1f) {
                    showMessage("SGPA must be between 0 and 10.1.", MessageType.ERROR);
                    return;
                }

                int gradeId = erpDb.createOrUpdateGrade(enrollment.getId(), sgpa);
                if (gradeId > 0) {
                    showMessage("Grade saved successfully.", MessageType.SUCCESS);
                    loadStudentsForGrading(course);
                    dialog.dispose();
                } else {
                    showMessage("Unable to save grade.", MessageType.ERROR);
                }
            } catch (NumberFormatException ex) {
                showMessage("Please enter a valid number for SGPA.", MessageType.ERROR);
            }
        });

        JButton cancelBtn = createButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);

        content.add(infoPanel, BorderLayout.CENTER);
        content.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.setVisible(true);
    }

    private void refreshAllData() {
        loadCourses();
    }

    private void openChangePasswordDialog() {
        RoundedPasswordField currentField = createPasswordField();
        RoundedPasswordField newField = createPasswordField();
        RoundedPasswordField confirmField = createPasswordField();

        JPanel form = createFormPanel();
        GridBagConstraints gbc = createFormConstraints();
        addFormHeading(form, gbc, "Change Password");
        addFormRow(form, gbc, "Current Password", currentField);
        addFormRow(form, gbc, "New Password", newField);
        addFormRow(form, gbc, "Confirm New Password", confirmField);

        if (!showFormDialog("Change Password", form)) {
            return;
        }

        String current = new String(currentField.getPassword());
        String next = new String(newField.getPassword());
        String confirm = new String(confirmField.getPassword());

        if (next.isEmpty() || !next.equals(confirm)) {
            showMessage("New passwords do not match or are empty.", MessageType.ERROR);
            return;
        }

        boolean changed = AuthService.changePassword(user.getUsername(), current, next);
        if (!changed) {
            showMessage("Unable to change password. Verify your current password.", MessageType.ERROR);
            return;
        }

        showMessage("Password updated successfully.", MessageType.SUCCESS);
    }

    private JPanel buildModulePanel(JPanel actions, JTable table) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(ThemeManager.getBackgroundColor());
        wrapper.setBorder(new EmptyBorder(20, 20, 20, 20));

        RoundedPanel card = new RoundedPanel(28, ThemeManager.getPanelColor());
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.add(actions, BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        card.add(scrollPane, BorderLayout.CENTER);

        wrapper.add(card, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createActionBar(JButton... buttons) {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        actions.setOpaque(false);
        for (JButton button : buttons) {
            actions.add(button);
        }
        return actions;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(42);
        table.setFillsViewportHeight(true);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(true);
        table.setGridColor(new Color(220, 220, 220));
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setDefaultRenderer(Object.class, new SimpleTableCellRenderer());
        table.setBackground(ThemeManager.getTableBackgroundColor());
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(ThemeManager.getAccentColor());
        table.setSelectionForeground(Color.WHITE);
        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 42));
        header.setBackground(ThemeManager.getTableHeaderColor());
        header.setForeground(ThemeManager.getTextColor());
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, ThemeManager.getBorderColor()),
                new EmptyBorder(10, 12, 10, 12)
        ));
    }

    private void attachButtonColumn(JTable table, int columnIndex, String label, java.util.function.IntConsumer handler) {
        TableColumn column = table.getColumnModel().getColumn(columnIndex);
        column.setCellRenderer(new ButtonCellRenderer(label));
        column.setCellEditor(new ButtonCellEditor(table, label, handler));
        column.setPreferredWidth(100);
        column.setMinWidth(90);
        column.setMaxWidth(110);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(228, 233, 240)),
                new EmptyBorder(20, 25, 20, 25)
        ));
        return panel;
    }

    private GridBagConstraints createFormConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridy = -1;
        return gbc;
    }

    private void addFormHeading(JPanel panel, GridBagConstraints gbc, String heading) {
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 12, 0);
        JLabel label = new JLabel(heading);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(new Color(45, 45, 45));
        panel.add(label, gbc);
        gbc.gridwidth = 1;
        gbc.insets = new Insets(6, 0, 6, 15);
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field) {
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(new Color(90, 95, 105));
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(field, gbc);
    }

    private RoundedTextField createInputField(String value) {
        RoundedTextField field = new RoundedTextField();
        if (value != null) {
            field.setText(value);
        }
        return field;
    }

    private RoundedPasswordField createPasswordField() {
        return new RoundedPasswordField();
    }

    private void styleComboBox(JComboBox<?> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setBackground(Color.WHITE);
        combo.setForeground(new Color(50, 50, 50));
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(8, 10, 8, 10)
        ));
    }

    private boolean showFormDialog(String title, JPanel form) {
        final boolean[] result = {false};

        JButton saveBtn = createButton("Save", PANEL_ACCENT, Color.WHITE);
        JButton cancelBtn = createButton("Cancel");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);

        JPanel dialogPanel = new JPanel(new BorderLayout());
        dialogPanel.setBackground(Color.WHITE);
        dialogPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
        dialogPanel.add(form, BorderLayout.CENTER);
        dialogPanel.add(buttonPanel, BorderLayout.SOUTH);

        JDialog dialog = new JDialog(this, title, true);
        dialog.setContentPane(dialogPanel);
        dialog.setResizable(false);
        dialog.pack();
        dialog.setLocationRelativeTo(this);

        saveBtn.addActionListener(e -> {
            result[0] = true;
            dialog.setVisible(false);
            dialog.dispose();
        });
        cancelBtn.addActionListener(e -> {
            result[0] = false;
            dialog.setVisible(false);
            dialog.dispose();
        });

        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);

        return result[0];
    }

    private void showMessage(String message, MessageType type) {
        JLabel label = new JLabel(message);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(50, 50, 50));
        label.setBorder(new EmptyBorder(25, 25, 15, 25));

        JButton okBtn = createButton("OK", type.background, Color.WHITE);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        buttonPanel.add(okBtn);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(label, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        JDialog dialog = new JDialog(this, type.title, true);
        dialog.setContentPane(contentPanel);
        dialog.setResizable(false);
        dialog.pack();
        dialog.setLocationRelativeTo(this);

        okBtn.addActionListener(e -> {
            dialog.setVisible(false);
            dialog.dispose();
        });

        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }

    private JButton createButton(String text) {
        return createButton(text, BUTTON_DEFAULT, BUTTON_TEXT);
    }

    private JButton createButton(String text, Color color) {
        return createButton(text, color, Color.WHITE);
    }

    private JButton createButton(String text, Color color, Color foreground) {
        RoundedButton button = new RoundedButton(text, color, foreground);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return button;
    }

    // Inner classes
    private static class NavigationButton extends JToggleButton {
        private final Color activeColor;
        private final Color inactiveColor;

        NavigationButton(String text, Color activeColor, Color inactiveColor) {
            super(text);
            this.activeColor = activeColor;
            this.inactiveColor = inactiveColor;
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setForeground(new Color(80, 84, 96));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setMargin(new Insets(12, 26, 12, 26));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color fill = isSelected() ? activeColor : inactiveColor;
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            g2.dispose();
            setForeground(isSelected() ? Color.WHITE : new Color(80, 84, 96));
            super.paintComponent(g);
        }
    }

    private static class RoundedTextField extends JTextField {
        private final int radius = 24;

        RoundedTextField() {
            super(20);
            setOpaque(false);
            setBorder(new EmptyBorder(10, 14, 10, 14));
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setForeground(new Color(40, 40, 40));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(248, 250, 252));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.setColor(new Color(228, 233, 240));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            // handled in paintComponent
        }
    }

    private static class RoundedPasswordField extends JPasswordField {
        private final int radius = 24;

        RoundedPasswordField() {
            super(20);
            setOpaque(false);
            setBorder(new EmptyBorder(10, 14, 10, 14));
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setForeground(new Color(40, 40, 40));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(248, 250, 252));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.setColor(new Color(228, 233, 240));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            // handled above
        }
    }

    private static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color bg;

        RoundedPanel(int radius, Color bg) {
            this.radius = radius;
            this.bg = bg;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class SimpleTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            component.setBackground(isSelected ? table.getSelectionBackground() : ThemeManager.getTableBackgroundColor());
            component.setForeground(isSelected ? table.getSelectionForeground() : ThemeManager.getTextColor());
            if (component instanceof JComponent jComponent) {
                jComponent.setBorder(new EmptyBorder(10, 12, 10, 12));
            }
            JLabel label = (JLabel) component;
            String columnName = table.getColumnName(column);
            if (columnName.equals("Credits") || columnName.equals("Students Enrolled") || columnName.equals("Capacity")) {
                label.setHorizontalAlignment(SwingConstants.RIGHT);
            } else {
                label.setHorizontalAlignment(SwingConstants.LEFT);
            }
            return component;
        }
    }

    private class ButtonCellRenderer extends RoundedButton implements javax.swing.table.TableCellRenderer {
        ButtonCellRenderer(String text) {
            super(text, new Color(66, 133, 244), Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setMargin(new Insets(6, 12, 6, 12));
            setPreferredSize(new Dimension(90, 30));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    private class ButtonCellEditor extends AbstractCellEditor implements javax.swing.table.TableCellEditor, ActionListener {
        private final JTable table;
        private final java.util.function.IntConsumer handler;
        private final RoundedButton button;
        private int viewRow;

        ButtonCellEditor(JTable table, String label, java.util.function.IntConsumer handler) {
            this.table = table;
            this.handler = handler;
            this.button = new RoundedButton(label, new Color(66, 133, 244), Color.WHITE);
            this.button.setFont(new Font("Segoe UI", Font.BOLD, 12));
            this.button.setMargin(new Insets(6, 12, 6, 12));
            this.button.setPreferredSize(new Dimension(90, 30));
            this.button.addActionListener(this);
        }

        @Override
        public Object getCellEditorValue() {
            return button.getText();
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.viewRow = row;
            return button;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            handler.accept(viewRow);
            fireEditingStopped();
        }
    }

    private static class RoundedButton extends JButton {
        private final int cornerRadius = 22;
        private final Color baseColor;
        private final Color textColor;

        RoundedButton(String text, Color baseColor, Color textColor) {
            super(text);
            this.baseColor = baseColor;
            this.textColor = textColor;
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setMargin(new Insets(10, 22, 10, 22));
            setForeground(textColor);
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
            g2.setColor(new Color(0, 0, 0, 25));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
            g2.dispose();
            setForeground(textColor);
            super.paintComponent(g);
        }
    }

    private static class RoundedToggleButton extends JToggleButton {
        private final int radius = 28;

        RoundedToggleButton() {
            super();
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setForeground(Color.WHITE);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setMargin(new Insets(12, 20, 12, 20));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color base = isSelected() ? new Color(66, 176, 172) : new Color(100, 100, 100);
            if (getModel().isRollover()) {
                base = base.brighter();
            }
            g2.setColor(base);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private enum MessageType {
        SUCCESS("Success", new Color(76, 175, 80), Color.WHITE),
        ERROR("Action Needed", new Color(220, 53, 69), Color.WHITE),
        INFO("Heads Up", new Color(66, 176, 172), Color.WHITE);

        final String title;
        final Color background;
        final Color foreground;

        MessageType(String title, Color background, Color foreground) {
            this.title = title;
            this.background = background;
            this.foreground = foreground;
        }
    }
}
