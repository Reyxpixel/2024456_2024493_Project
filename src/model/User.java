package model;

public class User {
    private final String username;
    private final String passwordHash;
    private final String role;

    public User(String username, String passwordHash, String role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public String getUsername() {return username;}
    public String getPasswordHash() {return passwordHash;}
    public String getRole() {return role;}
}

