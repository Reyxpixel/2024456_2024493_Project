import db.AuthDB;
import db.erpDB;
import model.Course;
import model.Section;
import service.AuthService;
import service.ErpService;
import ui.LoginScreen;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        AuthDB.init();

        // Note: If you encounter database schema errors, delete the data/erp.db file
        // and let it recreate with the new schema
        erpDB erpDb = new erpDB();

        // Register students with corresponding profiles (only if not already registered)
        if (AuthService.register("stu1", "stupass", "student")) {
            erpDb.addStudent("John Doe", "stu1@iiitd.ac.in", "CSE");
        } else {
            // User exists, but ensure student profile exists
            if (ErpService.getStudentByEmail("stu1@iiitd.ac.in") == null) {
                erpDb.addStudent("John Doe", "stu1@iiitd.ac.in", "CSE");
            }
        }

        if (AuthService.register("stu2", "stupass", "student")) {
            erpDb.addStudent("Jane Smith", "stu2@iiitd.ac.in", "ECE");
        } else {
            if (ErpService.getStudentByEmail("stu2@iiitd.ac.in") == null) {
                erpDb.addStudent("Jane Smith", "stu2@iiitd.ac.in", "ECE");
            }
        }

        if (AuthService.register("stu3", "stupass", "student")) {
            erpDb.addStudent("Alice Johnson", "stu3@iiitd.ac.in", "CSE");
        } else {
            if (ErpService.getStudentByEmail("stu3@iiitd.ac.in") == null) {
                erpDb.addStudent("Alice Johnson", "stu3@iiitd.ac.in", "CSE");
            }
        }

        // Register instructors with corresponding profiles
        if (AuthService.register("inst1", "instpass", "instructor")) {
            erpDb.addInstructor("Dr. Robert Brown", "inst1@iiitd.ac.in", "Computer Science");
        } else {
            if (ErpService.getInstructorByEmail("inst1@iiitd.ac.in") == null) {
                erpDb.addInstructor("Dr. Robert Brown", "inst1@iiitd.ac.in", "Computer Science");
            }
        }

        if (AuthService.register("inst2", "instpass", "instructor")) {
            erpDb.addInstructor("Dr. Sarah Williams", "inst2@iiitd.ac.in", "Electronics");
        } else {
            if (ErpService.getInstructorByEmail("inst2@iiitd.ac.in") == null) {
                erpDb.addInstructor("Dr. Sarah Williams", "inst2@iiitd.ac.in", "Electronics");
            }
        }

        // Register admin
        AuthService.register("admin1", "adminpass", "admin");

        // Add some sample courses (check if they exist first to avoid duplicates)
        List<Course> existingCourses = erpDb.getAllCourses();
        boolean hasCS101 = existingCourses.stream().anyMatch(c -> "CS101".equals(c.getCode()));
        boolean hasCS201 = existingCourses.stream().anyMatch(c -> "CS201".equals(c.getCode()));
        boolean hasCS301 = existingCourses.stream().anyMatch(c -> "CS301".equals(c.getCode()));
        boolean hasECE101 = existingCourses.stream().anyMatch(c -> "ECE101".equals(c.getCode()));
        boolean hasMATH101 = existingCourses.stream().anyMatch(c -> "MATH101".equals(c.getCode()));

        if (!hasCS101) {
            erpDb.addCourse("CS101", "Introduction to Programming", 4);
        }
        if (!hasCS201) {
            erpDb.addCourse("CS201", "Data Structures", 4);
        }
        if (!hasCS301) {
            erpDb.addCourse("CS301", "Algorithms", 3);
        }
        if (!hasECE101) {
            erpDb.addCourse("ECE101", "Digital Circuits", 4);
        }
        if (!hasMATH101) {
            erpDb.addCourse("MATH101", "Linear Algebra", 3);
        }

        // Add some sample sections (only if table is empty or migration succeeded)
        try {
            List<Section> existingSections = erpDb.getAllSections();
            if (existingSections.isEmpty()) {
                // Get course IDs (they might have changed if courses were re-added)
                int cs101Id = erpDb.getAllCourses().stream()
                        .filter(c -> "CS101".equals(c.getCode()))
                        .findFirst().map(Course::getId).orElse(1);
                int cs201Id = erpDb.getAllCourses().stream()
                        .filter(c -> "CS201".equals(c.getCode()))
                        .findFirst().map(Course::getId).orElse(2);
                int cs301Id = erpDb.getAllCourses().stream()
                        .filter(c -> "CS301".equals(c.getCode()))
                        .findFirst().map(Course::getId).orElse(3);
                int ece101Id = erpDb.getAllCourses().stream()
                        .filter(c -> "ECE101".equals(c.getCode()))
                        .findFirst().map(Course::getId).orElse(4);
                int math101Id = erpDb.getAllCourses().stream()
                        .filter(c -> "MATH101".equals(c.getCode()))
                        .findFirst().map(Course::getId).orElse(5);

                // Get instructor IDs
                int inst1Id = ErpService.getInstructorByEmail("inst1@iiitd.ac.in") != null
                        ? ErpService.getInstructorByEmail("inst1@iiitd.ac.in").getId() : 1;
                int inst2Id = ErpService.getInstructorByEmail("inst2@iiitd.ac.in") != null
                        ? ErpService.getInstructorByEmail("inst2@iiitd.ac.in").getId() : 2;

                erpDb.addSection(cs101Id, inst1Id, "Section A", 60, "B203", "Mon 10:00-11:00, Wed 10:00-11:00");
                erpDb.addSection(cs101Id, inst1Id, "Section B", 60, "B204", "Tue 14:00-15:00, Thu 14:00-15:00");
                erpDb.addSection(cs201Id, inst1Id, "Section A", 50, "B205", "Mon 12:00-13:00, Wed 12:00-13:00");
                erpDb.addSection(cs301Id, inst1Id, "Section A", 40, "B206", "Tue 10:00-11:00, Thu 10:00-11:00");
                erpDb.addSection(ece101Id, inst2Id, "Section A", 55, "B207", "Mon 14:00-15:00, Wed 14:00-15:00");
                erpDb.addSection(math101Id, inst2Id, "Section A", 45, "B208", "Tue 12:00-13:00, Thu 12:00-13:00");
            }
        } catch (Exception e) {
            System.err.println("Error adding sections. If you see database schema errors, please delete data/erp.db and restart.");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

        new LoginScreen().setVisible(true);
    }
}
