package com.example.hms.auth.admin;

import com.example.hms.HMSApp;
import com.example.hms.util.auth.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UserModification {
  private UserDAO userDao;
  private PatientDAO patientDao;
  private DoctorDAO doctorDao;
  private AdministratorDAO adminDao;
  private Stage primaryStage;

  void errorDialog(String err) {
    Alert alert = new Alert(Alert.AlertType.ERROR, "Error");
    alert.setTitle("Error");
    alert.setHeaderText(null);
    alert.setContentText(err);
    alert.showAndWait();
  }

  public UserModification(UserDAO userDao, PatientDAO patientDao,
                          DoctorDAO doctorDao, AdministratorDAO adminDao,
                          Stage primaryStage) {
    this.userDao = userDao;
    this.patientDao = patientDao;
    this.doctorDao = doctorDao;
    this.adminDao = adminDao;
    this.primaryStage = primaryStage;
  }

  public Scene getScene(EventHandler<ActionEvent> goToMainWindow) {
    VBox root = new VBox(10);
    root.setAlignment(Pos.CENTER);

    TableView<HashMap<String, String>> table = new TableView<>();
    table.setEditable(true);

    final List<User>[] users = new List[] {new LinkedList<>()};
    boolean success = false;
    try {
      users[0] = userDao.queryForAll();
      success = true;
    } catch (Exception ex) {
      System.err.println("Error listing users:\n" + ex);
      ex.printStackTrace(System.err);
      Alert alert = new Alert(Alert.AlertType.ERROR, "Error listing users", ButtonType.OK);
      alert.setHeaderText(ex.toString());
      alert.showAndWait()
          .filter(response -> response == ButtonType.OK)
          .ifPresent(res -> goToMainWindow.handle(null));
    }

    if (success) {
      ObservableList<HashMap<String, String>> data =
          FXCollections.observableArrayList();
      for (User user : users[0]) {
        HashMap<String, String> row = new HashMap<>();
        row.put("user_id", user.getUserId().toString());
        row.put("username", user.getUsername());
        row.put("email", user.getEmail());
        row.put("first_name", user.getFirstName());
        row.put("middle_name", user.getMiddleName());
        row.put("last_name", user.getLastName());
        row.put("date_of_birth", user.getDateOfBirth().toString());
        row.put("phone_number", user.getPhoneNumber());
        data.add(row);
      }

      TableColumn<HashMap<String, String>, String> userIdCol =
          new TableColumn<>("User ID");
      userIdCol.setCellValueFactory(
          param -> new SimpleStringProperty(param.getValue().get("user_id")));
      table.getColumns().add(userIdCol);

      TableColumn<HashMap<String, String>, String> usernameCol =
          new TableColumn<>("Username");
      usernameCol.setCellValueFactory(
          param -> new SimpleStringProperty(param.getValue().get("username")));
      table.getColumns().add(usernameCol);

      TableColumn<HashMap<String, String>, String> emailCol =
          new TableColumn<>("Email");
      emailCol.setCellValueFactory(
          param -> new SimpleStringProperty(param.getValue().get("email")));
      table.getColumns().add(emailCol);

      TableColumn<HashMap<String, String>, String> firstNameCol =
          new TableColumn<>("First Name");
      firstNameCol.setCellValueFactory(
          param
          -> new SimpleStringProperty(param.getValue().get("first_name")));
      table.getColumns().add(firstNameCol);

      TableColumn<HashMap<String, String>, String> middleNameCol =
          new TableColumn<>("Middle Name");
      middleNameCol.setCellValueFactory(
          param
          -> new SimpleStringProperty(param.getValue().get("middle_name")));
      table.getColumns().add(middleNameCol);

      TableColumn<HashMap<String, String>, String> lastNameCol =
          new TableColumn<>("Last Name");
      lastNameCol.setCellValueFactory(
          param -> new SimpleStringProperty(param.getValue().get("last_name")));
      table.getColumns().add(lastNameCol);

      TableColumn<HashMap<String, String>, String> dateOfBirthCol =
          new TableColumn<>("Date of Birth");
      dateOfBirthCol.setCellValueFactory(
          param
          -> new SimpleStringProperty(param.getValue().get("date_of_birth")));
      table.getColumns().add(dateOfBirthCol);

      TableColumn<HashMap<String, String>, String> phoneNumberCol =
          new TableColumn<>("Phone Number");
      phoneNumberCol.setCellValueFactory(
          param
          -> new SimpleStringProperty(param.getValue().get("phone_number")));
      table.getColumns().add(phoneNumberCol);

      TableColumn<HashMap<String, String>, String> adminCol =
          new TableColumn<>("Admin");
      adminCol.setCellValueFactory(param -> {
        try {
          if (adminDao.getAdministratorObjectForUser(userDao.queryForId(
                  UUID.fromString(param.getValue().get("user_id")))) != null) {
            return new SimpleStringProperty("Yes");
          } else {
            return new SimpleStringProperty("No");
          }
        } catch (Exception ex) {
          System.err.println("Error getting admin status:\n" + ex);
          ex.printStackTrace(System.err);
          return new SimpleStringProperty("Error");
        }
      });
      table.getColumns().add(adminCol);

      TableColumn<HashMap<String, String>, String> doctorCol =
          new TableColumn<>("Doctor");
      doctorCol.setCellValueFactory(param -> {
        try {
          if (doctorDao.getDoctorObjectForUser(userDao.queryForId(
                  UUID.fromString(param.getValue().get("user_id")))) != null) {
            return new SimpleStringProperty("Yes");
          } else {
            return new SimpleStringProperty("No");
          }
        } catch (Exception ex) {
          System.err.println("Error getting doctor status:\n" + ex);
          ex.printStackTrace(System.err);
          return new SimpleStringProperty("Error");
        }
      });
      table.getColumns().add(doctorCol);

      table.setItems(data);

      Button btnRefresh = new Button("Refresh");
      btnRefresh.setOnAction(e -> {
        data.clear();
        try {
          users[0] = userDao.queryForAll();
        } catch (Exception ex) {
          System.err.println("Error listing users:\n" + ex);
          ex.printStackTrace(System.err);
        }
        for (User user : users[0]) {
          HashMap<String, String> row = new HashMap<>();
          row.put("user_id", user.getUserId().toString());
          row.put("username", user.getUsername());
          row.put("email", user.getEmail());
          row.put("first_name", user.getFirstName());
          row.put("middle_name", user.getMiddleName());
          row.put("last_name", user.getLastName());
          row.put("password_hash", user.getPasswordHash().toString());
          row.put("date_of_birth", user.getDateOfBirth().toString());
          row.put("phone_number", user.getPhoneNumber());
          data.add(row);
        }
      });

      Button btnAddUser = new Button("Add User");
      btnAddUser.setOnAction(e -> {
        Stage stage = new Stage();
        stage.setTitle("Add User");
        stage.setScene(HMSApp.userRegistrationForm(
            userDao, patientDao, doctorDao, getScene(goToMainWindow), adminDao,
            true, goToMainWindow, primaryStage));
        stage.show();
      });

      Button btnDeleteUser = new Button("Delete User");
      btnDeleteUser.setOnAction(e -> {
        HashMap<String, String> selected =
            table.getSelectionModel().getSelectedItem();
        if (selected == null) {
          errorDialog("No user selected");
          return;
        }

        Alert confirmationAlert =
            new Alert(Alert.AlertType.CONFIRMATION,
                      "Are you sure you want to delete this user?",
                      ButtonType.YES, ButtonType.NO);
        confirmationAlert.showAndWait()
            .filter(response -> response == ButtonType.YES)
            .ifPresent(res -> {
              try {
                userDao.deleteById(UUID.fromString(selected.get("user_id")));
                data.remove(selected);
              } catch (Exception ex) {
                System.err.println("Error deleting user:\n" + ex);
                ex.printStackTrace(System.err);
                errorDialog("Error deleting user");
              }
            });
      });

      Button btnMakeAdmin = new Button("Make Admin");
      btnMakeAdmin.setOnAction(e -> {
        HashMap<String, String> selected =
            table.getSelectionModel().getSelectedItem();
        if (selected == null) {
          errorDialog("No user selected");
          return;
        }
        try {
          User user =
              userDao.queryForId(UUID.fromString(selected.get("user_id")));
          Administrator admin = new Administrator(user);
          adminDao.create(admin);
          data.clear();
          users[0] = userDao.queryForAll();
          for (User u : users[0]) {
            HashMap<String, String> row = new HashMap<>();
            row.put("user_id", u.getUserId().toString());
            row.put("username", u.getUsername());
            row.put("email", u.getEmail());
            row.put("first_name", u.getFirstName());
            row.put("middle_name", u.getMiddleName());
            row.put("last_name", u.getLastName());
            row.put("password_hash", u.getPasswordHash().toString());
            row.put("date_of_birth", u.getDateOfBirth().toString());
            row.put("phone_number", u.getPhoneNumber());
            data.add(row);
          }
        } catch (Exception ex) {
          System.err.println("Error making user admin:\n" + ex);
          ex.printStackTrace(System.err);
          errorDialog("Error making user admin");
        }
      });

      Button btnMakeDoctor = new Button("Make Doctor");
      btnMakeDoctor.setOnAction(e -> {
        HashMap<String, String> selected =
            table.getSelectionModel().getSelectedItem();
        if (selected == null) {
          errorDialog("No user selected");
          return;
        }
        Stage newStage = new Stage();
        VBox vb = new VBox(10);
        GridPane gp = new GridPane();

        Label departmentLabel = new Label("Department: ");
        TextField departmentField = new TextField();
        gp.add(departmentLabel, 0, 0);
        gp.add(departmentField, 1, 0);

        Button btnContinue = new Button("Continue");
        btnContinue.setOnAction(e1 -> {
          try {
            User user =
                userDao.queryForId(UUID.fromString(selected.get("user_id")));
            Doctor doctor = new Doctor(user, departmentField.getText());
            doctorDao.create(doctor);
            data.clear();
            users[0] = userDao.queryForAll();
            for (User u : users[0]) {
              HashMap<String, String> row = new HashMap<>();
              row.put("user_id", u.getUserId().toString());
              row.put("username", u.getUsername());
              row.put("email", u.getEmail());
              row.put("first_name", u.getFirstName());
              row.put("middle_name", u.getMiddleName());
              row.put("last_name", u.getLastName());
              row.put("password_hash", u.getPasswordHash().toString());
              row.put("date_of_birth", u.getDateOfBirth().toString());
              row.put("phone_number", u.getPhoneNumber());
              data.add(row);
            }
            newStage.close();
          } catch (Exception ex) {
            System.err.println("Error making user doctor:\n" + ex);
            ex.printStackTrace(System.err);
            errorDialog("Error making user doctor");
          }
        });

        vb.getChildren().addAll(gp, btnContinue);
        newStage.setTitle("Enter details");
        newStage.setScene(new Scene(vb, 300, 200));
        newStage.show();
      });

      root.getChildren().addAll(table, btnRefresh, btnAddUser, btnDeleteUser,
                                btnMakeAdmin, btnMakeDoctor);
    }

    Button btnGoToMainWindow = new Button("Go to main window");
    btnGoToMainWindow.setOnAction(goToMainWindow);

    root.getChildren().add(btnGoToMainWindow);

    return new Scene(root, 700, 700);
  }
}
