package model;

public class Section {
    private final int id;
    private final int courseId;
    private final Integer instructorId;
    private final String name;
    private final int capacity;
    private final String room;
    private final String timetable;

    public Section(int id,
                   int courseId,
                   Integer instructorId,
                   String name,
                   int capacity,
                   String room,
                   String timetable) {
        this.id = id;
        this.courseId = courseId;
        this.instructorId = instructorId;
        this.name = name;
        this.capacity = capacity;
        this.room = room;
        this.timetable = timetable;
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

    public String getRoom() {
        return room;
    }

    public String getTimetable() {
        return timetable;
    }
    
    // For backward compatibility
    public String getSchedule() {
        return timetable;
    }
}
