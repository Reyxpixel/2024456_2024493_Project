import db.*;
import service.*;
import ui.*;

public class Main {
    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }



        AuthDB.init();

        AuthService.register("alice", "password123", "student");

        new LoginScreen().setVisible(true);
    }
}
