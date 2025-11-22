package service;

import db.erpDB;
import model.*;

public class ErpService {
    private final erpDB db;

    public ErpService() {
        this.db = new erpDB();
    }
    public static Student getStudentByEmail(String email) {
        return erpDB.getStudentByEmailStatic(email);
    }

    public static Instructor getInstructorByEmail(String email) {
        return erpDB.getInstructorByEmailStatic(email);
    }
    // Sections
    public java.util.List<Section> getSectionsByCourse(int courseId) {
        return db.getSectionsByCourse(courseId);
    }

    public boolean addSection(int courseId, Integer instructorId, String name, int capacity, String room, String timetable) {
        return db.addSection(courseId, instructorId, name, capacity, room, timetable);
    }

    public boolean updateSection(int id, int courseId, Integer instructorId, String name, int capacity, String room, String timetable) {
        return db.updateSection(id, courseId, instructorId, name, capacity, room, timetable);
    }

    public boolean deleteSection(int id) {
        return db.deleteSection(id);
    }

    public int getStudentCountForSection(int sectionId) {
        return db.getEnrollmentCountForSection(sectionId);
    }

}
