import db.AuthDB;
import service.AuthService;
import ui.LoginScreen;

public class Main {
    public static void main(String[] args) {
        AuthDB.init();

        AuthService.register("alice", "password123", "student");
        AuthService.register("bob", "teachpass", "instructor");
        AuthService.register("charlie", "adminpass", "admin");

        new LoginScreen().setVisible(true);
    }
}
