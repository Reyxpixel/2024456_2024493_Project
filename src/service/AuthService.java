package service;

import db.AuthDB;
import model.User;
import util.PasswordUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthService {

    public static User login(String username, String password) {
        String sql = "SELECT username, password_hash, role FROM users WHERE username=?";
        try (Connection conn = AuthDB.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    String role = rs.getString("role");

                    if (storedHash.equals(PasswordUtils.hashPassword(password))) {
                        return new User(username, storedHash, role);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // login failed
    }

    public static boolean register(String username, String password, String role) {
        try {
            if (AuthDB.userExists(username)) {
                System.out.println("User already exists: " + username);
                return false;
            }
            String hash = PasswordUtils.hashPassword(password);
            AuthDB.addUser(username, hash, role);
            System.out.println("User registered: " + username);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean changePassword(String username, String currentPassword, String newPassword) {
        User existing = login(username, currentPassword);
        if (existing == null) {
            return false;
        }
        String newHash = PasswordUtils.hashPassword(newPassword);
        return AuthDB.updatePassword(username, newHash);
    }

    public static boolean deleteUser(String username) {
        return AuthDB.deleteUser(username);
    }

}
