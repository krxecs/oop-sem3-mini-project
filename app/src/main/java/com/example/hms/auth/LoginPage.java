package com.example.hms.auth;

import com.example.hms.util.RunOnChange;
import com.example.hms.util.auth.User;
import com.example.hms.util.auth.UserDAO;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class LoginPage {
  RunOnChange<User> loggedInUser;

  public LoginPage(RunOnChange<User> loggedInUser) { this.loggedInUser = loggedInUser; }

  public Scene getScene(UserDAO userDao, EventHandler<ActionEvent> goToMainWindow, RunOnChange<User> loggedInUser) {
    VBox root = new VBox(10);
    root.setAlignment(Pos.CENTER);

    Scene scene = new Scene(root, 300, 200);

    GridPane gp = new GridPane();
    gp.setAlignment(Pos.CENTER);
    gp.setHgap(10);
    gp.setVgap(10);
    Label usernameLabel = new Label("Username: ");
    TextField usernameField = new TextField();
    gp.add(usernameLabel, 0, 0);
    gp.add(usernameField, 1, 0);

    Label passwordLabel = new Label("Password: ");
    PasswordField passwordField = new PasswordField();
    gp.add(passwordLabel, 0, 1);
    gp.add(passwordField, 1, 1);

    Button btnLogin = new Button("Login");

    Button btnGotToRegistration = new Button("Go to main window");
    btnGotToRegistration.setOnAction(goToMainWindow);

    Label lblStatus = new Label();

    btnLogin.setOnAction(e -> {
      String username = usernameField.getText();
      char[] password = passwordField.getText().toCharArray();
      try {
        User user = userDao.login(username, password);
        System.out.println("Authenticated user: " + user);
        lblStatus.setText("Authenticated user successfully");
        loggedInUser.set(user);
      } catch (Exception ex) {
        lblStatus.setText("Invalid username or password");
        System.err.println("Error logging in:\n" + ex);
        ex.printStackTrace(System.err);
      }
    });

    root.getChildren().addAll(gp, btnLogin, btnGotToRegistration, lblStatus);
    return scene;
  }
}
