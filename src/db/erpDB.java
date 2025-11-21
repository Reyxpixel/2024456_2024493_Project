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
                    capacity INTEGER,
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
        return DriverManager.getConnection(DB_URL);
    }
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
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Student(
                        rs.getInt("student_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("program")
                );
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
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Instructor(
                        rs.getInt("instructor_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("department")
                );
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
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Course(
                        rs.getInt("course_id"),
                        rs.getString("code"),
                        rs.getString("title"),
                        rs.getInt("credits")
                );
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
    // addSection
    public boolean addSection(int courseId, Integer instructorId, String schedule, String room, int capacity) {
        String sql = "INSERT INTO sections (course_id, instructor_id, schedule, room, capacity) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courseId);
            if (instructorId == null) stmt.setNull(2, java.sql.Types.INTEGER);
            else stmt.setInt(2, instructorId);
            stmt.setString(3, schedule);
            stmt.setString(4, room);
            stmt.setInt(5, capacity);
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // getSectionById
    public Section getSectionById(int id) {
        String sql = "SELECT * FROM sections WHERE section_id = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Section(
                        rs.getInt("section_id"),
                        rs.getInt("course_id"),
                        rs.getInt("instructor_id"),
                        rs.getString("schedule"),
                        rs.getString("room"),
                        rs.getInt("capacity")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // getSectionsByCourse
    public java.util.List<Section> getSectionsByCourse(int courseId) {
        java.util.List<Section> list = new java.util.ArrayList<>();
        String sql = "SELECT * FROM sections WHERE course_id = ? ORDER BY section_id";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new Section(
                        rs.getInt("section_id"),
                        rs.getInt("course_id"),
                        rs.getInt("instructor_id"),
                        rs.getString("schedule"),
                        rs.getString("room"),
                        rs.getInt("capacity")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public int getSectionCountForCourse(int courseId) {
        String sql = "SELECT COUNT(*) AS total FROM sections WHERE course_id = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();

            return rs.next() ? rs.getInt("total") : 0;

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
            ResultSet rs = stmt.executeQuery();

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

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    // getAllSections
    public java.util.List<Section> getAllSections() {
        java.util.List<Section> list = new java.util.ArrayList<>();
        String sql = "SELECT * FROM sections ORDER BY course_id, section_id";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new Section(
                        rs.getInt("section_id"),
                        rs.getInt("course_id"),
                        rs.getInt("instructor_id"),
                        rs.getString("schedule"),
                        rs.getString("room"),
                        rs.getInt("capacity")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // updateSection
    public boolean updateSection(int id, Integer instructorId, String schedule, String room, int capacity) {
        String sql = "UPDATE sections SET instructor_id = ?, schedule = ?, room = ?, capacity = ? WHERE section_id = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (instructorId == null) stmt.setNull(1, java.sql.Types.INTEGER);
            else stmt.setInt(1, instructorId);
            stmt.setString(2, schedule);
            stmt.setString(3, room);
            stmt.setInt(4, capacity);
            stmt.setInt(5, id);
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // deleteSection
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

    // helper: get student count for a section (useful in UI)
    public int getStudentCountForSection(int sectionId) {
        String sql = "SELECT COUNT(*) AS cnt FROM enrollments WHERE section_id = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sectionId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("cnt");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean createEnrollment(int studentId, int sectionId) {
        String sql = "INSERT INTO Enrollment (student_id, section_id, grade_id) VALUES (?, ?, NULL)";
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
        String sql = "SELECT id, student_id, section_id, grade_id FROM Enrollment WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) return null;

            return new Enrollment(
                    rs.getInt("id"),
                    rs.getInt("student_id"),
                    rs.getInt("section_id"),
                    (Integer) rs.getObject("grade_id")
            );
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public List<Enrollment> getEnrollmentsByStudent(int studentId) {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT id, student_id, section_id, grade_id FROM Enrollment WHERE student_id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();

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
        String sql = "UPDATE Enrollment SET grade_id = ? WHERE id = ?";
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
        String sql = "DELETE FROM Enrollment WHERE id = ?";
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
            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) return null;

            return new Settings(
                    rs.getString("key"),
                    rs.getString("value")
            );

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

    public static ResultSet getStudentByEmail(String email) throws Exception {
        String sql = "SELECT * FROM students WHERE email=?";
        Connection conn = connect();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, email);
        return stmt.executeQuery();
    }

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
}
