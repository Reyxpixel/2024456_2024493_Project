package service;

import db.erpDB;
import model.*;

import java.sql.ResultSet;

public class ErpService {
    private final erpDB db;

    public ErpService() {
        this.db = new erpDB();
    }
    public static Student getStudentByEmail(String email) {
        try {
            ResultSet rs = erpDB.getStudentByEmail(email);
            if (rs.next()) {
                return new Student(
                        rs.getInt("student_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("program")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Instructor getInstructorByEmail(String email) {
        try {
            ResultSet rs = erpDB.getInstructorByEmail(email);
            if (rs.next()) {
                return new Instructor(
                        rs.getInt("instructor_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("department")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
