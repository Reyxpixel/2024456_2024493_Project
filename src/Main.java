import db.AuthDB;
import service.AuthService;
import ui.LoginScreen;

public class Main {
    public static void main(String[] args) {
        // Initialize DB
        AuthDB.init();

        // Add some hardcoded users (only if not already present)
        AuthService.register("alice", "password123", "student");
        AuthService.register("bob", "teachpass", "instructor");
        AuthService.register("charlie", "adminpass", "admin");

        // Launch login screen
        new LoginScreen().setVisible(true);
    }
}
