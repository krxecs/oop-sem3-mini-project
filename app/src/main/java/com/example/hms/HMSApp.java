package com.example.hms;

import com.example.hms.auth.UserAttributes;
import com.example.hms.auth.UserDatabase;
import com.example.hms.util.crypto.PHCHash;

import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.UUID;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public final class HMSApp extends Application {
  // Original main method
  public static void main_orig(String[] args) throws IOException {
    Scanner sc = new Scanner(System.in);
    UserDatabase ub = new UserDatabase("users.json");

    int choice = 0;
    do {
      System.out.print("Choice:\n\t1. Add user\n\t2. Delete user\n\t3. List users\n\t4. Exit\n\nEnter choice: ");
      choice = sc.nextInt();
      sc.nextLine();
      switch (choice) {
        case 1:
          System.out.println("Enter a username:");
          String username = sc.nextLine();
          System.out.println("Enter a password:");
          char[] pwd = sc.nextLine().toCharArray();
          ub.addUser(username, pwd);
          break;
        case 2:
          System.out.println("Enter a username:");
          String delUser = sc.nextLine();
          ub.removeUser(delUser);
          break;
        case 3:
          HashMap<UUID, UserAttributes> users = ub.listUsers();
          for (UUID k : users.keySet()) {
            HashMap<String, Object> ua = users.get(k).getAttributes();
            System.out.print("\t" + k + "\t" + ua.get("username"));
            for (String k1 : ua.keySet().stream().filter(k1 -> !k1.equals("username")).toList()) {
              if (ua.get(k1) instanceof PHCHash)
                System.out.print("\t" + new String(((PHCHash) ua.get(k1)).getFormattedHash()));
              else
                System.out.print("\t" + ua.get(k1).toString());
            }
            System.out.println();
          }
          break;
        case 4:
          System.out.println("Exiting...");
          break;
        default:
          System.out.println("Invalid choice");
      }
      System.out.println();
    } while (choice != 4);

    ub.flush();
    sc.close();
  }

  // GUI Main method
  public void start(Stage primaryStage) {
    primaryStage.setTitle("HMS");

    VBox root = new VBox(10);
    root.setAlignment(Pos.CENTER);

    Scene scene = new Scene(root, 300, 200);

    primaryStage.setScene(scene);

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

    Label lblStatus = new Label();

    btnLogin.setOnAction(e -> {
      String username = usernameField.getText();
      char[] password = passwordField.getText().toCharArray();
      try {
        UserDatabase ub = new UserDatabase("users.json");
        UUID userId = ub.login(username, password);
        System.out.println(userId);
        if (userId == null)
          lblStatus.setText("Invalid username or password");
        else
          lblStatus.setText("Authenticated user successfully");
      } catch (IOException ex) {
        lblStatus.setText("Error authenticating user");
      }
    });

    root.getChildren().addAll(gp, btnLogin, lblStatus);
    primaryStage.show();
  }
}
