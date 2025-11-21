package ui;

import db.erpDB;
<<<<<<< HEAD
import model.Course;
import model.Enrollment;
import model.Instructor;
import model.Section;
import model.Settings;
import model.Student;
import model.User;
=======
import model.*;
>>>>>>> a19db90af34c1b2d4bba788ca824308a2bab989f
import service.AuthService;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.ComboBoxModel;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicSpinnerUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
<<<<<<< HEAD
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntConsumer;
=======
import javax.imageio.ImageIO;
import javax.swing.table.JTableHeader;
import java.util.List;
import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.*;
>>>>>>> a19db90af34c1b2d4bba788ca824308a2bab989f

public class AdminDashboard extends JFrame {
    private static final Color BG_LIGHT = new Color(240, 242, 245);
    private static final Color PANEL_ACCENT = new Color(0x42, 0xB0, 0xAC);
    private static final Color BUTTON_DEFAULT = new Color(235, 238, 243);
    private static final Color BUTTON_TEXT = new Color(60, 60, 60);
    private static final String[] NAV_ITEMS = {
            "Dashboard", "Courses", "Instructors", "Sections", "Students", "Statistics"
    };

    private final User user;
    private final erpDB erpDb;

    private JTable courseTable;
    private JTable studentTable;
    private JTable instructorTable;
    private JTable sectionsTable;

    private JLabel studentCountLabel;
    private JLabel instructorCountLabel;
    private JLabel courseCountLabel;
    private JLabel maintenanceStatusLabel;
    private RoundedToggleButton maintenanceToggle;
    private boolean updatingMaintenanceToggle = false;
    private Image headerBackgroundImage;

    private CardLayout contentLayout;
    private JPanel contentPanel;
    private final Map<String, NavigationButton> navButtons = new LinkedHashMap<>();

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
        contentPanel.add(createHomePanel(), "Dashboard");
        contentPanel.add(createCoursesPanel(), "Courses");
        contentPanel.add(createInstructorsPanel(), "Instructors");
        contentPanel.add(createSectionsPanel(), "Sections");
        contentPanel.add(createStudentsPanel(), "Students");
        contentPanel.add(createStatisticsPanel(), "Statistics");
        add(contentPanel, BorderLayout.CENTER);

        setActiveSection("Dashboard");
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

        JLabel title = new JLabel("IIITD ERP • Admin");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        title.setVerticalAlignment(SwingConstants.CENTER);

        JLabel subtitle = new JLabel("Welcome, " + user.getUsername());
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

        maintenanceStatusLabel = new JLabel("Portal status unknown");
        maintenanceStatusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        maintenanceStatusLabel.setForeground(Color.WHITE);
        maintenanceStatusLabel.setVerticalAlignment(SwingConstants.CENTER);

        maintenanceToggle = new RoundedToggleButton();
        maintenanceToggle.addActionListener(e -> {
            if (!updatingMaintenanceToggle) {
                setMaintenanceMode(maintenanceToggle.isSelected());
            }
        });

        JPanel maintenancePanel = new JPanel();
        maintenancePanel.setOpaque(false);
        maintenancePanel.setLayout(new BoxLayout(maintenancePanel, BoxLayout.X_AXIS));
        maintenancePanel.add(Box.createHorizontalGlue());
        maintenancePanel.add(maintenanceStatusLabel);
        maintenancePanel.add(Box.createHorizontalStrut(12));
        maintenancePanel.add(maintenanceToggle);
        maintenancePanel.add(Box.createHorizontalGlue());

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
        header.add(maintenancePanel, BorderLayout.CENTER);
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
    }

    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_LIGHT);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel stats = new JPanel(new GridLayout(1, 3, 20, 0));
        stats.setOpaque(false);

        studentCountLabel = new JLabel("-");
        instructorCountLabel = new JLabel("-");
        courseCountLabel = new JLabel("-");

        stats.add(createStatCard("Total Students", studentCountLabel, "Students"));
        stats.add(createStatCard("Total Instructors", instructorCountLabel, "Instructors"));
        stats.add(createStatCard("Total Courses", courseCountLabel, "Courses"));

        panel.add(stats, BorderLayout.NORTH);
        return panel;
    }

<<<<<<< HEAD
    private JPanel createCoursesPanel() {
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Code", "Title", "Credits", "Manage"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };
        courseTable = new JTable(model);
        styleTable(courseTable);
        courseTable.setRowHeight(34);
        attachButtonColumn(courseTable, 4, "Edit", this::handleCourseEditAction);

        JButton addBtn = createButton("Add Course");
        addBtn.addActionListener(e -> openCourseDialog(null));

        JButton deleteBtn = createButton("Delete Selected");
        deleteBtn.addActionListener(e -> deleteCourse());

        JButton sectionBtn = createButton("Sections", PANEL_ACCENT, Color.WHITE);
        sectionBtn.addActionListener(e -> {
            Course selected = getSelectedCourse();
            if (selected != null) {
                openCourseSectionsDialog(selected);
            }
        });

        JButton refreshBtn = createButton("Refresh");
        refreshBtn.addActionListener(e -> loadCourses());

        JPanel actions = createActionBar(addBtn, deleteBtn, sectionBtn, refreshBtn);
        return buildModulePanel(actions, courseTable);
    }
=======
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
        editBtn.addActionListener(e -> { Course selected = getSelectedCourse();
            if (selected != null) { openCourseDialog(selected); } });
        JButton deleteBtn = createButton("Delete Selected", new Color(231, 76, 60));
        deleteBtn.addActionListener(e -> deleteCourse());
        JButton refreshBtn = createButton("Refresh", new Color(40, 167, 69));
        refreshBtn.addActionListener(e -> loadCourses());
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        actions.add(addBtn); actions.add(editBtn);
        actions.add(deleteBtn); actions.add(refreshBtn);
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(new EmptyBorder(10, 10, 10, 10));
        wrapper.add(actions, BorderLayout.NORTH);
        wrapper.add(new JScrollPane(courseTable), BorderLayout.CENTER);
        return wrapper; }
