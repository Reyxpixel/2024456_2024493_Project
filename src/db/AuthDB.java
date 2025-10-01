package db;

import java.sql.Connection;
import java.sql.DriverManager;
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

    public static ResultSet getUser(String username) throws Exception {
        Connection conn = connect();
        Statement stmt = conn.createStatement();
        String sql = "SELECT * FROM users WHERE username='" + username + "'";
        return stmt.executeQuery(sql);
    }

    public static void addUser(String username, String passwordHash, String role) throws Exception {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            String sql = "INSERT INTO users (username, password_hash, role) " +
                    "VALUES ('" + username + "', '" + passwordHash + "', '" + role + "')";
            stmt.executeUpdate(sql);
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

}

