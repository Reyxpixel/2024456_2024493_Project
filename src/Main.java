import db.AuthDB;
import service.AuthService;
import ui.LoginScreen;

public class Main {
    public static void main(String[] args) {
        AuthDB.init();

        AuthService.register("stu1", "stupass", "student");
        AuthService.register("stu2", "stupass", "student");
        AuthService.register("inst1", "instpass", "instructor");
        AuthService.register("admin1", "adminpass", "admin");

        new LoginScreen().setVisible(true);
        //samplecomment
    }
}
