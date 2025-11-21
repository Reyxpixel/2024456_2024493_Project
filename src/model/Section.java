package model;

public class Section {
    private final int id;
    private final int courseId;
    private final Integer instructorId;
    private final String name;
    private final int capacity;
    private final String timetable;
    private final String semester;

    public Section(int id,
                   int courseId,
                   Integer instructorId,
                   String name,
                   int capacity,
                   String timetable,
                   String semester) {
        this.id = id;
        this.courseId = courseId;
        this.instructorId = instructorId;
        this.name = name;
        this.capacity = capacity;
        this.timetable = timetable;
        this.semester = semester;
    }

    public int getId() {
        return id;
    }

    public int getCourseId() {
        return courseId;
    }

    public Integer getInstructorId() {
        return instructorId;
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getTimetable() {
        return timetable;
    }

    public String getSemester() {
        return semester;
    }
}