//    private JPanel createCoursesPanel() {
//        JPanel panel = new JPanel(new BorderLayout());
//
//        JLabel title = new JLabel("Manage Courses", SwingConstants.CENTER);
//        title.setFont(new Font("Arial", Font.BOLD, 18));
//        panel.add(title, BorderLayout.NORTH);
//
//        // Table model
//        String[] columnNames = {"ID", "Name", "Instructor", "Program", "Edit", "Delete"};
//        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
//            @Override
//            public boolean isCellEditable(int row, int column) {
//                return column == 4 || column == 5; // Edit & Delete only
//            }
//        };
//
//        JTable table = new JTable(model);
//        table.setRowHeight(30);
//
//        // Button renderer/editor for Edit column
//        table.getColumn("Edit").setCellRenderer(new ButtonRenderer());
//        table.getColumn("Edit").setCellEditor(new ButtonEditor(new JCheckBox(), (row) -> {
//            int courseId = (int) model.getValueAt(row, 0);
//            editCourse(courseId);
//        }));
//
//        // Button renderer/editor for Delete column
//        table.getColumn("Delete").setCellRenderer(new ButtonRenderer());
//        table.getColumn("Delete").setCellEditor(new ButtonEditor(new JCheckBox(), (row) -> {
//            int courseId = (int) model.getValueAt(row, 0);
//            deleteCourse(courseId);
//            refreshCourseTable(model);
//        }));
//
//        // Load initial data
//        refreshCourseTable(model);
//
//        panel.add(new JScrollPane(table), BorderLayout.CENTER);
//
//        return panel;
//    }


>>>>>>> a19db90af34c1b2d4bba788ca824308a2bab989f

    private JPanel createStudentsPanel() {
        studentTable = buildTable(new String[]{"ID", "Name", "Email", "Enrolled Courses"});

        JButton addBtn = createButton("Add Student");
        addBtn.addActionListener(e -> openStudentRegistrationDialog());

        JButton editBtn = createButton("Edit Selected");
        editBtn.addActionListener(e -> {
            Student student = getSelectedStudent();
            if (student != null) {
                openStudentEditDialog(student);
            }
        });

        JButton deleteBtn = createButton("Delete Selected");
        deleteBtn.addActionListener(e -> deleteStudent());

        JButton enrollBtn = createButton("Enroll in Course", PANEL_ACCENT, Color.WHITE);
        enrollBtn.addActionListener(e -> {
            Student student = getSelectedStudent();
            if (student != null) {
                openEnrollmentDialog(student);
            }
        });

        JPanel actions = createActionBar(addBtn, editBtn, deleteBtn, enrollBtn);
        return buildModulePanel(actions, studentTable);
    }

    private JPanel createInstructorsPanel() {
        instructorTable = buildTable(new String[]{"ID", "Name", "Email", "Department", "Assigned Courses"});

        JButton addBtn = createButton("Add Instructor");
        addBtn.addActionListener(e -> openInstructorRegistrationDialog());

        JButton editBtn = createButton("Edit Selected");
        editBtn.addActionListener(e -> {
            Instructor instructor = getSelectedInstructor();
            if (instructor != null) {
                openInstructorEditDialog(instructor);
            }
        });

        JButton deleteBtn = createButton("Delete Selected");
        deleteBtn.addActionListener(e -> deleteInstructor());

        JPanel actions = createActionBar(addBtn, editBtn, deleteBtn);
        return buildModulePanel(actions, instructorTable);
    }

    private JPanel createSectionsPanel() {
        sectionsTable = buildTable(new String[]{"Section ID", "Course Code", "Section", "Instructor", "Capacity", "Students"});

        JButton addBtn = createButton("Add Section");
        addBtn.addActionListener(e -> {
            if (openSectionForm(null, null)) {
                loadSections();
            }
        });

        JButton editBtn = createButton("Edit Selected");
        editBtn.addActionListener(e -> {
            Section section = getSelectedSection();
            if (section != null && openSectionForm(section, null)) {
                loadSections();
            }
        });

        JButton assignBtn = createButton("Assign Instructor", PANEL_ACCENT, Color.WHITE);
        assignBtn.addActionListener(e -> {
            Section section = getSelectedSection();
            if (section != null) {
                openAssignInstructorDialog(section);
            }
        });

        JButton viewStudentsBtn = createButton("View Students");
        viewStudentsBtn.addActionListener(e -> {
            Section section = getSelectedSection();
            if (section != null) {
                openSectionStudentsDialog(section);
            }
        });

        JButton deleteBtn = createButton("Delete Selected");
        deleteBtn.addActionListener(e -> {
            Section section = getSelectedSection();
            if (section != null) {
                deleteSection(section);
            }
        });

        JPanel actions = createActionBar(addBtn, editBtn, assignBtn, viewStudentsBtn, deleteBtn);
        return buildModulePanel(actions, sectionsTable);
    }

    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_LIGHT);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel content = new JPanel(new GridLayout(2, 2, 20, 20));
        content.setOpaque(false);

        content.add(createCourseEnrollmentStats());
        content.add(createInstructorLoadStats());
        content.add(createSectionCapacityStats());
        content.add(createOverallStats());

        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createCourseEnrollmentStats() {
        RoundedPanel card = new RoundedPanel(28, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Course Enrollments");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(new Color(50, 50, 50));
        title.setBorder(new EmptyBorder(0, 0, 15, 0));

        JTable table = buildTable(new String[]{"Course", "Sections", "Enrolled"});
        loadCourseEnrollmentStats(table);

        card.add(title, BorderLayout.NORTH);
        card.add(new JScrollPane(table), BorderLayout.CENTER);
        return card;
    }

    private JPanel createInstructorLoadStats() {
        RoundedPanel card = new RoundedPanel(28, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Instructor Loads");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(new Color(50, 50, 50));
        title.setBorder(new EmptyBorder(0, 0, 15, 0));

        JTable table = buildTable(new String[]{"Instructor", "Sections", "Students"});
        loadInstructorLoadStats(table);

        card.add(title, BorderLayout.NORTH);
        card.add(new JScrollPane(table), BorderLayout.CENTER);
        return card;
    }

    private JPanel createSectionCapacityStats() {
        RoundedPanel card = new RoundedPanel(28, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Section Capacity");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(new Color(50, 50, 50));
        title.setBorder(new EmptyBorder(0, 0, 15, 0));

        JTable table = buildTable(new String[]{"Section", "Capacity", "Enrolled", "Available"});
        loadSectionCapacityStats(table);

        card.add(title, BorderLayout.NORTH);
        card.add(new JScrollPane(table), BorderLayout.CENTER);
        return card;
    }

    private JPanel createOverallStats() {
        RoundedPanel card = new RoundedPanel(28, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Overall Statistics");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(new Color(50, 50, 50));
        title.setBorder(new EmptyBorder(0, 0, 15, 0));

        int totalStudents = erpDb.getStudentCount();
        int totalInstructors = erpDb.getInstructorCount();
        int totalCourses = erpDb.getCourseCount();
        int totalSections = erpDb.getAllSections().size();
        int totalEnrollments = erpDb.getAllEnrollments().size();

        JPanel stats = new JPanel(new GridLayout(5, 1, 0, 10));
        stats.setOpaque(false);
        stats.add(createStatRow("Total Students", String.valueOf(totalStudents)));
        stats.add(createStatRow("Total Instructors", String.valueOf(totalInstructors)));
        stats.add(createStatRow("Total Courses", String.valueOf(totalCourses)));
        stats.add(createStatRow("Total Sections", String.valueOf(totalSections)));
        stats.add(createStatRow("Total Enrollments", String.valueOf(totalEnrollments)));

        card.add(title, BorderLayout.NORTH);
        card.add(stats, BorderLayout.CENTER);
        return card;
    }

    private JPanel createStatRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        labelComp.setForeground(new Color(100, 100, 100));
        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Segoe UI", Font.BOLD, 14));
        valueComp.setForeground(new Color(50, 50, 50));
        row.add(labelComp, BorderLayout.WEST);
        row.add(valueComp, BorderLayout.EAST);
        return row;
    }

    private void loadCourseEnrollmentStats(JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        List<Course> courses = erpDb.getAllCourses();
        for (Course course : courses) {
            List<Section> sections = erpDb.getSectionsByCourse(course.getId());
            int totalEnrolled = 0;
            for (Section section : sections) {
                totalEnrolled += erpDb.getEnrollmentCountForSection(section.getId());
            }
            model.addRow(new Object[]{
                course.getCode() + " • " + course.getTitle(),
                sections.size(),
                totalEnrolled
            });
        }
    }

    private void loadInstructorLoadStats(JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        List<Instructor> instructors = erpDb.getAllInstructors();
        for (Instructor instructor : instructors) {
            List<Section> sections = erpDb.getAllSections().stream()
                .filter(s -> s.getInstructorId() != null && s.getInstructorId().equals(instructor.getId()))
                .toList();
            int totalStudents = 0;
            for (Section section : sections) {
                totalStudents += erpDb.getEnrollmentCountForSection(section.getId());
            }
            model.addRow(new Object[]{
                instructor.getName(),
                sections.size(),
                totalStudents
            });
        }
    }

    private void loadSectionCapacityStats(JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        List<Section> sections = erpDb.getAllSections();
        for (Section section : sections) {
            Course course = erpDb.getCourseById(section.getCourseId());
            int enrolled = erpDb.getEnrollmentCountForSection(section.getId());
            int available = section.getCapacity() - enrolled;
            model.addRow(new Object[]{
                (course != null ? course.getCode() : "?") + " • " + section.getName(),
                section.getCapacity(),
                enrolled,
                available
            });
        }
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

    private JPanel createStatCard(String label, JLabel valueLabel, String targetCard) {
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

        if (targetCard != null) {
            card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    setActiveSection(targetCard);
                }
            });
        }
        return card;
    }

    private JPanel createActionBar(JButton... buttons) {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        actions.setOpaque(false);
        for (JButton button : buttons) {
            actions.add(button);
        }
        return actions;
    }

    private JTable buildTable(String[] headers) {
        DefaultTableModel model = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
<<<<<<< HEAD
        styleTable(table);
        return table;
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
        header.setDefaultRenderer(new HeaderRenderer());
    }

    private void attachButtonColumn(JTable table, int columnIndex, String label, IntConsumer handler) {
        TableColumn column = table.getColumnModel().getColumn(columnIndex);
        column.setCellRenderer(new ButtonCellRenderer(label));
        column.setCellEditor(new ButtonCellEditor(table, label, handler));
        column.setPreferredWidth(100);
        column.setMinWidth(90);
        column.setMaxWidth(110);
    }
=======
        table.setRowHeight(36); // increased row height
        table.setFillsViewportHeight(true);

        // Bigger font for cells
        Font cellFont = new Font("Segoe UI", Font.PLAIN, 14);
        table.setFont(cellFont);
        table.setGridColor(new Color(230, 230, 230));

        // Header font slightly larger and bold
        JTableHeader hdr = table.getTableHeader();
        hdr.setFont(new Font("Segoe UI", Font.BOLD, 14));
        hdr.setPreferredSize(new Dimension(hdr.getPreferredSize().width, 36));

        return table;
    }

