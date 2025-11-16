package model;

public class Section {
    private int id;
    private int courseId;
    private int instructorId;
    private String semester;

    public Section(int id, int courseId, int instructorId, String semester) {
        this.id = id;
        this.courseId = courseId;
        this.instructorId = instructorId;
        this.semester = semester;
    }

    public int getId() { return id; }
    public int getCourseId() { return courseId; }
    public int getInstructorId() { return instructorId; }
    public String getSemester() { return semester; }
}
