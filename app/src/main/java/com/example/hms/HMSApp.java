package com.example.hms;

import com.example.hms.auth.UserAttributes;
import com.example.hms.auth.UserDatabase;
import com.example.hms.util.crypto.PHCHash;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public final class HMSApp extends Application {
  Stage primaryStage;
  Scene primaryScene;
  UserDatabase ub = new UserDatabase("users.json");

  // Original main method
  public static void main_orig(String[] args) throws IOException {
    Scanner sc = new Scanner(System.in);
    UserDatabase ub = new UserDatabase("users.json");

    int choice;
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

  Scene registrationForm(UserDatabase ub) {
    VBox root = new VBox(10);
    root.setAlignment(Pos.CENTER);

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

    Button btnRegister = new Button("Register");

    Label lblStatus = new Label();

    btnRegister.setOnAction(e -> {
      String username = usernameField.getText();
      char[] password = passwordField.getText().toCharArray();
      boolean success = false;
      try {
        ub.addUser(username, password);
        lblStatus.setText("User registered successfully");
        success = true;
      } catch (Exception ex) {
        lblStatus.setText("Error registering user");
        System.err.println("Error registering user:\n" + ex);
        ex.printStackTrace(System.err);
      }
      if (success) {
        try {
          ub.flush();
        } catch (IOException ex) {
          lblStatus.setText("Error saving user data");
          System.err.println("Error saving user data:\n" + ex);
          ex.printStackTrace(System.err);
        }
      }
    });

    Button btnGoToMainWindow = new Button("Go to main window");
    btnGoToMainWindow.setOnAction(e -> goToMainWindow());

    root.getChildren().addAll(gp, btnRegister, btnGoToMainWindow, lblStatus);
    return new Scene(root, 300, 200);
  }

  Scene loginForm(UserDatabase ub) {
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
    btnGotToRegistration.setOnAction(e -> goToMainWindow());

    Label lblStatus = new Label();

    btnLogin.setOnAction(e -> {
      String username = usernameField.getText();
      char[] password = passwordField.getText().toCharArray();
      UUID userId = ub.login(username, password);
      System.out.println(userId);
      if (userId == null)
        lblStatus.setText("Invalid username or password");
      else
        lblStatus.setText("Authenticated user successfully");
    });

    root.getChildren().addAll(gp, btnLogin, btnGotToRegistration, lblStatus);
    return scene;
  }

  void goToMainWindow() { primaryStage.setScene(this.primaryScene); }

  Scene userList(UserDatabase ub) {
    VBox root = new VBox(10);
    root.setAlignment(Pos.CENTER);

    TableView<HashMap<String, String>> table = new TableView<>();
    table.setEditable(false);

    HashMap<UUID, UserAttributes> users = ub.listUsers();
    List<String> colNames = users.values().stream()
            .flatMap(ua -> ua.getAttributes().keySet().stream())
            .distinct()
            .toList();

    TableColumn<HashMap<String, String>, String> userIdCol = new TableColumn<>("userId");
    userIdCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("userId")));
    table.getColumns().add(userIdCol);
    for (String colName : colNames) {
        TableColumn<HashMap<String, String>, String> col = new TableColumn<>(colName);
        col.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(colName)));
        table.getColumns().add(col);
    }

    ObservableList<HashMap<String, String>> data = FXCollections.observableArrayList();
    for (UUID userId : users.keySet()) {
        HashMap<String, Object> attributes = users.get(userId).getAttributes();
        HashMap<String, String> row = new HashMap<>();
        row.put("userId", userId.toString());
        for (String colName : colNames) {
            Object value = attributes.get(colName);
            row.put(colName, value != null ? value.toString() : "");
        }
        data.add(row);
    }

    table.setItems(data);

    Scene scene = new Scene(root, 300, 200);
    root.getChildren().add(table);
    return scene;
  }
  // GUI Main method
  public void start(Stage primaryStage) {
    this.primaryStage = primaryStage;
    primaryStage.setTitle("HMS");

    Scene sceneLoginForm = loginForm(ub);
    Scene sceneRegistrationForm = registrationForm(ub);
    Scene sceneUserList = userList(ub);

    VBox root = new VBox(10);
    root.setAlignment(Pos.CENTER);

    Button btnLogin = new Button("Login");
    btnLogin.setOnAction(e -> primaryStage.setScene(sceneLoginForm));

    Button btnRegister = new Button("Register");
    btnRegister.setOnAction(e -> primaryStage.setScene(sceneRegistrationForm));

    Button btnListUsers = new Button("List users");
    btnListUsers.setOnAction(e -> primaryStage.setScene(sceneUserList));

    root.getChildren().addAll(btnLogin, btnRegister, btnListUsers);
    this.primaryScene = new Scene(root, 300, 200);
    primaryStage.setScene(this.primaryScene);
    primaryStage.show();
  }
}
