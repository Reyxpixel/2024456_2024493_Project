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
            instructor_id INTEGER NOT NULL,
            semester TEXT NOT NULL,
            FOREIGN KEY(course_id) REFERENCES courses(course_id),
            FOREIGN KEY(instructor_id) REFERENCES instructors(instructor_id)
        );
    """;

        String enrollments = """
        CREATE TABLE IF NOT EXISTS enrollments (
            enroll_id INTEGER PRIMARY KEY AUTOINCREMENT,
            student_id INTEGER NOT NULL,
            section_id INTEGER NOT NULL,
            FOREIGN KEY(student_id) REFERENCES students(student_id),
            FOREIGN KEY(section_id) REFERENCES sections(section_id)
        );
    """;

        String grades = """
        CREATE TABLE IF NOT EXISTS grades (
            grade_id INTEGER PRIMARY KEY AUTOINCREMENT,
            enroll_id INTEGER NOT NULL,
            grade TEXT,
            FOREIGN KEY(enroll_id) REFERENCES enrollments(enroll_id)
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
    private Connection connect() {
        Connection conn = null;

        try {
            // IMPORTANT: Update the file path to match your project
            String url = "jdbc:sqlite:erp.db";
            conn = DriverManager.getConnection(url);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return conn;
    }
    public boolean addStudent(String name, String email, String program) {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {

            String sql = "INSERT INTO students (name, email, program) VALUES (" +
                    "'" + name + "', " +
                    "'" + email + "', " +
                    "'" + program + "'" +
                    ");";

            stmt.execute(sql);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public Student getStudentById(int id) {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            String sql = "SELECT * FROM students WHERE student_id = " + id + ";";
            ResultSet rs = stmt.executeQuery(sql);

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

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            String sql = "SELECT * FROM students;";
            ResultSet rs = stmt.executeQuery(sql);

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
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {

            String sql = "UPDATE students SET " +
                    "name='" + name + "', " +
                    "email='" + email + "', " +
                    "program='" + program + "' " +
                    "WHERE student_id=" + id + ";";

            stmt.execute(sql);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean deleteStudent(int id) {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {

            String sql = "DELETE FROM students WHERE student_id=" + id + ";";
            stmt.execute(sql);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean addInstructor(String name, String email, String department) {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {

            String sql = "INSERT INTO instructors (name, email, department) VALUES (" +
                    "'" + name + "', " +
                    "'" + email + "', " +
                    "'" + department + "'" +
                    ");";

            stmt.execute(sql);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public Instructor getInstructorById(int id) {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            String sql = "SELECT * FROM instructors WHERE instructor_id = " + id + ";";
            ResultSet rs = stmt.executeQuery(sql);

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

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            String sql = "SELECT * FROM instructors;";
            ResultSet rs = stmt.executeQuery(sql);

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
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {

            String sql = "UPDATE instructors SET " +
                    "name='" + name + "', " +
                    "email='" + email + "', " +
                    "department='" + department + "' " +
                    "WHERE instructor_id=" + id + ";";

            stmt.execute(sql);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean deleteInstructor(int id) {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {

            String sql = "DELETE FROM instructors WHERE instructor_id=" + id + ";";
            stmt.execute(sql);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean addCourse(String code, String title, int credits) {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {

            String sql = "INSERT INTO courses (code, title, credits) VALUES (" +
                    "'" + code + "', " +
                    "'" + title + "', " +
                    credits +
                    ");";

            stmt.execute(sql);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public Course getCourseById(int id) {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            String sql = "SELECT * FROM courses WHERE course_id = " + id + ";";
            ResultSet rs = stmt.executeQuery(sql);

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

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            String sql = "SELECT * FROM courses;";
            ResultSet rs = stmt.executeQuery(sql);

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
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {

            String sql = "UPDATE courses SET " +
                    "code='" + code + "', " +
                    "title='" + title + "', " +
                    "credits=" + credits + " " +
                    "WHERE course_id=" + id + ";";

            stmt.execute(sql);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean deleteCourse(int id) {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {

            String sql = "DELETE FROM courses WHERE course_id=" + id + ";";
            stmt.execute(sql);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean addSection(int courseId, int instructorId, String semester) {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {

            String sql = "INSERT INTO sections (course_id, instructor_id, semester) VALUES (" +
                    courseId + ", " +
                    instructorId + ", " +
                    "'" + semester + "'" +
                    ");";

            stmt.execute(sql);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public Section getSectionById(int id) {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            String sql = "SELECT * FROM sections WHERE section_id = " + id + ";";
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                return new Section(
                        rs.getInt("section_id"),
                        rs.getInt("course_id"),
                        rs.getInt("instructor_id"),
                        rs.getString("semester")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public List<Section> getAllSections() {
        List<Section> list = new ArrayList<>();

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            String sql = "SELECT * FROM sections;";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                list.add(new Section(
                        rs.getInt("section_id"),
                        rs.getInt("course_id"),
                        rs.getInt("instructor_id"),
                        rs.getString("semester")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public boolean updateSection(int id, int courseId, int instructorId, String semester) {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {

            String sql = "UPDATE sections SET " +
                    "course_id=" + courseId + ", " +
                    "instructor_id=" + instructorId + ", " +
                    "semester='" + semester + "' " +
                    "WHERE section_id=" + id + ";";

            stmt.execute(sql);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean deleteSection(int id) {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {

            String sql = "DELETE FROM sections WHERE section_id=" + id + ";";
            stmt.execute(sql);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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

}
