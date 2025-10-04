package service;

import db.AuthDB;
import model.User;
import util.*;
import java.sql.ResultSet;

public class AuthService {

    private static String hash(String password) {
        return Integer.toHexString(password.hashCode());
    }

    public static User login(String username, String password) {
        try {
            ResultSet rs = AuthDB.getUser(username);
            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                String role = rs.getString("role");

                if (storedHash.equals(util.PasswordUtils.hashPassword(password))) {
                    return new User(username, storedHash, role);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // login failed
    }

    public static void register(String username, String password, String role) {
        try {
            if (AuthDB.userExists(username)) {
                System.out.println("User already exists: " + username);
                return;
            }
            String hash = PasswordUtils.hashPassword(password);
            AuthDB.addUser(username, hash, role);
            System.out.println("User registered: " + username);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
