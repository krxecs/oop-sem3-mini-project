package com.example.hms;

import com.example.hms.auth.User;
import com.example.hms.auth.UserDAO;
import com.example.hms.auth.UserDAOImpl;
import com.example.hms.util.crypto.PHCHash;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.sql.SQLException;

import com.j256.ormlite.dao.CloseableIterable;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public final class HMSApp extends Application {
  Stage primaryStage;
  Scene primaryScene;

  Scene registrationForm(UserDAO userDao) {
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

    Label emailLabel = new Label("Email: ");
    TextField emailField = new TextField();
    gp.add(emailLabel, 0, 2);
    gp.add(emailField, 1, 2);

    Label firstNameLabel = new Label("First Name: ");
    TextField firstNameField = new TextField();
    gp.add(firstNameLabel, 0, 3);
    gp.add(firstNameField, 1, 3);

    Label middleNameLabel = new Label("Middle Name: ");
    TextField middleNameField = new TextField();
    gp.add(middleNameLabel, 0, 4);
    gp.add(middleNameField, 1, 4);

    Label lastNameLabel = new Label("Last Name: ");
    TextField lastNameField = new TextField();
    gp.add(lastNameLabel, 0, 5);
    gp.add(lastNameField, 1, 5);

    Button btnRegister = new Button("Register");

    Label lblStatus = new Label();

    btnRegister.setOnAction(e -> {
      try {
        userDao.addUser(
          usernameField.getText(),
          passwordField.getText().toCharArray(),
          emailField.getText(),
          firstNameField.getText(),
          middleNameField.getText(),
          lastNameField.getText()
        );
        lblStatus.setText("User registered successfully");
      } catch (Exception ex) {
        lblStatus.setText("Error registering user");
        System.err.println("Error registering user:\n" + ex);
        ex.printStackTrace(System.err);
      }
    });

    Button btnGoToMainWindow = new Button("Go to main window");
    btnGoToMainWindow.setOnAction(e -> goToMainWindow());

    root.getChildren().addAll(gp, btnRegister, btnGoToMainWindow, lblStatus);
    return new Scene(root, 300, 200);
  }

  Scene loginForm(UserDAO userDao) {
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
      try {
        User user = userDao.login(username, password);
        lblStatus.setText("Authenticated user successfully");
      } catch (Exception ex) {
        lblStatus.setText("Invalid username or password");
      }
    });

    root.getChildren().addAll(gp, btnLogin, btnGotToRegistration, lblStatus);
    return scene;
  }

  void goToMainWindow() { primaryStage.setScene(this.primaryScene); }

  Scene userList(UserDAO userDao) {
    VBox root = new VBox(10);
    root.setAlignment(Pos.CENTER);

    TableView<HashMap<String, String>> table = new TableView<>();
    table.setEditable(false);

    List<User> users = new LinkedList<>();
    boolean success = false;
    try {
      users = userDao.queryForAll();
      success = true;
    } catch (Exception ex) {
      System.err.println("Error listing users:\n" + ex);
      ex.printStackTrace(System.err);
    }

    if (success) {
      ObservableList<HashMap<String, String>> data = FXCollections.observableArrayList();
      for (User user : users) {
        HashMap<String, String> row = new HashMap<>();
        row.put("user_id", user.getUserId().toString());
        row.put("username", user.getUsername());
        row.put("email", user.getEmail());
        row.put("first_name", user.getFirstName());
        row.put("middle_name", user.getMiddleName());
        row.put("last_name", user.getLastName());
        row.put("password_hash", user.getPasswordHash().toString());
        data.add(row);
      }

      for (Field colName : userDao.getTableInfo().getDataClass().getDeclaredFields()) {
        //if (!colName.isAnnotationPresent(DatabaseField.class))
        //  continue;
      //for (String colName : data.getFirst().keySet()) {
        System.out.println(colName.getName());
        TableColumn<HashMap<String, String>, String> col = new TableColumn<>(colName.getName());
        col.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(colName.getName())));
        table.getColumns().add(col);
      }

      table.setItems(data);
    }

    Button btnGoToMainWindow = new Button("Go to main window");
    btnGoToMainWindow.setOnAction(e -> goToMainWindow());

    Scene scene = new Scene(root, 300, 200);
    root.getChildren().addAll(table, btnGoToMainWindow);
    return scene;
  }
  // GUI Main method
  public void start(Stage primaryStage) {
    this.primaryStage = primaryStage;
    primaryStage.setTitle("HMS");
    Scene sceneLoginForm, sceneRegistrationForm;

    VBox root = new VBox(10);
    root.setAlignment(Pos.CENTER);

    try(JdbcConnectionSource connectionSource = new JdbcConnectionSource("jdbc:sqlite:db.sqlite3", "sa", "")) {
      UserDAO userDao = DaoManager.createDao(connectionSource, User.class);
      TableUtils.createTableIfNotExists(connectionSource, User.class);
      TableUtils.createTableIfNotExists(connectionSource, Patient.class);

      sceneLoginForm = loginForm(userDao);
      sceneRegistrationForm = registrationForm(userDao);

      Button btnLogin = new Button("Login");
      btnLogin.setOnAction(e -> primaryStage.setScene(sceneLoginForm));

      Button btnRegister = new Button("Register");
      btnRegister.setOnAction(e -> primaryStage.setScene(sceneRegistrationForm));

      Button btnListUsers = new Button("List users");
      btnListUsers.setOnAction(e -> {
        primaryStage.setScene(userList(userDao));
      });

      root.getChildren().addAll(btnLogin, btnRegister, btnListUsers);

      this.primaryScene = new Scene(root, 300, 200);
      primaryStage.setScene(this.primaryScene);
      primaryStage.show();
    } catch (Exception ex) {
      System.err.println("Error creating connection source:\n" + ex);
      ex.printStackTrace(System.err);
      System.exit(1);
    }
  }
}
