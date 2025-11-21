package model;

public class Section {
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
}
