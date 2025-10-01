package ui;

import model.User;
import service.AuthService;

import javax.swing.*;
import java.awt.event.*;

public class LoginScreen extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginScreen() {
        setTitle("Login");
        setSize(300, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        loginButton = new JButton("Login");

        JPanel panel = new JPanel();
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(loginButton);

        add(panel);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String u = usernameField.getText();
                String p = new String(passwordField.getPassword());
                User user = AuthService.login(u, p);

                if (user != null) {
                    JOptionPane.showMessageDialog(null, "Welcome " + user.getRole() + " " + user.getUsername());
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid credentials!");
                }
            }
        });
    }
}
