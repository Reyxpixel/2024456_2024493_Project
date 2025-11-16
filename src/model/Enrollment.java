package model;

public class Enrollment {
    private final int id;
    private final int studentId;
    private final int sectionId;
    private final Integer gradeId; // Nullable

    public Enrollment(int id, int studentId, int sectionId, Integer gradeId) {
        this.id = id;
        this.studentId = studentId;
        this.sectionId = sectionId;
        this.gradeId = gradeId;
    }

    public int getId() { return id; }
    public int getStudentId() { return studentId; }
    public int getSectionId() { return sectionId; }
    public Integer getGradeId() { return gradeId; }
}