>>>>>>> a19db90af34c1b2d4bba788ca824308a2bab989f

    private void refreshAllData() {
        loadCourses();
        loadInstructors();
        loadSections();
        loadStudents();
        refreshStats();
    }

    private void loadCourses() {
        DefaultTableModel model = (DefaultTableModel) courseTable.getModel();
        model.setRowCount(0);
        List<Course> courses = erpDb.getAllCourses();
        courseTable.putClientProperty("courses", courses);
        for (Course course : courses) {

            // Try to fetch instructor name and section count. If your ERPDB doesn't yet
            // provide these helpers, they must be implemented. For now we try-catch so UI won't crash.
            String instructorName = "";
            int sectionsCount = 0;

            try {
                // These helper methods are expected on erpDb. If absent, they'll throw / return defaults.
                // I can implement these in ERPDB when you confirm.
                instructorName = erpDb.getInstructorNameForCourse(course.getId()); // String
            } catch (Exception ex) {
                instructorName = ""; // placeholder until backend exists
            }

            try {
                sectionsCount = erpDb.getSectionCountForCourse(course.getId()); // int
            } catch (Exception ex) {
                sectionsCount = 0; // placeholder until backend exists
            }

            model.addRow(new Object[]{
                    course.getId(),
                    course.getCode(),
                    course.getTitle(),
                    course.getCredits(),
<<<<<<< HEAD
                    "Edit"
=======
                    instructorName,
                    sectionsCount,
                    "⋯" // placeholder for the show-more button cell
>>>>>>> a19db90af34c1b2d4bba788ca824308a2bab989f
            });
        }
    }


    private void loadStudents() {
        DefaultTableModel model = (DefaultTableModel) studentTable.getModel();
        model.setRowCount(0);
        List<Student> students = erpDb.getAllStudents();
        studentTable.putClientProperty("students", students);
        for (Student student : students) {
            List<String> enrolled = erpDb.getCourseCodesForStudent(student.getId());
            String enrolledLabel = enrolled.isEmpty() ? "-" : String.join(", ", enrolled);
            model.addRow(new Object[]{
                    student.getId(),
                    student.getName(),
                    student.getEmail(),
                    enrolledLabel
            });
        }
    }

    private void loadInstructors() {
        DefaultTableModel model = (DefaultTableModel) instructorTable.getModel();
        model.setRowCount(0);
        List<Instructor> instructors = erpDb.getAllInstructors();
        instructorTable.putClientProperty("instructors", instructors);
        for (Instructor instructor : instructors) {
            List<String> assigned = erpDb.getCourseCodesForInstructor(instructor.getId());
            String assignedLabel = assigned.isEmpty() ? "-" : String.join(", ", assigned);
            model.addRow(new Object[]{
                    instructor.getId(),
                    instructor.getName(),
                    instructor.getEmail(),
                    instructor.getDepartment(),
                    assignedLabel
            });
        }
    }

    private void loadSections() {
        if (sectionsTable == null) {
            return;
        }
        DefaultTableModel model = (DefaultTableModel) sectionsTable.getModel();
        model.setRowCount(0);
        List<Section> sections = erpDb.getAllSections();
        sectionsTable.putClientProperty("sections", sections);
        for (Section section : sections) {
            Course course = erpDb.getCourseById(section.getCourseId());
            Instructor instructor = section.getInstructorId() != null
                    ? erpDb.getInstructorById(section.getInstructorId())
                    : null;
            int enrolled = erpDb.getEnrollmentCountForSection(section.getId());
            model.addRow(new Object[]{
                    section.getId(),
                    course != null ? course.getCode() : "-",
                    section.getName(),
                    instructor != null ? instructor.getName() : "Unassigned",
                    section.getCapacity(),
                    enrolled
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
            showMessage("Unable to update maintenance mode.", MessageType.ERROR);
        }
        refreshStats();
    }

    private void handleCourseEditAction(int viewRow) {
        List<Course> courses = (List<Course>) courseTable.getClientProperty("courses");
        if (courses == null || viewRow < 0) {
            return;
        }
        int modelRow = courseTable.convertRowIndexToModel(viewRow);
        if (modelRow < 0 || modelRow >= courses.size()) {
            return;
        }
        openCourseDialog(courses.get(modelRow));
    }

    private Course getSelectedCourse() {
        int viewRow = courseTable.getSelectedRow();
        if (viewRow < 0) {
            showMessage("Select a course first.", MessageType.INFO);
            return null;
        }
        List<Course> courses = (List<Course>) courseTable.getClientProperty("courses");
        if (courses == null) {
            return null;
        }
        int modelRow = courseTable.convertRowIndexToModel(viewRow);
        if (modelRow < 0 || modelRow >= courses.size()) {
            return null;
        }
        return courses.get(modelRow);
    }

    private Student getSelectedStudent() {
        int viewRow = studentTable.getSelectedRow();
        if (viewRow < 0) {
            showMessage("Select a student first.", MessageType.INFO);
            return null;
        }
        List<Student> students = (List<Student>) studentTable.getClientProperty("students");
        if (students == null) {
            return null;
        }
        int modelRow = studentTable.convertRowIndexToModel(viewRow);
        if (modelRow < 0 || modelRow >= students.size()) {
            return null;
        }
        return students.get(modelRow);
    }

    private Instructor getSelectedInstructor() {
        int viewRow = instructorTable.getSelectedRow();
        if (viewRow < 0) {
            showMessage("Select an instructor first.", MessageType.INFO);
            return null;
        }
        List<Instructor> instructors = (List<Instructor>) instructorTable.getClientProperty("instructors");
        if (instructors == null) {
            return null;
        }
        int modelRow = instructorTable.convertRowIndexToModel(viewRow);
        if (modelRow < 0 || modelRow >= instructors.size()) {
            return null;
        }
        return instructors.get(modelRow);
    }

    private Section getSelectedSection() {
        if (sectionsTable == null) {
            showMessage("Sections panel is not ready yet.", MessageType.INFO);
            return null;
        }
        int viewRow = sectionsTable.getSelectedRow();
        if (viewRow < 0) {
            showMessage("Select a section first.", MessageType.INFO);
            return null;
        }
        List<Section> sections = (List<Section>) sectionsTable.getClientProperty("sections");
        if (sections == null) {
            return null;
        }
        int modelRow = sectionsTable.convertRowIndexToModel(viewRow);
        if (modelRow < 0 || modelRow >= sections.size()) {
            return null;
        }
        return sections.get(modelRow);
    }

    private void openCourseDialog(Course current) {
        RoundedTextField codeField = createInputField(current != null ? current.getCode() : "");
        RoundedTextField titleField = createInputField(current != null ? current.getTitle() : "");
        RoundedTextField creditsField = createInputField(current != null ? String.valueOf(current.getCredits()) : "4");

        JPanel form = createFormPanel();
        GridBagConstraints gbc = createFormConstraints();
        addFormHeading(form, gbc, current == null ? "Add Course" : "Edit Course");
        addFormRow(form, gbc, "Course Code", codeField);
        addFormRow(form, gbc, "Title", titleField);
        addFormRow(form, gbc, "Credits", creditsField);

        if (current != null) {
            JButton manageSectionsBtn = createButton("Manage Sections", PANEL_ACCENT, Color.WHITE);
            manageSectionsBtn.addActionListener(e -> openCourseSectionsDialog(current));
            addFormRow(form, gbc, "Sections", manageSectionsBtn);
        }

        if (!showFormDialog(current == null ? "Add Course" : "Edit Course", form)) {
            return;
        }

        String code = codeField.getText().trim();
        String title = titleField.getText().trim();
        int credits;
        try {
            credits = Integer.parseInt(creditsField.getText().trim());
            if (credits < 1 || credits > 20) {
                showMessage("Credits must be between 1 and 20.", MessageType.ERROR);
                return;
            }
        } catch (NumberFormatException e) {
            showMessage("Please enter a valid number for credits.", MessageType.ERROR);
            return;
        }


        if (code.isEmpty() || title.isEmpty()) {
            showMessage("Code and title are required.", MessageType.ERROR);
            return;
        }

        boolean success = current == null
                ? erpDb.addCourse(code, title, credits)
                : erpDb.updateCourse(current.getId(), code, title, credits);

        if (!success) {
            showMessage("Unable to save course.", MessageType.ERROR);
            return;
        }

        showMessage(current == null ? "Course created successfully." : "Course updated successfully.", MessageType.SUCCESS);
        loadCourses();
        refreshStats();
    }

    private void deleteCourse() {
        Course selected = getSelectedCourse();
        if (selected == null) {
            return;
        }
        if (!confirmAction("Delete selected course?")) {
            return;
        }
        if (!erpDb.deleteCourse(selected.getId())) {
            showMessage("Unable to delete course.", MessageType.ERROR);
            return;
        }
        showMessage("Course deleted.", MessageType.SUCCESS);
        loadCourses();
        loadSections();
        refreshStats();
    }

    private void openCourseSectionsDialog(Course course) {
        JDialog dialog = new JDialog(this, "Sections • " + course.getCode(), true);
        dialog.setSize(720, 480);
        dialog.setLocationRelativeTo(this);

        JTable table = buildTable(new String[]{"Section", "Instructor", "Capacity", "Timetable"});
        loadCourseSections(table, course);

        JButton addBtn = createButton("Add Section");
        addBtn.addActionListener(e -> {
            if (openSectionForm(null, course)) {
                loadCourseSections(table, course);
            }
        });

        JButton editBtn = createButton("Edit Section");
        editBtn.addActionListener(e -> {
            Section section = getSectionFromCourseTable(table);
            if (section != null && openSectionForm(section, course)) {
                loadCourseSections(table, course);
            }
        });

        JButton deleteBtn = createButton("Delete Section");
        deleteBtn.addActionListener(e -> {
            Section section = getSectionFromCourseTable(table);
            if (section != null) {
                deleteSection(section);
                loadCourseSections(table, course);
            }
        });

        JPanel actions = createActionBar(addBtn, editBtn, deleteBtn);

        JPanel wrapper = new JPanel(new BorderLayout(0, 12));
        wrapper.setBorder(new EmptyBorder(20, 20, 20, 20));
        wrapper.add(actions, BorderLayout.NORTH);
        wrapper.add(new JScrollPane(table), BorderLayout.CENTER);

        dialog.setContentPane(wrapper);
        dialog.setVisible(true);
    }

    @SuppressWarnings("unchecked")
    private Section getSectionFromCourseTable(JTable table) {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            showMessage("Select a section first.", MessageType.INFO);
            return null;
        }
        List<Section> sections = (List<Section>) table.getClientProperty("course_sections");
        if (sections == null) {
            return null;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);
        if (modelRow < 0 || modelRow >= sections.size()) {
            return null;
        }
        return sections.get(modelRow);
    }

    private void loadCourseSections(JTable table, Course course) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        List<Section> sections = erpDb.getSectionsByCourse(course.getId());
        table.putClientProperty("course_sections", sections);
        for (Section section : sections) {
            Instructor instructor = section.getInstructorId() != null ? erpDb.getInstructorById(section.getInstructorId()) : null;
            model.addRow(new Object[]{
                    section.getName(),
                    instructor != null ? instructor.getName() : "Unassigned",
                    section.getCapacity(),
                    section.getTimetable() == null || section.getTimetable().isEmpty() ? "-" : section.getTimetable()
            });
        }
    }

    private boolean openSectionForm(Section current, Course lockedCourse) {
        List<Course> courses = erpDb.getAllCourses();
        JComboBox<Course> courseCombo = new JComboBox<>(courses.toArray(new Course[0]));
        styleComboBox(courseCombo);
        courseCombo.setRenderer(createCourseRenderer());
        if (lockedCourse != null) {
            selectCourse(courseCombo, lockedCourse.getId());
            courseCombo.setEnabled(false);
        } else if (current != null) {
            selectCourse(courseCombo, current.getCourseId());
        }

        RoundedTextField nameField = createInputField(current != null ? current.getName() : "");
        RoundedTextField capacityField = createInputField(current != null ? String.valueOf(current.getCapacity()) : "60");

        RoundedTextField timetableField = createInputField(current != null ? valueOrDash(current.getTimetable()) : "");
        RoundedTextField semesterField = createInputField(current != null ? valueOrDash(current.getSemester()) : "");

        List<Instructor> instructors = erpDb.getAllInstructors();
        DefaultComboBoxModel<Instructor> instructorModel = new DefaultComboBoxModel<>();
        instructorModel.addElement(null);
        for (Instructor instructor : instructors) {
            instructorModel.addElement(instructor);
        }
        JComboBox<Instructor> instructorCombo = new JComboBox<>(instructorModel);
        styleComboBox(instructorCombo);
        instructorCombo.setRenderer(createInstructorRenderer());
        if (current != null) {
            selectInstructor(instructorCombo, current.getInstructorId());
        }

        JPanel form = createFormPanel();
        GridBagConstraints gbc = createFormConstraints();
        addFormHeading(form, gbc, current == null ? "Add Section" : "Edit Section");
        if (lockedCourse == null) {
            addFormRow(form, gbc, "Course", courseCombo);
        }
        addFormRow(form, gbc, "Section Name", nameField);
        addFormRow(form, gbc, "Capacity", capacityField);
        addFormRow(form, gbc, "Timetable", timetableField);
        addFormRow(form, gbc, "Semester", semesterField);
        addFormRow(form, gbc, "Instructor", instructorCombo);

        if (!showFormDialog(current == null ? "Add Section" : "Edit Section", form)) {
            return false;
        }

        Course selectedCourse = lockedCourse != null ? lockedCourse : (Course) courseCombo.getSelectedItem();
        if (selectedCourse == null) {
            showMessage("Select a course.", MessageType.ERROR);
            return false;
        }

        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            showMessage("Section name is required.", MessageType.ERROR);
            return false;
        }

        int capacity;
        try {
            capacity = Integer.parseInt(capacityField.getText().trim());
            if (capacity < 5 || capacity > 400) {
                showMessage("Capacity must be between 5 and 400.", MessageType.ERROR);
                return false;
            }
        } catch (NumberFormatException e) {
            showMessage("Please enter a valid number for capacity.", MessageType.ERROR);
            return false;
        }
        String timetable = timetableField.getText().trim();
        String semester = semesterField.getText().trim();
        Instructor instructor = (Instructor) instructorCombo.getSelectedItem();
        Integer instructorId = instructor != null ? instructor.getId() : null;

        boolean success = current == null
                ? erpDb.addSection(selectedCourse.getId(), instructorId, name, capacity, timetable, semester)
                : erpDb.updateSection(current.getId(), selectedCourse.getId(), instructorId, name, capacity, timetable, semester);

        if (!success) {
            showMessage("Unable to save section.", MessageType.ERROR);
            return false;
        }

        showMessage(current == null ? "Section created." : "Section updated.", MessageType.SUCCESS);
        loadSections();
        return true;
    }

    private void deleteSection(Section section) {
        if (!confirmAction("Delete selected section?")) {
            return;
        }
        if (!erpDb.deleteSection(section.getId())) {
            showMessage("Unable to delete section.", MessageType.ERROR);
            return;
        }
        showMessage("Section removed.", MessageType.SUCCESS);
        loadSections();
    }

    private void openAssignInstructorDialog(Section section) {
        List<Instructor> instructors = erpDb.getAllInstructors();
        DefaultComboBoxModel<Instructor> instructorModel = new DefaultComboBoxModel<>();
        instructorModel.addElement(null);
        for (Instructor instructor : instructors) {
            instructorModel.addElement(instructor);
        }
        JComboBox<Instructor> comboBox = new JComboBox<>(instructorModel);
        styleComboBox(comboBox);
        comboBox.setRenderer(createInstructorRenderer());
        selectInstructor(comboBox, section.getInstructorId());

        JPanel form = createFormPanel();
        GridBagConstraints gbc = createFormConstraints();
        addFormHeading(form, gbc, "Assign Instructor");
        addFormRow(form, gbc, "Instructor", comboBox);

        if (!showFormDialog("Assign Instructor", form)) {
            return;
        }

        Instructor instructor = (Instructor) comboBox.getSelectedItem();
        Integer instructorId = instructor != null ? instructor.getId() : null;
        if (!erpDb.assignInstructorToSection(section.getId(), instructorId)) {
            showMessage("Unable to assign instructor.", MessageType.ERROR);
            return;
        }
        showMessage("Instructor updated.", MessageType.SUCCESS);
        loadSections();
    }

    private void openSectionStudentsDialog(Section section) {
        List<Student> students = erpDb.getStudentsForSection(section.getId());
        JTable table = buildTable(new String[]{"ID", "Name", "Email", "Program"});
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        for (Student student : students) {
            model.addRow(new Object[]{student.getId(), student.getName(), student.getEmail(), student.getProgram()});
        }

        JDialog dialog = new JDialog(this, "Students • " + section.getName(), true);
        dialog.setSize(600, 420);
        dialog.setLocationRelativeTo(this);
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(new EmptyBorder(20, 20, 20, 20));
        wrapper.add(new JScrollPane(table), BorderLayout.CENTER);
        dialog.setContentPane(wrapper);
        dialog.setVisible(true);
    }

    private void openStudentRegistrationDialog() {
        RoundedTextField usernameField = createInputField(null);
        RoundedPasswordField passwordField = createPasswordField();
        RoundedTextField nameField = createInputField(null);
        RoundedTextField emailField = createInputField(null);
        RoundedTextField programField = createInputField(null);

        JPanel form = createFormPanel();
        GridBagConstraints gbc = createFormConstraints();
        addFormHeading(form, gbc, "Register Student");
        addFormRow(form, gbc, "Username", usernameField);
        addFormRow(form, gbc, "Temporary Password", passwordField);
        addFormRow(form, gbc, "Full Name", nameField);
        addFormRow(form, gbc, "Email", emailField);
        addFormRow(form, gbc, "Program", programField);

        if (!showFormDialog("Register Student", form)) {
            return;
        }

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String program = programField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || name.isEmpty() || email.isEmpty()) {
            showMessage("Please fill in all required fields.", MessageType.ERROR);
            return;
        }

        if (!AuthService.register(username, password, "student")) {
            showMessage("Unable to create login for this student.", MessageType.ERROR);
            return;
        }

        if (!erpDb.addStudent(name, email, program)) {
            AuthService.deleteUser(username);
            showMessage("Unable to save student profile.", MessageType.ERROR);
            return;
        }

        showMessage("Student registered successfully.", MessageType.SUCCESS);
        loadStudents();
        refreshStats();
    }

    private void openStudentEditDialog(Student current) {
        RoundedTextField nameField = createInputField(current.getName());
        RoundedTextField emailField = createInputField(current.getEmail());
        RoundedTextField programField = createInputField(current.getProgram());

        JPanel form = createFormPanel();
        GridBagConstraints gbc = createFormConstraints();
        addFormHeading(form, gbc, "Edit Student");
        addFormRow(form, gbc, "Full Name", nameField);
        addFormRow(form, gbc, "Email", emailField);
        addFormRow(form, gbc, "Program", programField);

        if (!showFormDialog("Edit Student", form)) {
            return;
        }

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String program = programField.getText().trim();
        if (name.isEmpty() || email.isEmpty()) {
            showMessage("Name and email are required.", MessageType.ERROR);
            return;
        }
        if (!erpDb.updateStudent(current.getId(), name, email, program)) {
            showMessage("Unable to update student.", MessageType.ERROR);
            return;
        }
        showMessage("Student updated.", MessageType.SUCCESS);
        loadStudents();
    }

    private void deleteStudent() {
        Student student = getSelectedStudent();
        if (student == null) {
            return;
        }
        if (!confirmAction("Delete selected student profile?")) {
            return;
        }
        if (!erpDb.deleteStudent(student.getId())) {
            showMessage("Unable to delete student.", MessageType.ERROR);
            return;
        }
        showMessage("Student removed.", MessageType.SUCCESS);
        loadStudents();
        refreshStats();
    }

    private void openEnrollmentDialog(Student student) {
        List<Section> sections = erpDb.getAllSections();
        if (sections.isEmpty()) {
            showMessage("No sections available for enrollment.", MessageType.INFO);
            return;
        }
        JComboBox<Section> sectionCombo = new JComboBox<>(sections.toArray(new Section[0]));
        styleComboBox(sectionCombo);
        sectionCombo.setRenderer(createSectionRenderer());

        JPanel form = createFormPanel();
        GridBagConstraints gbc = createFormConstraints();
        addFormHeading(form, gbc, "Enroll in Section");
        addFormRow(form, gbc, "Section", sectionCombo);

        if (!showFormDialog("Enroll Student", form)) {
            return;
        }

        Section target = (Section) sectionCombo.getSelectedItem();
        if (target == null) {
            showMessage("Select a section.", MessageType.ERROR);
            return;
        }

        int currentCount = erpDb.getEnrollmentCountForSection(target.getId());
        if (currentCount >= target.getCapacity()) {
            showMessage("Section is already at capacity.", MessageType.ERROR);
            return;
        }

        List<Enrollment> enrollments = erpDb.getEnrollmentsByStudent(student.getId());
        boolean alreadyEnrolled = enrollments.stream().anyMatch(e -> e.getSectionId() == target.getId());
        if (alreadyEnrolled) {
            showMessage("Student is already enrolled in this section.", MessageType.INFO);
            return;
        }

        if (!erpDb.createEnrollment(student.getId(), target.getId())) {
            showMessage("Unable to create enrollment.", MessageType.ERROR);
            return;
        }
        showMessage("Student enrolled successfully.", MessageType.SUCCESS);
        loadStudents();
        loadSections();
    }

    private void openInstructorRegistrationDialog() {
        RoundedTextField usernameField = createInputField(null);
        RoundedPasswordField passwordField = createPasswordField();
        RoundedTextField nameField = createInputField(null);
        RoundedTextField emailField = createInputField(null);
        RoundedTextField departmentField = createInputField(null);

        JPanel form = createFormPanel();
        GridBagConstraints gbc = createFormConstraints();
        addFormHeading(form, gbc, "Register Instructor");
        addFormRow(form, gbc, "Username", usernameField);
        addFormRow(form, gbc, "Temporary Password", passwordField);
        addFormRow(form, gbc, "Full Name", nameField);
        addFormRow(form, gbc, "Email", emailField);
        addFormRow(form, gbc, "Department", departmentField);

        if (!showFormDialog("Register Instructor", form)) {
            return;
        }

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String department = departmentField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || name.isEmpty() || email.isEmpty()) {
            showMessage("Please fill in all required fields.", MessageType.ERROR);
            return;
        }

        if (!AuthService.register(username, password, "instructor")) {
            showMessage("Unable to create login for this instructor.", MessageType.ERROR);
            return;
        }

        if (!erpDb.addInstructor(name, email, department)) {
            AuthService.deleteUser(username);
            showMessage("Unable to save instructor profile.", MessageType.ERROR);
            return;
        }

        showMessage("Instructor registered successfully.", MessageType.SUCCESS);
        loadInstructors();
        refreshStats();
    }

    private void openInstructorEditDialog(Instructor instructor) {
        RoundedTextField nameField = createInputField(instructor.getName());
        RoundedTextField emailField = createInputField(instructor.getEmail());
        RoundedTextField departmentField = createInputField(instructor.getDepartment());

        JPanel form = createFormPanel();
        GridBagConstraints gbc = createFormConstraints();
        addFormHeading(form, gbc, "Edit Instructor");
        addFormRow(form, gbc, "Full Name", nameField);
        addFormRow(form, gbc, "Email", emailField);
        addFormRow(form, gbc, "Department", departmentField);

        if (!showFormDialog("Edit Instructor", form)) {
            return;
        }

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String department = departmentField.getText().trim();

        if (name.isEmpty() || email.isEmpty()) {
            showMessage("Name and email are required.", MessageType.ERROR);
            return;
        }

        if (!erpDb.updateInstructor(instructor.getId(), name, email, department)) {
            showMessage("Unable to update instructor.", MessageType.ERROR);
            return;
        }

        showMessage("Instructor updated.", MessageType.SUCCESS);
        loadInstructors();
    }

    private void deleteInstructor() {
        Instructor instructor = getSelectedInstructor();
        if (instructor == null) {
            return;
        }
        if (!confirmAction("Delete selected instructor profile?")) {
            return;
        }
        if (!erpDb.deleteInstructor(instructor.getId())) {
            showMessage("Unable to delete instructor.", MessageType.ERROR);
            return;
        }
        showMessage("Instructor removed.", MessageType.SUCCESS);
        loadInstructors();
        refreshStats();
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
        combo.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = new JButton("▼");
                button.setBorderPainted(false);
                button.setContentAreaFilled(true);
                button.setFocusPainted(false);
                button.setOpaque(true);
                button.setBackground(new Color(245, 245, 245));
                button.setForeground(new Color(80, 80, 80));
                button.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                button.setPreferredSize(new Dimension(25, 20));
                button.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(200, 200, 200)));
                button.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        button.setBackground(new Color(230, 230, 230));
                    }
                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        button.setBackground(new Color(245, 245, 245));
                    }
                });
                return button;
            }
        });
    }

    private void styleSpinner(JSpinner spinner) {
        spinner.setOpaque(false);
        spinner.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(0, 8, 0, 0)
        ));
        spinner.setUI(new BasicSpinnerUI() {
            @Override
            protected Component createNextButton() {
                JButton btn = new JButton("▲");
                styleSpinnerButton(btn);
                installNextButtonListeners(btn);
                return btn;
            }
            
            @Override
            protected Component createPreviousButton() {
                JButton btn = new JButton("▼");
                styleSpinnerButton(btn);
                installPreviousButtonListeners(btn);
                return btn;
            }
        });
        
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor defaultEditor) {
            JFormattedTextField textField = defaultEditor.getTextField();
            textField.setBorder(null);
            textField.setBackground(Color.WHITE);
            textField.setForeground(new Color(50, 50, 50));
            textField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        }
    }
    
    private void styleSpinnerButton(JButton btn) {
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(true);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBackground(new Color(245, 245, 245));
        btn.setForeground(new Color(80, 80, 80));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setPreferredSize(new Dimension(20, 15));
        btn.setMargin(new Insets(0, 0, 0, 0));
        btn.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(200, 200, 200)));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(230, 230, 230));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(245, 245, 245));
            }
        });
    }

    private JComponent wrapSpinner(JSpinner spinner) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(0, 0, 0, 0));
        wrapper.add(spinner, BorderLayout.CENTER);
        return wrapper;
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

    private String valueOrDash(String value) {
        return value == null ? "" : value;
    }

    private DefaultListCellRenderer createCourseRenderer() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Course course) {
                    setText(course.getCode() + " • " + course.getTitle());
                } else if (value == null) {
                    setText("Select course");
                }
                return component;
            }
        };
    }

    private DefaultListCellRenderer createInstructorRenderer() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Instructor instructor) {
                    String dept = instructor.getDepartment() == null ? "" : " • " + instructor.getDepartment();
                    setText(instructor.getName() + dept);
                } else if (value == null) {
                    setText("Unassigned");
                }
                return component;
            }
        };
    }

    private DefaultListCellRenderer createSectionRenderer() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Section section) {
                    Course course = erpDb.getCourseById(section.getCourseId());
                    String courseCode = course != null ? course.getCode() : "Course";
                    setText(courseCode + " • " + section.getName());
                }
                return component;
            }
        };
    }

    private void selectCourse(JComboBox<Course> combo, int courseId) {
        ComboBoxModel<Course> model = combo.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            Course course = model.getElementAt(i);
            if (course != null && course.getId() == courseId) {
                combo.setSelectedIndex(i);
                break;
            }
        }
    }

    private void selectInstructor(JComboBox<Instructor> combo, Integer instructorId) {
        if (instructorId == null) {
            combo.setSelectedIndex(0);
            return;
        }
        ComboBoxModel<Instructor> model = combo.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            Instructor instructor = model.getElementAt(i);
            if (instructor != null && instructor.getId() == instructorId) {
                combo.setSelectedIndex(i);
                break;
            }
        }
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
    private JPanel createSectionsPanel() {
        JTable sectionsTable;
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Course", "Instructor", "Semester/Schedule", "Room", "Capacity", "Students", "Actions"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        sectionsTable = new JTable(model);
        sectionsTable.setRowHeight(30);

        // Actions column - reuse ButtonRenderer/ButtonEditor approach
        sectionsTable.getColumnModel().getColumn(7).setCellRenderer(new ButtonRenderer());
        sectionsTable.getColumnModel().getColumn(7).setCellEditor(new ButtonEditor(new JCheckBox(), (row) -> {
            int sectionId = (int) model.getValueAt(row, 0);
            // open small section editor dialog
            openSectionDetailsDialog(sectionId);
        }));

        JButton addBtn = createButton("Add Section", new Color(40, 167, 69));
        addBtn.addActionListener(e -> openSectionCreateDialog());

        JButton refreshBtn = createButton("Refresh", new Color(40, 167, 69));
        refreshBtn.addActionListener(e -> loadSections(model));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        actions.add(addBtn);
        actions.add(refreshBtn);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(new EmptyBorder(10, 10, 10, 10));
        wrapper.add(actions, BorderLayout.NORTH);
        wrapper.add(new JScrollPane(sectionsTable), BorderLayout.CENTER);

        // initial load
        loadSections(model);

        return wrapper;
    }
    private void loadSections(DefaultTableModel model) {
        model.setRowCount(0);
        java.util.List<Section> secs = erpDb.getAllSections();
        if (secs == null) return;
        for (Section s : secs) {
            String courseCode = "";
            try {
                Course c = erpDb.getCourseById(s.getCourseId());
                courseCode = c != null ? c.getCode() : "";
            } catch (Exception ex) {}

            String instrName = "";
            try {
                Instructor instr = erpDb.getInstructorById(s.getInstructorId());
                instrName = instr != null ? instr.getName() : "";
            } catch (Exception ex) {}

            int studentCount = erpDb.getStudentCountForSection(s.getId());

            model.addRow(new Object[]{
                    s.getId(),
                    courseCode,
                    instrName,
                    s.getSchedule(),
                    s.getRoom(),
                    s.getCapacity(),
                    studentCount,
                    "⋯"
            });
        }
    }

    private void openSectionCreateDialog() {
        // small dialog to create section
        JTextField courseIdField = new JTextField();
        JTextField scheduleField = new JTextField();
        JTextField roomField = new JTextField();
        JSpinner capacitySpinner = new JSpinner(new SpinnerNumberModel(30, 1, 500, 1));
        JPanel p = new JPanel(new GridLayout(0, 2, 8, 8));
        p.add(new JLabel("Course ID:")); p.add(courseIdField);
        p.add(new JLabel("Schedule:")); p.add(scheduleField);
        p.add(new JLabel("Room:")); p.add(roomField);
        p.add(new JLabel("Capacity:")); p.add(capacitySpinner);

        int res = JOptionPane.showConfirmDialog(this, p, "Create Section", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            try {
                int courseId = Integer.parseInt(courseIdField.getText().trim());
                String schedule = scheduleField.getText().trim();
                String room = roomField.getText().trim();
                int capacity = (int) capacitySpinner.getValue();
                boolean ok = erpDb.addSection(courseId, null, schedule, room, capacity);
                if (!ok) JOptionPane.showMessageDialog(this, "Unable to create section.", "Error", JOptionPane.ERROR_MESSAGE);
                else {
                    JOptionPane.showMessageDialog(this, "Section created.");
                    refreshAllData();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Course ID must be a number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openSectionDetailsDialog(int sectionId) {
        Section s = erpDb.getSectionById(sectionId);
        if (s == null) { JOptionPane.showMessageDialog(this, "Section not found."); return; }

        JTextField scheduleField = new JTextField(s.getSchedule());
        JTextField roomField = new JTextField(s.getRoom());
        JSpinner capacitySpinner = new JSpinner(new SpinnerNumberModel(s.getCapacity(), 1, 500, 1));

        // instructor dropdown
        java.util.List<Instructor> instructors = erpDb.getAllInstructors();
        String[] instrOptions = new String[instructors.size() + 1];
        instrOptions[0] = "Unassigned";
        for (int i = 0; i < instructors.size(); i++) {
            instrOptions[i+1] = instructors.get(i).getName() + " <" + instructors.get(i).getEmail() + ">";
        }
        JComboBox<String> instrCombo = new JComboBox<>(instrOptions);
        // try to select current instructor
        try {
            if (s.getInstructorId() > 0) {
                Instructor cur = erpDb.getInstructorById(s.getInstructorId());
                for (int i = 0; i < instructors.size(); i++) {
                    if (instructors.get(i).getId() == s.getInstructorId()) {
                        instrCombo.setSelectedIndex(i+1);
                        break;
                    }
                }
            }
        } catch (Exception ex) {}

        JPanel p = new JPanel(new GridLayout(0, 2, 8, 8));
        p.add(new JLabel("Schedule:")); p.add(scheduleField);
        p.add(new JLabel("Room:")); p.add(roomField);
        p.add(new JLabel("Capacity:")); p.add(capacitySpinner);
        p.add(new JLabel("Instructor:")); p.add(instrCombo);

        int res = JOptionPane.showConfirmDialog(this, p, "Edit Section", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            int instrId = -1;
            int sel = instrCombo.getSelectedIndex();
            if (sel > 0) instrId = instructors.get(sel-1).getId();
            boolean ok = erpDb.updateSection(s.getId(), instrId > 0 ? instrId : null, scheduleField.getText().trim(), roomField.getText().trim(), (int) capacitySpinner.getValue());
            if (!ok) JOptionPane.showMessageDialog(this, "Unable to update section.", "Error", JOptionPane.ERROR_MESSAGE);
            else {
                JOptionPane.showMessageDialog(this, "Section updated.");
                refreshAllData();
            }
        }
    }

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
            component.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            component.setForeground(isSelected ? table.getSelectionForeground() : new Color(50, 50, 50));
            if (component instanceof JComponent jComponent) {
                jComponent.setBorder(new EmptyBorder(10, 12, 10, 12));
            }
            JLabel label = (JLabel) component;
            String columnName = table.getColumnName(column);
            if (columnName.equals("ID") || columnName.equals("Credits") || columnName.equals("Capacity") || 
                columnName.equals("Students") || columnName.equals("Enrolled") || columnName.equals("Available") ||
                columnName.equals("Sections")) {
                label.setHorizontalAlignment(SwingConstants.RIGHT);
            } else {
                label.setHorizontalAlignment(SwingConstants.LEFT);
            }
            return component;
        }
    }
    
    private static class HeaderRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            component.setBackground(new Color(245, 245, 245));
            component.setForeground(new Color(60, 60, 60));
            if (component instanceof JComponent jComponent) {
                jComponent.setBorder(new EmptyBorder(10, 12, 10, 12));
            }
            ((JLabel) component).setHorizontalAlignment(SwingConstants.LEFT);
            ((JLabel) component).setFont(new Font("Segoe UI", Font.BOLD, 13));
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
        private final IntConsumer handler;
        private final RoundedButton button;
        private int viewRow;

        ButtonCellEditor(JTable table, String label, IntConsumer handler) {
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
