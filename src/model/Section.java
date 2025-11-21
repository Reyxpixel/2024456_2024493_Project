package model;

public class Section {
<<<<<<< HEAD
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
=======
    private int id;
    private int courseId;
    private int instructorId;
    private String schedule;   // "Mon 10–11, Wed 10–12"
    private String room;       // "B203"
    private int capacity;      // 60

    public Section(int id, int courseId, int instructorId, String schedule, String room, int capacity) {
        this.id = id;
        this.courseId = courseId;
        this.instructorId = instructorId;
        this.schedule = schedule;
        this.room = room;
        this.capacity = capacity;
    }

    public int getId(){ return id; }
    public int getCourseId(){ return courseId; }
    public int getInstructorId(){ return instructorId; }
    public String getSchedule(){ return schedule; }
    public String getRoom(){ return room; }
    public int getCapacity(){ return capacity; }
>>>>>>> a19db90af34c1b2d4bba788ca824308a2bab989f
}
