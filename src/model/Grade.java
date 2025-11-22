package model;

public class Grade {
    private int id;
    private int enrollmentId;
    private String grade; // Stores SGPA as string (e.g., "9.5")

    public Grade(int id, int enrollmentId, String grade) {
        this.id = id;
        this.enrollmentId = enrollmentId;
        this.grade = grade;
    }

    public int getId() { return id; }
    public int getEnrollmentId() { return enrollmentId; }
    public String getGrade() { return grade; }
    
    // Get SGPA as float, returns null if not set
    public Float getSGPA() {
        if (grade == null || grade.trim().isEmpty() || "-".equals(grade)) {
            return null;
        }
        try {
            return Float.parseFloat(grade);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    // Get letter grade based on SGPA
    public String getLetterGrade() {
        Float sgpa = getSGPA();
        if (sgpa == null) {
            return "-";
        }
        
        if (sgpa >= 10.1f) {
            return "A+";
        } else if (sgpa >= 9.5f) {
            return "A";
        } else if (sgpa >= 8.5f) {
            return "B+";
        } else if (sgpa >= 8.0f) {
            return "B";
        } else if (sgpa >= 7.5f) {
            return "C";
        } else if (sgpa >= 7.0f) {
            return "C-";
        } else if (sgpa >= 6.0f) {
            return "D";
        } else if (sgpa >= 4.0f) {
            return "D-";
        } else {
            return "F";
        }
    }
    
    // Static method to calculate letter grade from SGPA
    public static String calculateLetterGrade(Float sgpa) {
        if (sgpa == null) {
            return "-";
        }
        
        if (sgpa >= 10.1f) {
            return "A+";
        } else if (sgpa >= 9.5f) {
            return "A";
        } else if (sgpa >= 8.5f) {
            return "B+";
        } else if (sgpa >= 8.0f) {
            return "B";
        } else if (sgpa >= 7.5f) {
            return "C";
        } else if (sgpa >= 7.0f) {
            return "C-";
        } else if (sgpa >= 6.0f) {
            return "D";
        } else if (sgpa >= 4.0f) {
            return "D-";
        } else {
            return "F";
        }
    }
}
