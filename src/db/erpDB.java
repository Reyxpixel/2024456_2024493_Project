package db;

import model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class erpDB {

    private static final String DB_URL = "jdbc:sqlite:data/erp.db";

    public erpDB() {
        String students = """
                CREATE TABLE IF NOT EXISTS students (
                    student_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    email TEXT UNIQUE NOT NULL,
                    program TEXT
                );
                """;

        String instructors = """
                CREATE TABLE IF NOT EXISTS instructors (
                    instructor_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    email TEXT UNIQUE NOT NULL,
                    department TEXT
                );
                """;

        String courses = """
                CREATE TABLE IF NOT EXISTS courses (
                    course_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    code TEXT UNIQUE NOT NULL,
                    title TEXT NOT NULL,
                    credits INTEGER
                );
                """;

        String sections = """
                CREATE TABLE IF NOT EXISTS sections (
                    section_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    course_id INTEGER NOT NULL,
                    instructor_id INTEGER,
                    name TEXT NOT NULL DEFAULT 'Section A',
                    capacity INTEGER NOT NULL DEFAULT 60,
                    room TEXT,
                    timetable TEXT,
                    FOREIGN KEY(course_id) REFERENCES courses(course_id),
                    FOREIGN KEY(instructor_id) REFERENCES instructors(instructor_id)
                );
                """;

        String enrollments = """
                CREATE TABLE IF NOT EXISTS enrollments (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    student_id INTEGER NOT NULL,
                    section_id INTEGER NOT NULL,
                    grade_id INTEGER,
                    FOREIGN KEY(student_id) REFERENCES students(student_id),
                    FOREIGN KEY(section_id) REFERENCES sections(section_id)
                );
                """;

        String grades = """
                CREATE TABLE IF NOT EXISTS grades (
                    grade_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    enroll_id INTEGER NOT NULL,
                    grade TEXT,
                    FOREIGN KEY(enroll_id) REFERENCES enrollments(id)
                );
                """;

        String settings = """
                CREATE TABLE IF NOT EXISTS settings (
                    key TEXT PRIMARY KEY,
                    value TEXT
                );
                """;

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            // Migrate first if needed (before creating new tables)
            migrateSectionsTable();
            
            stmt.execute(students);
            stmt.execute(instructors);
            stmt.execute(courses);
            stmt.execute(sections);
            stmt.execute(enrollments);
            stmt.execute(grades);
            stmt.execute(settings);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Connection connect() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL);
        // Configure SQLite to reduce locking issues
        conn.setAutoCommit(true);
        try (Statement stmt = conn.createStatement()) {
            // Enable WAL mode for better concurrency (if supported)
            try {
                stmt.execute("PRAGMA journal_mode=WAL");
            } catch (SQLException e) {
                // WAL not supported, use default
            }
            // Set busy timeout to handle locks gracefully
            stmt.execute("PRAGMA busy_timeout=3000");
        }
        return conn;
    }

    // Student methods
    public boolean addStudent(String name, String email, String program) {
        String sql = "INSERT INTO students (name, email, program) VALUES (?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, program);
            stmt.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Student getStudentById(int id) {
        String sql = "SELECT * FROM students WHERE student_id = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Student(
                            rs.getInt("student_id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("program")
                    );
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();

        String sql = "SELECT * FROM students ORDER BY name";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new Student(
                        rs.getInt("student_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("program")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateStudent(int id, String name, String email, String program) {
        String sql = "UPDATE students SET name=?, email=?, program=? WHERE student_id=?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, program);
            stmt.setInt(4, id);
            stmt.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteStudent(int id) {
        String sql = "DELETE FROM students WHERE student_id=?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Instructor methods
    public boolean addInstructor(String name, String email, String department) {
        String sql = "INSERT INTO instructors (name, email, department) VALUES (?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, department);
            stmt.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Instructor getInstructorById(int id) {
        String sql = "SELECT * FROM instructors WHERE instructor_id = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Instructor(
                            rs.getInt("instructor_id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("department")
                    );
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Instructor> getAllInstructors() {
        List<Instructor> list = new ArrayList<>();

        String sql = "SELECT * FROM instructors ORDER BY name";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new Instructor(
                        rs.getInt("instructor_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("department")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateInstructor(int id, String name, String email, String department) {
        String sql = "UPDATE instructors SET name=?, email=?, department=? WHERE instructor_id=?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, department);
            stmt.setInt(4, id);
            stmt.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteInstructor(int id) {
        String sql = "DELETE FROM instructors WHERE instructor_id=?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Course methods
    public boolean addCourse(String code, String title, int credits) {
        String sql = "INSERT INTO courses (code, title, credits) VALUES (?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, code);
            stmt.setString(2, title);
            stmt.setInt(3, credits);
            stmt.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Course getCourseById(int id) {
        String sql = "SELECT * FROM courses WHERE course_id = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Course(
                            rs.getInt("course_id"),
                            rs.getString("code"),
                            rs.getString("title"),
                            rs.getInt("credits")
                    );
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Course> getAllCourses() {
        List<Course> list = new ArrayList<>();

        String sql = "SELECT * FROM courses ORDER BY code";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new Course(
                        rs.getInt("course_id"),
                        rs.getString("code"),
                        rs.getString("title"),
                        rs.getInt("credits")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateCourse(int id, String code, String title, int credits) {
        String sql = "UPDATE courses SET code=?, title=?, credits=? WHERE course_id=?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, code);
            stmt.setString(2, title);
            stmt.setInt(3, credits);
            stmt.setInt(4, id);
            stmt.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCourse(int id) {
        String sql = "DELETE FROM courses WHERE course_id=?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Section methods
    public boolean addSection(int courseId, Integer instructorId, String name, int capacity, String room, String timetable) {
        String sql = "INSERT INTO sections (course_id, instructor_id, name, capacity, room, timetable) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courseId);
            if (instructorId == null) {
                stmt.setNull(2, Types.INTEGER);
            } else {
                stmt.setInt(2, instructorId);
            }
            stmt.setString(3, name);
            stmt.setInt(4, capacity);
            stmt.setString(5, room);
            stmt.setString(6, timetable);
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Section getSectionById(int id) {
        String sql = "SELECT * FROM sections WHERE section_id = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapSection(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Section> getSectionsByCourse(int courseId) {
        List<Section> list = new ArrayList<>();
        String sql = "SELECT * FROM sections WHERE course_id = ? ORDER BY name";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapSection(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Section> getAllSections() {
        List<Section> list = new ArrayList<>();
        String sql = "SELECT * FROM sections ORDER BY course_id, section_id";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapSection(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateSection(int id, int courseId, Integer instructorId, String name, int capacity, String room, String timetable) {
        String sql = "UPDATE sections SET course_id=?, instructor_id=?, name=?, capacity=?, room=?, timetable=? WHERE section_id=?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courseId);
            if (instructorId == null) {
                stmt.setNull(2, Types.INTEGER);
            } else {
                stmt.setInt(2, instructorId);
            }
            stmt.setString(3, name);
            stmt.setInt(4, capacity);
            stmt.setString(5, room);
            stmt.setString(6, timetable);
            stmt.setInt(7, id);
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean assignInstructorToSection(int sectionId, Integer instructorId) {
        String sql = "UPDATE sections SET instructor_id=? WHERE section_id=?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (instructorId == null) {
                stmt.setNull(1, Types.INTEGER);
            } else {
                stmt.setInt(1, instructorId);
            }
            stmt.setInt(2, sectionId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteSection(int id) {
        String sql = "DELETE FROM sections WHERE section_id = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getSectionCountForCourse(int courseId) {
        String sql = "SELECT COUNT(*) AS total FROM sections WHERE course_id = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt("total") : 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public String getInstructorNameForCourse(int courseId) {
        String sql = """
            SELECT i.name 
            FROM instructors i
            JOIN sections s ON i.instructor_id = s.instructor_id
            WHERE s.course_id = ?
            """;

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                String first = null;
                boolean multiple = false;

                while (rs.next()) {
                    if (first == null) {
                        first = rs.getString("name");
                    } else {
                        multiple = true;
                        break;
                    }
                }

                if (first == null) return "";
                if (multiple) return "Multiple";
                return first;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    // Enrollment methods
    public int getEnrollmentCountForSection(int sectionId) {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE section_id = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sectionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Student> getStudentsForSection(int sectionId) {
        List<Student> list = new ArrayList<>();
        String sql = """
                SELECT s.student_id, s.name, s.email, s.program
                FROM students s
                INNER JOIN enrollments e ON e.student_id = s.student_id
                WHERE e.section_id = ?
                ORDER BY s.name
                """;
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sectionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Student(
                            rs.getInt("student_id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("program")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean createEnrollment(int studentId, int sectionId) {
        String sql = "INSERT INTO enrollments (student_id, section_id, grade_id) VALUES (?, ?, NULL)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);
            pstmt.setInt(2, sectionId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Enrollment getEnrollmentById(int id) {
        String sql = "SELECT id, student_id, section_id, grade_id FROM enrollments WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) return null;

                return new Enrollment(
                        rs.getInt("id"),
                        rs.getInt("student_id"),
                        rs.getInt("section_id"),
                        (Integer) rs.getObject("grade_id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Enrollment> getEnrollmentsByStudent(int studentId) {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT id, student_id, section_id, grade_id FROM enrollments WHERE student_id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Enrollment(
                            rs.getInt("id"),
                            rs.getInt("student_id"),
                            rs.getInt("section_id"),
                            (Integer) rs.getObject("grade_id")
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Enrollment> getAllEnrollments() {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT id, student_id, section_id, grade_id FROM enrollments";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                list.add(new Enrollment(
                        rs.getInt("id"),
                        rs.getInt("student_id"),
                        rs.getInt("section_id"),
                        (Integer) rs.getObject("grade_id")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateEnrollmentGrade(int enrollmentId, int gradeId) {
        String sql = "UPDATE enrollments SET grade_id = ? WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, gradeId);
            pstmt.setInt(2, enrollmentId);
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteEnrollment(int id) {
        String sql = "DELETE FROM enrollments WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Settings methods
    public boolean setSetting(String key, String value) {
        String sql = "INSERT INTO Settings (key, value) VALUES (?, ?) " +
                "ON CONFLICT(key) DO UPDATE SET value = excluded.value";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, key);
            pstmt.setString(2, value);
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Settings getSetting(String key) {
        String sql = "SELECT key, value FROM Settings WHERE key = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, key);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) return null;

                return new Settings(
                        rs.getString("key"),
                        rs.getString("value")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Settings> getAllSettings() {
        List<Settings> list = new ArrayList<>();
        String sql = "SELECT key, value FROM Settings";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Settings(
                        rs.getString("key"),
                        rs.getString("value")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Helper methods
    public List<String> getCourseCodesForStudent(int studentId) {
        List<String> courses = new ArrayList<>();
        String sql = """
                SELECT DISTINCT c.code || ' • ' || IFNULL(sec.name, 'Section') AS label
                FROM enrollments e
                INNER JOIN sections sec ON sec.section_id = e.section_id
                INNER JOIN courses c ON c.course_id = sec.course_id
                WHERE e.student_id = ?
                ORDER BY c.code
                """;
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    courses.add(rs.getString("label"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    public List<String> getCourseCodesForInstructor(int instructorId) {
        List<String> courses = new ArrayList<>();
        String sql = """
                SELECT DISTINCT c.code || ' • ' || IFNULL(sec.name, 'Section') AS label
                FROM sections sec
                INNER JOIN courses c ON c.course_id = sec.course_id
                WHERE sec.instructor_id = ?
                ORDER BY c.code
                """;
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, instructorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    courses.add(rs.getString("label"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    private Section mapSection(ResultSet rs) throws SQLException {
        return new Section(
                rs.getInt("section_id"),
                rs.getInt("course_id"),
                (Integer) rs.getObject("instructor_id"),
                rs.getString("name") != null ? rs.getString("name") : "Section A",
                rs.getInt("capacity"),
                rs.getString("room"),
                rs.getString("timetable")
        );
    }

    public static Student getStudentByEmailStatic(String email) {
        String sql = "SELECT * FROM students WHERE email=?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Student(
                            rs.getInt("student_id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("program")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Instructor getInstructorByEmailStatic(String email) {
        String sql = "SELECT * FROM instructors WHERE email=?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Instructor(
                            rs.getInt("instructor_id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("department")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Deprecated - use getStudentByEmailStatic instead
    @Deprecated
    public static ResultSet getStudentByEmail(String email) throws Exception {
        String sql = "SELECT * FROM students WHERE email=?";
        Connection conn = connect();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, email);
        return stmt.executeQuery();
    }

    // Deprecated - use getInstructorByEmailStatic instead
    @Deprecated
    public static ResultSet getInstructorByEmail(String email) throws Exception {
        String sql = "SELECT * FROM instructors WHERE email=?";
        Connection conn = connect();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, email);
        return stmt.executeQuery();
    }

    public int getStudentCount() {
        return getCount("students");
    }

    public int getInstructorCount() {
        return getCount("instructors");
    }

    public int getCourseCount() {
        return getCount("courses");
    }

    private int getCount(String table) {
        String sql = "SELECT COUNT(*) FROM " + table;
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void ensureColumn(String table, String column, String definition) {
        String sql = "ALTER TABLE " + table + " ADD COLUMN " + column + " " + definition;
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException ignored) {
            // column already exists
        }
    }

    private void migrateSectionsTable() {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            
            // Check if semester column exists by querying table info
            boolean hasSemester = false;
            boolean hasRoom = false;
            try (ResultSet rs = stmt.executeQuery("PRAGMA table_info(sections)")) {
                while (rs.next()) {
                    String colName = rs.getString("name");
                    if ("semester".equalsIgnoreCase(colName)) {
                        hasSemester = true;
                    }
                    if ("room".equalsIgnoreCase(colName)) {
                        hasRoom = true;
                    }
                }
            } catch (SQLException e) {
                // Table might not exist yet, which is fine
                return;
            }
            
            if (hasSemester) {
                // Old schema detected - migrate to new schema
                System.out.println("Migrating sections table from old schema...");
                
                // Create new table with correct schema
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS sections_new (
                        section_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        course_id INTEGER NOT NULL,
                        instructor_id INTEGER,
                        name TEXT NOT NULL DEFAULT 'Section A',
                        capacity INTEGER NOT NULL DEFAULT 60,
                        room TEXT,
                        timetable TEXT,
                        FOREIGN KEY(course_id) REFERENCES courses(course_id),
                        FOREIGN KEY(instructor_id) REFERENCES instructors(instructor_id)
                    )
                    """);
                
                // Copy data from old table (excluding semester)
                try {
                    // Check if room column exists in old table, if not use NULL
                    String copySql;
                    if (hasRoom) {
                        copySql = """
                            INSERT INTO sections_new (section_id, course_id, instructor_id, name, capacity, room, timetable)
                            SELECT section_id, course_id, instructor_id, 
                                   COALESCE(name, 'Section A') as name,
                                   COALESCE(capacity, 60) as capacity,
                                   room,
                                   COALESCE(timetable, '') as timetable
                            FROM sections
                            """;
                    } else {
                        copySql = """
                            INSERT INTO sections_new (section_id, course_id, instructor_id, name, capacity, room, timetable)
                            SELECT section_id, course_id, instructor_id, 
                                   COALESCE(name, 'Section A') as name,
                                   COALESCE(capacity, 60) as capacity,
                                   NULL as room,
                                   COALESCE(timetable, '') as timetable
                            FROM sections
                            """;
                    }
                    
                    stmt.execute(copySql);
                    
                    // Drop old table and rename new one
                    stmt.execute("DROP TABLE sections");
                    stmt.execute("ALTER TABLE sections_new RENAME TO sections");
                    System.out.println("Sections table migration completed successfully.");
                } catch (SQLException e) {
                    // If copy fails, drop the new table and keep old one
                    try {
                        stmt.execute("DROP TABLE sections_new");
                    } catch (SQLException ignored) {}
                    System.err.println("Migration failed, keeping old schema: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                // New schema - just ensure room column exists
                if (!hasRoom) {
                    ensureColumn("sections", "room", "TEXT");
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Grade methods
    public Grade getGradeById(int gradeId) {
        String sql = "SELECT * FROM grades WHERE grade_id = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gradeId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Grade(
                            rs.getInt("grade_id"),
                            rs.getInt("enroll_id"),
                            rs.getString("grade")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Grade> getGradesByEnrollment(int enrollmentId) {
        List<Grade> list = new ArrayList<>();
        String sql = "SELECT * FROM grades WHERE enroll_id = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, enrollmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Grade(
                            rs.getInt("grade_id"),
                            rs.getInt("enroll_id"),
                            rs.getString("grade")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Enrollment> getEnrollmentsWithGradesByStudent(int studentId) {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT id, student_id, section_id, grade_id FROM enrollments WHERE student_id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Enrollment(
                            rs.getInt("id"),
                            rs.getInt("student_id"),
                            rs.getInt("section_id"),
                            (Integer) rs.getObject("grade_id")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int getTotalCreditsForStudent(int studentId) {
        String sql = """
                SELECT SUM(c.credits) as total
                FROM enrollments e
                INNER JOIN sections s ON e.section_id = s.section_id
                INNER JOIN courses c ON s.course_id = c.course_id
                WHERE e.student_id = ?
                """;
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getRegisteredCourseCount(int studentId) {
        String sql = "SELECT COUNT(DISTINCT s.course_id) as count FROM enrollments e INNER JOIN sections s ON e.section_id = s.section_id WHERE e.student_id = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
