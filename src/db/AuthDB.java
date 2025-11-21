package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;

public class AuthDB {
    private static final String DB_URL = "jdbc:sqlite:data/auth.db";

    public static Connection connect() throws Exception {return DriverManager.getConnection(DB_URL);}

    public static void init() {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS users (" +
                    "username TEXT PRIMARY KEY, " +
                    "password_hash TEXT NOT NULL, " +
                    "role TEXT NOT NULL)";
            stmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addUser(String username, String passwordHash, String role) throws Exception {
        String sql = "INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, passwordHash);
            stmt.setString(3, role);
            stmt.executeUpdate();
        }
    }

    public static boolean userExists(String username) {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            String sql = "SELECT 1 FROM users WHERE username='" + username + "' LIMIT 1";
            ResultSet rs = stmt.executeQuery(sql);
            return rs.next();
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean updatePassword(String username, String passwordHash) {
        String sql = "UPDATE users SET password_hash=? WHERE username=?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, passwordHash);
            stmt.setString(2, username);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteUser(String username) {
        String sql = "DELETE FROM users WHERE username=?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}

