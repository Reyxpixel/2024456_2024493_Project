package model;

public class Grade {
    private int id;
    private int enrollmentId;
    private String grade;

    public Grade(int id, int enrollmentId, String grade) {
        this.id = id;
        this.enrollmentId = enrollmentId;
        this.grade = grade;
    }

    public int getId() { return id; }
    public int getEnrollmentId() { return enrollmentId; }
    public String getGrade() { return grade; }
}
