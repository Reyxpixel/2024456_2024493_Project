package ui;

import db.erpDB;
import model.*;
import service.AuthService;
import service.ErpService;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StudentDashboard extends JFrame {
    private static final Color BG_LIGHT = new Color(240, 242, 245);
    private static final Color PANEL_ACCENT = new Color(0x42, 0xB0, 0xAC);
    private static final Color BUTTON_DEFAULT = new Color(235, 238, 243);
    private static final Color BUTTON_TEXT = new Color(60, 60, 60);
    private static final String[] NAV_ITEMS = {
            "Home", "Course Catalog", "My Registrations", "Timetable", "Grades"
    };

    private final User user;
    private final erpDB erpDb;
    private Student student;
    private Image headerBackgroundImage;

    private JLabel coursesCountLabel;
    private JLabel creditsCountLabel;
    private JTable catalogTable;
    private JTable registrationsTable;
    private JTable timetableTable;
    private JTable gradesTable;

    private CardLayout contentLayout;
    private JPanel contentPanel;
    private final Map<String, NavigationButton> navButtons = new LinkedHashMap<>();

    public StudentDashboard(User user) {
        this.user = user;
        this.erpDb = new erpDB();
        String username = user.getUsername();
        
        // Try multiple lookup strategies
        this.student = ErpService.getStudentByEmail(username);
        
        if (this.student == null) {
            // Try to find student by matching username with email prefix or name
            List<Student> allStudents = erpDb.getAllStudents();
            for (Student s : allStudents) {
                String email = s.getEmail() != null ? s.getEmail().toLowerCase() : "";
                String name = s.getName() != null ? s.getName().toLowerCase() : "";
                String usernameLower = username.toLowerCase();
                
                // Check if username matches email exactly, email prefix, or name
                if (email.equals(usernameLower) || 
                    email.startsWith(usernameLower + "@") ||
                    name.equals(usernameLower)) {
                    this.student = s;
                    break;
                }
            }
        }
        
        if (this.student == null) {
            JOptionPane.showMessageDialog(null, "Student profile not found. Please contact administrator.");
            dispose();
            new LoginScreen().setVisible(true);
            return;
        }

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
        setTitle("Student Dashboard - " + student.getName());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 760);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_LIGHT);

        JPanel northWrapper = new JPanel(new BorderLayout());
        northWrapper.setOpaque(false);
        northWrapper.add(createHeader(), BorderLayout.NORTH);
        northWrapper.add(createNavigationBar(), BorderLayout.SOUTH);
        add(northWrapper, BorderLayout.NORTH);

        contentLayout = new CardLayout();
        contentPanel = new JPanel(contentLayout);
        contentPanel.setOpaque(false);
        contentPanel.add(createHomePanel(), "Home");
        contentPanel.add(createCatalogPanel(), "Course Catalog");
        contentPanel.add(createRegistrationsPanel(), "My Registrations");
        contentPanel.add(createTimetablePanel(), "Timetable");
        contentPanel.add(createGradesPanel(), "Grades");
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

        JLabel title = new JLabel("IIITD ERP • Student Portal");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        title.setVerticalAlignment(SwingConstants.CENTER);

        JLabel subtitle = new JLabel("Welcome, " + student.getName());
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
        nav.setBackground(Color.WHITE);
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
        if (key.equals("My Registrations") || key.equals("Timetable") || key.equals("Grades")) {
            refreshAllData();
        }
    }

    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_LIGHT);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel stats = new JPanel(new GridLayout(1, 2, 20, 0));
        stats.setOpaque(false);

        coursesCountLabel = new JLabel("-");
        creditsCountLabel = new JLabel("-");

        stats.add(createStatCard("Registered Courses", coursesCountLabel));
        stats.add(createStatCard("Total Credits", creditsCountLabel));

        panel.add(stats, BorderLayout.NORTH);
        return panel;
    }

    private JPanel createStatCard(String label, JLabel valueLabel) {
        RoundedPanel card = new RoundedPanel(28, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(18, 22, 18, 22));

        JPanel accent = new JPanel();
        accent.setBackground(PANEL_ACCENT);
        accent.setPreferredSize(new Dimension(60, 4));
        card.add(accent, BorderLayout.NORTH);

        JLabel title = new JLabel(label);
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        title.setForeground(new Color(105, 110, 120));
        title.setBorder(new EmptyBorder(10, 0, 0, 0));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setForeground(PANEL_ACCENT.darker());

        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.add(title, BorderLayout.NORTH);
        content.add(valueLabel, BorderLayout.CENTER);
        card.add(content, BorderLayout.CENTER);

        return card;
    }

    private JPanel createCatalogPanel() {
        DefaultTableModel model = new DefaultTableModel(new String[]{"Course Code", "Title", "Credits", "Section", "Instructor", "Capacity", "Available", "Register"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7;
            }
        };
        catalogTable = new JTable(model);
        styleTable(catalogTable);
        catalogTable.setRowHeight(34);
        attachButtonColumn(catalogTable, 7, "Register", this::handleRegisterAction);

        JButton refreshBtn = createButton("Refresh");
        refreshBtn.addActionListener(e -> loadCatalog());

        JPanel actions = createActionBar(refreshBtn);
        return buildModulePanel(actions, catalogTable);
    }

    private JPanel createRegistrationsPanel() {
        DefaultTableModel model = new DefaultTableModel(new String[]{"Course Code", "Title", "Credits", "Section", "Instructor", "Room", "Timetable", "Drop"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7;
            }
        };
        registrationsTable = new JTable(model);
        styleTable(registrationsTable);
        registrationsTable.setRowHeight(34);
        attachButtonColumn(registrationsTable, 7, "Drop", this::handleDropAction);

        JButton refreshBtn = createButton("Refresh");
        refreshBtn.addActionListener(e -> loadRegistrations());

        JPanel actions = createActionBar(refreshBtn);
        return buildModulePanel(actions, registrationsTable);
    }

    private JPanel createTimetablePanel() {
        DefaultTableModel model = new DefaultTableModel(new String[]{"Day/Time", "Course Code", "Title", "Section", "Room", "Instructor"}, 0);
        timetableTable = new JTable(model);
        styleTable(timetableTable);
        timetableTable.setRowHeight(34);

        JButton refreshBtn = createButton("Refresh");
        refreshBtn.addActionListener(e -> loadTimetable());

        JPanel actions = createActionBar(refreshBtn);
        return buildModulePanel(actions, timetableTable);
    }

    private JPanel createGradesPanel() {
        DefaultTableModel model = new DefaultTableModel(new String[]{"Course Code", "Title", "Credits", "Section", "Grade", "Final Grade"}, 0);
        gradesTable = new JTable(model);
        styleTable(gradesTable);
        gradesTable.setRowHeight(34);

        JButton downloadCSVBtn = createButton("Download CSV", PANEL_ACCENT, Color.WHITE);
        downloadCSVBtn.addActionListener(e -> downloadTranscriptCSV());

        JButton downloadPDFBtn = createButton("Download PDF", PANEL_ACCENT, Color.WHITE);
        downloadPDFBtn.addActionListener(e -> downloadTranscriptPDF());

        JButton refreshBtn = createButton("Refresh");
        refreshBtn.addActionListener(e -> loadGrades());

        JPanel actions = createActionBar(refreshBtn, downloadCSVBtn, downloadPDFBtn);
        return buildModulePanel(actions, gradesTable);
    }

    private void handleRegisterAction(int viewRow) {
        if (isMaintenanceEnabled()) {
            showMessage("Registration is currently disabled due to maintenance mode.", MessageType.ERROR);
            return;
        }

        List<Section> sections = (List<Section>) catalogTable.getClientProperty("sections");
        if (sections == null || viewRow < 0) {
            return;
        }
        int modelRow = catalogTable.convertRowIndexToModel(viewRow);
        if (modelRow < 0 || modelRow >= sections.size()) {
            return;
        }
        Section section = sections.get(modelRow);
        registerForSection(section);
    }

    private void handleDropAction(int viewRow) {
        List<Enrollment> enrollments = (List<Enrollment>) registrationsTable.getClientProperty("enrollments");
        if (enrollments == null || viewRow < 0) {
            return;
        }
        int modelRow = registrationsTable.convertRowIndexToModel(viewRow);
        if (modelRow < 0 || modelRow >= enrollments.size()) {
            return;
        }
        Enrollment enrollment = enrollments.get(modelRow);
        dropSection(enrollment);
    }

    private void registerForSection(Section section) {
        // Check if already enrolled
        List<Enrollment> currentEnrollments = erpDb.getEnrollmentsByStudent(student.getId());
        for (Enrollment e : currentEnrollments) {
            if (e.getSectionId() == section.getId()) {
                showMessage("You are already registered for this section.", MessageType.INFO);
                return;
            }
        }

        // Check capacity
        int enrolled = erpDb.getEnrollmentCountForSection(section.getId());
        if (enrolled >= section.getCapacity()) {
            showMessage("This section is full. No seats available.", MessageType.ERROR);
            return;
        }

        if (confirmAction("Register for " + getCourseCode(section.getCourseId()) + " - " + section.getName() + "?")) {
            if (erpDb.createEnrollment(student.getId(), section.getId())) {
                showMessage("Successfully registered for section.", MessageType.SUCCESS);
                loadCatalog();
                loadRegistrations();
                refreshHomeStats();
            } else {
                showMessage("Unable to register. Please try again.", MessageType.ERROR);
            }
        }
    }

    private void dropSection(Enrollment enrollment) {
        Section section = erpDb.getSectionById(enrollment.getSectionId());
        Course course = section != null ? erpDb.getCourseById(section.getCourseId()) : null;
        String courseName = course != null ? course.getCode() + " - " + section.getName() : "this section";
        
        if (confirmAction("Drop " + courseName + "?")) {
            if (erpDb.deleteEnrollment(enrollment.getId())) {
                showMessage("Successfully dropped section.", MessageType.SUCCESS);
                loadRegistrations();
                loadTimetable();
                refreshHomeStats();
            } else {
                showMessage("Unable to drop section. Please try again.", MessageType.ERROR);
            }
        }
    }

    private String getCourseCode(int courseId) {
        Course course = erpDb.getCourseById(courseId);
        return course != null ? course.getCode() : "?";
    }

    private void loadCatalog() {
        DefaultTableModel model = (DefaultTableModel) catalogTable.getModel();
        model.setRowCount(0);
        List<Section> allSections = new ArrayList<>();
        List<Course> courses = erpDb.getAllCourses();
        
        for (Course course : courses) {
            List<Section> sections = erpDb.getSectionsByCourse(course.getId());
            for (Section section : sections) {
                allSections.add(section);
            }
        }
        
        catalogTable.putClientProperty("sections", allSections);
        
        for (Section section : allSections) {
            Course course = erpDb.getCourseById(section.getCourseId());
            Instructor instructor = section.getInstructorId() != null 
                    ? erpDb.getInstructorById(section.getInstructorId()) 
                    : null;
            int enrolled = erpDb.getEnrollmentCountForSection(section.getId());
            int available = section.getCapacity() - enrolled;
            
            model.addRow(new Object[]{
                    course != null ? course.getCode() : "?",
                    course != null ? course.getTitle() : "?",
                    course != null ? course.getCredits() : 0,
                    section.getName(),
                    instructor != null ? instructor.getName() : "Unassigned",
                    section.getCapacity(),
                    available,
                    "Register"
            });
        }
    }

    private void loadRegistrations() {
        DefaultTableModel model = (DefaultTableModel) registrationsTable.getModel();
        model.setRowCount(0);
        List<Enrollment> enrollments = erpDb.getEnrollmentsByStudent(student.getId());
        registrationsTable.putClientProperty("enrollments", enrollments);
        
        for (Enrollment enrollment : enrollments) {
            Section section = erpDb.getSectionById(enrollment.getSectionId());
            if (section == null) continue;
            
            Course course = erpDb.getCourseById(section.getCourseId());
            Instructor instructor = section.getInstructorId() != null 
                    ? erpDb.getInstructorById(section.getInstructorId()) 
                    : null;
            
            model.addRow(new Object[]{
                    course != null ? course.getCode() : "?",
                    course != null ? course.getTitle() : "?",
                    course != null ? course.getCredits() : 0,
                    section.getName(),
                    instructor != null ? instructor.getName() : "Unassigned",
                    section.getRoom() != null ? section.getRoom() : "-",
                    section.getTimetable() != null ? section.getTimetable() : "-",
                    "Drop"
            });
        }
    }

    private void loadTimetable() {
        DefaultTableModel model = (DefaultTableModel) timetableTable.getModel();
        model.setRowCount(0);
        List<Enrollment> enrollments = erpDb.getEnrollmentsByStudent(student.getId());
        
        for (Enrollment enrollment : enrollments) {
            Section section = erpDb.getSectionById(enrollment.getSectionId());
            if (section == null || section.getTimetable() == null || section.getTimetable().isEmpty()) {
                continue;
            }
            
            Course course = erpDb.getCourseById(section.getCourseId());
            Instructor instructor = section.getInstructorId() != null 
                    ? erpDb.getInstructorById(section.getInstructorId()) 
                    : null;
            
            // Parse timetable (assuming format like "Mon 10:00-11:00, Wed 14:00-15:00")
            String[] timeSlots = section.getTimetable().split(",");
            for (String slot : timeSlots) {
                String trimmed = slot.trim();
                model.addRow(new Object[]{
                        trimmed,
                        course != null ? course.getCode() : "?",
                        course != null ? course.getTitle() : "?",
                        section.getName(),
                        section.getRoom() != null ? section.getRoom() : "-",
                        instructor != null ? instructor.getName() : "Unassigned"
                });
            }
        }
    }

    private void loadGrades() {
        DefaultTableModel model = (DefaultTableModel) gradesTable.getModel();
        model.setRowCount(0);
        List<Enrollment> enrollments = erpDb.getEnrollmentsByStudent(student.getId());
        
        for (Enrollment enrollment : enrollments) {
            Section section = erpDb.getSectionById(enrollment.getSectionId());
            if (section == null) continue;
            
            Course course = erpDb.getCourseById(section.getCourseId());
            Grade grade = enrollment.getGradeId() != null 
                    ? erpDb.getGradeById(enrollment.getGradeId()) 
                    : null;
            
            String gradeText = grade != null ? grade.getGrade() : "-";
            String finalGrade = gradeText; // For now, using the same grade as final
            
            model.addRow(new Object[]{
                    course != null ? course.getCode() : "?",
                    course != null ? course.getTitle() : "?",
                    course != null ? course.getCredits() : 0,
                    section.getName(),
                    gradeText,
                    finalGrade
            });
        }
    }

    private void refreshHomeStats() {
        int courseCount = erpDb.getRegisteredCourseCount(student.getId());
        int totalCredits = erpDb.getTotalCreditsForStudent(student.getId());
        coursesCountLabel.setText(String.valueOf(courseCount));
        creditsCountLabel.setText(String.valueOf(totalCredits));
    }

    private void refreshAllData() {
        loadCatalog();
        loadRegistrations();
        loadTimetable();
        loadGrades();
        refreshHomeStats();
    }

    private boolean isMaintenanceEnabled() {
        Settings setting = erpDb.getSetting("maintenance_mode");
        return setting != null && "ON".equalsIgnoreCase(setting.getValue());
    }

    private void downloadTranscriptCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Transcript as CSV");
        fileChooser.setSelectedFile(new File(student.getName().replaceAll(" ", "_") + "_transcript.csv"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("Course Code,Title,Credits,Section,Grade,Final Grade\n");
                
                List<Enrollment> enrollments = erpDb.getEnrollmentsByStudent(student.getId());
                for (Enrollment enrollment : enrollments) {
                    Section section = erpDb.getSectionById(enrollment.getSectionId());
                    if (section == null) continue;
                    
                    Course course = erpDb.getCourseById(section.getCourseId());
                    Grade grade = enrollment.getGradeId() != null 
                            ? erpDb.getGradeById(enrollment.getGradeId()) 
                            : null;
                    
                    String gradeText = grade != null ? grade.getGrade() : "-";
                    writer.write(String.format("%s,%s,%d,%s,%s,%s\n",
                            course != null ? course.getCode() : "?",
                            course != null ? course.getTitle() : "?",
                            course != null ? course.getCredits() : 0,
                            section.getName(),
                            gradeText,
                            gradeText
                    ));
                }
                
                showMessage("Transcript downloaded successfully as CSV.", MessageType.SUCCESS);
            } catch (IOException e) {
                showMessage("Error saving transcript: " + e.getMessage(), MessageType.ERROR);
            }
        }
    }

    private void downloadTranscriptPDF() {
        showMessage("PDF export feature coming soon. Please use CSV export for now.", MessageType.INFO);
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
        wrapper.setBackground(BG_LIGHT);
        wrapper.setBorder(new EmptyBorder(20, 20, 20, 20));

        RoundedPanel card = new RoundedPanel(28, Color.WHITE);
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
        table.setBackground(Color.WHITE);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(66, 176, 172));
        table.setSelectionForeground(Color.WHITE);
        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 42));
        header.setBackground(new Color(245, 245, 245));
        header.setForeground(new Color(60, 60, 60));
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(220, 220, 220)),
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

    private boolean confirmAction(String message) {
        final boolean[] result = {false};
        
        JLabel label = new JLabel(message);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(60, 60, 60));
        label.setBorder(new EmptyBorder(20, 25, 20, 25));
        
        JButton yesBtn = createButton("Yes", PANEL_ACCENT, Color.WHITE);
        JButton noBtn = createButton("No");
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        buttonPanel.add(noBtn);
        buttonPanel.add(yesBtn);
        
        JPanel dialogPanel = new JPanel(new BorderLayout());
        dialogPanel.setBackground(Color.WHITE);
        dialogPanel.add(label, BorderLayout.CENTER);
        dialogPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        JDialog dialog = new JDialog(this, "Please Confirm", true);
        dialog.setContentPane(dialogPanel);
        dialog.setResizable(false);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        
        yesBtn.addActionListener(e -> {
            result[0] = true;
            dialog.setVisible(false);
            dialog.dispose();
        });
        noBtn.addActionListener(e -> {
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

    private static class SimpleTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            component.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            component.setForeground(isSelected ? table.getSelectionForeground() : new Color(50, 50, 50));
            if (component instanceof JComponent jComponent) {
                jComponent.setBorder(new EmptyBorder(10, 12, 10, 12));
            }
            JLabel label = (JLabel) component;
            String columnName = table.getColumnName(column);
            if (columnName.equals("Credits") || columnName.equals("Capacity") || columnName.equals("Available")) {
                label.setHorizontalAlignment(SwingConstants.RIGHT);
            } else {
                label.setHorizontalAlignment(SwingConstants.LEFT);
            }
            return component;
        }
    }

    private class ButtonCellRenderer extends RoundedButton implements TableCellRenderer {
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

    private class ButtonCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
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
