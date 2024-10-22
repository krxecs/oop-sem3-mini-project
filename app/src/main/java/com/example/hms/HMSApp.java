package com.example.hms;

import com.example.hms.auth.*;

import java.lang.reflect.Field;
import java.util.*;

import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;
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

class RunOnChange<T> {
  private T value;
  private final Runnable onChange;

  public RunOnChange(T value, Runnable onChange) {
    this.value = value;
    this.onChange = onChange;
  }

  public T get() { return value; }

  public void set(T value) {
    this.value = value;
    onChange.run();
  }
}

public final class HMSApp extends Application {
  Stage primaryStage;
  Scene primaryScene;
  RunOnChange<User> loggedInUser;

  //Scene adminRegistrationForm(UserDAO userDAO)
  Scene userRegistrationForm(UserDAO userDAO, PatientDAO patientDAO, DoctorDAO doctorDAO, String nextScene) {
    VBox root = new VBox(10);
    root.setAlignment(Pos.CENTER);

    GridPane gp = new GridPane();
    gp.setAlignment(Pos.CENTER);
    gp.setHgap(10);
    gp.setVgap(10);

    Label emailLabel = new Label("Email: ");
    TextField emailField = new TextField();
    gp.add(emailLabel, 0, 0);
    gp.add(emailField, 1, 0);

    Label firstNameLabel = new Label("First Name: ");
    TextField firstNameField = new TextField();
    gp.add(firstNameLabel, 0, 1);
    gp.add(firstNameField, 1, 1);

    Label middleNameLabel = new Label("Middle Name: ");
    TextField middleNameField = new TextField();
    gp.add(middleNameLabel, 0, 2);
    gp.add(middleNameField, 1, 2);

    Label lastNameLabel = new Label("Last Name: ");
    TextField lastNameField = new TextField();
    gp.add(lastNameLabel, 0, 3);
    gp.add(lastNameField, 1, 3);

    Label dobLabel = new Label("Date of Birth: ");
    DatePicker dobField = new DatePicker();
    gp.add(dobLabel, 0, 4);
    gp.add(dobField, 1, 4);

    Label phoneNumberLabel = new Label("Phone Number: ");
    TextField phoneNumberField = new TextField();
    gp.add(phoneNumberLabel, 0, 5);
    gp.add(phoneNumberField, 1, 5);

    Label usernameLabel = new Label("Username: ");
    TextField usernameField = new TextField();
    gp.add(usernameLabel, 0, 6);
    gp.add(usernameField, 1, 6);

    Label passwordLabel = new Label("Password: ");
    PasswordField passwordField = new PasswordField();
    gp.add(passwordLabel, 0, 7);
    gp.add(passwordField, 1, 7);

    Label confirmPasswordLabel = new Label("Confirm Password: ");
    PasswordField confirmPasswordField = new PasswordField();
    gp.add(confirmPasswordLabel, 0, 8);
    gp.add(confirmPasswordField, 1, 8);

    Button btnRegister = new Button("Register");

    Label lblStatus = new Label();

    btnRegister.setOnAction(e -> {
      if (!passwordField.getText().equals(confirmPasswordField.getText())) {
        lblStatus.setText("Passwords do not match");
        return;
      }
      try {
        User user = userDAO.addUser(
          usernameField.getText(),
          passwordField.getText().toCharArray(),
          emailField.getText(),
          firstNameField.getText(),
          middleNameField.getText(),
          lastNameField.getText(),
          java.sql.Date.valueOf(dobField.getValue()),
          phoneNumberField.getText()
        );
        lblStatus.setText("User registered successfully");
        switch (nextScene) {
          case "patientRegistrationForm":
            primaryStage.setScene(patientRegistrationForm(patientDAO, user));
            break;
          case "doctorRegistrationForm":
            primaryStage.setScene(doctorRegistrationForm(doctorDAO, user));
            break;
          default:
            throw new Exception("Invalid next scene");
        }
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

  Scene patientRegistrationForm(PatientDAO patientDAO, User user) {
    VBox root = new VBox(10);
    root.setAlignment(Pos.CENTER);

    GridPane gp = new GridPane();
    gp.setAlignment(Pos.CENTER);
    gp.setHgap(10);
    gp.setVgap(10);

    Label dateTimeOfVisitLabel = new Label("Date and Time of Visit: ");
    DatePicker dateTimeOfVisitField = new DatePicker();
    gp.add(dateTimeOfVisitLabel, 0, 0);
    gp.add(dateTimeOfVisitField, 1, 0);

    Label bloodGroupLabel = new Label("Blood Group: ");
    TextField bloodGroupField = new TextField();
    gp.add(bloodGroupLabel, 0, 1);
    gp.add(bloodGroupField, 1, 1);

    Label reasonForVisitLabel = new Label("Reason for Visit: ");
    TextField reasonForVisitField = new TextField();
    gp.add(reasonForVisitLabel, 0, 2);
    gp.add(reasonForVisitField, 1, 2);

    Label diagnosisLabel = new Label("Diagnosis: ");
    TextField diagnosisField = new TextField();
    gp.add(diagnosisLabel, 0, 3);
    gp.add(diagnosisField, 1, 3);

    Label smokingAndAlcoholStatusLabel = new Label("Smoking and Alcohol Status: ");
    TextField smokingAndAlcoholStatusField = new TextField();
    gp.add(smokingAndAlcoholStatusLabel, 0, 4);
    gp.add(smokingAndAlcoholStatusField, 1, 4);

    Label additionalNotesLabel = new Label("Additional Notes: ");
    TextField additionalNotesField = new TextField();
    gp.add(additionalNotesLabel, 0, 5);
    gp.add(additionalNotesField, 1, 5);

    Button btnRegister = new Button("Register");

    Label lblStatus = new Label();

    btnRegister.setOnAction(e -> {
      Date dateTimeOfVisit = Date.from(dateTimeOfVisitField.getValue().atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
      try {
        patientDAO.addPatientForUser(
          user,
          bloodGroupField.getText(),
          dateTimeOfVisit,
          reasonForVisitField.getText(),
          diagnosisField.getText(),
          smokingAndAlcoholStatusField.getText(),
          additionalNotesField.getText()
        );
        lblStatus.setText("Patient registered successfully");
      } catch (Exception ex) {
        lblStatus.setText("Error registering patient");
        System.err.println("Error registering patient:\n" + ex);
        ex.printStackTrace(System.err);
      }
    });

    Button btnGoToMainWindow = new Button("Go to main window");
    btnGoToMainWindow.setOnAction(e -> goToMainWindow());

    root.getChildren().addAll(gp, btnRegister, btnGoToMainWindow, lblStatus);
    return new Scene(root, 300, 200);
  }

  Scene doctorRegistrationForm(DoctorDAO doctorDAO, User user) {
    VBox root = new VBox(10);
    root.setAlignment(Pos.CENTER);

    GridPane gp = new GridPane();
    gp.setAlignment(Pos.CENTER);

    Label departmentLabel = new Label("Department: ");
    TextField departmentField = new TextField();
    gp.add(departmentLabel, 0, 0);
    gp.add(departmentField, 1, 0);

    Button btnRegister = new Button("Register");

    Label lblStatus = new Label();

    btnRegister.setOnAction(e -> {
      try {
        doctorDAO.addDoctorForUser(user, departmentField.getText());
        lblStatus.setText("Doctor registered successfully");
      } catch (Exception ex) {
        lblStatus.setText("Error registering doctor");
        System.err.println("Error registering doctor:\n" + ex);
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
        loggedInUser.set(user);
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
        row.put("date_of_birth", user.getDateOfBirth().toString());
        row.put("phone_number", user.getPhoneNumber());
        data.add(row);
      }

      TableColumn<HashMap<String, String>, String> userIdCol = new TableColumn<>("User ID");
      userIdCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get("user_id")));
      table.getColumns().add(userIdCol);

      TableColumn<HashMap<String, String>, String> usernameCol = new TableColumn<>("Username");
      usernameCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get("username")));
      table.getColumns().add(usernameCol);

      TableColumn<HashMap<String, String>, String> emailCol = new TableColumn<>("Email");
      emailCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get("email")));
      table.getColumns().add(emailCol);

      TableColumn<HashMap<String, String>, String> firstNameCol = new TableColumn<>("First Name");
      firstNameCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get("first_name")));
      table.getColumns().add(firstNameCol);

      TableColumn<HashMap<String, String>, String> middleNameCol = new TableColumn<>("Middle Name");
      middleNameCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get("middle_name")));
      table.getColumns().add(middleNameCol);

      TableColumn<HashMap<String, String>, String> lastNameCol = new TableColumn<>("Last Name");
      lastNameCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get("last_name")));
      table.getColumns().add(lastNameCol);

      TableColumn<HashMap<String, String>, String> passwordHashCol = new TableColumn<>("Password Hash");
      passwordHashCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get("password_hash")));
      table.getColumns().add(passwordHashCol);

      TableColumn<HashMap<String, String>, String> dateOfBirthCol = new TableColumn<>("Date of Birth");
      dateOfBirthCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get("date_of_birth")));
      table.getColumns().add(dateOfBirthCol);

      TableColumn<HashMap<String, String>, String> phoneNumberCol = new TableColumn<>("Phone Number");
      phoneNumberCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get("phone_number")));
      table.getColumns().add(phoneNumberCol);

      table.setItems(data);
    }

    Button btnGoToMainWindow = new Button("Go to main window");
    btnGoToMainWindow.setOnAction(e -> goToMainWindow());

    Scene scene = new Scene(root, 300, 200);
    root.getChildren().addAll(table, btnGoToMainWindow);
    return scene;
  }

  Scene listPatients(PatientDAO patientDAO) {
    VBox root = new VBox(10);
    root.setAlignment(Pos.CENTER);

    TableView<HashMap<String, String>> table = new TableView<>();
    table.setEditable(false);

    List<Patient> patients = new LinkedList<>();
    boolean success = false;
    try {
      patients = patientDAO.queryForAll();
      success = true;
    } catch (Exception ex) {
      System.err.println("Error listing patients:\n" + ex);
      ex.printStackTrace(System.err);
    }

    if (success) {
      ObservableList<HashMap<String, String>> data = FXCollections.observableArrayList();
      for (Patient patient : patients) {
        HashMap<String, String> row = new HashMap<>();
        row.put("patient_id", Long.toString(patient.getPatientId()));
        row.put("user_id", patient.getUser().getUserId().toString());
        row.put("first_name", patient.getUser().getFirstName());
        row.put("last_name", patient.getUser().getLastName());
        row.put("blood_group", patient.getBloodGroup());
        row.put("date_time_of_appointment", patient.getDateTimeOfAppointment().toString());
        row.put("reason_for_visit", patient.getReasonForVisit());
        row.put("diagnosis", patient.getDiagnosis());
        row.put("smoking_and_alcohol_status", patient.getSmokingAndAlcoholStatus());
        row.put("additional_notes", patient.getAdditionalNotes());

        System.out.println(patient.getDateTimeOfAppointment().toString());
        data.add(row);
      }

      //for (Field colName : patientDAO.getTableInfo().getDataClass().getDeclaredFields()) {
      //  TableColumn<HashMap<String, String>, String> col = new TableColumn<>(colName.getName());
      //  col.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(colName.getName())));
      //  table.getColumns().add(col);
      //  System.out.println(colName.getName());
      //}

      TableColumn<HashMap<String, String>, String> patientIdCol = new TableColumn<>("Patient ID");
      patientIdCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get("patient_id")));
      table.getColumns().add(patientIdCol);

      TableColumn<HashMap<String, String>, String> userIdCol = new TableColumn<>("User ID");
      userIdCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get("user_id")));
      table.getColumns().add(userIdCol);

      TableColumn<HashMap<String, String>, String> firstNameCol = new TableColumn<>("First Name");
      firstNameCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get("first_name")));
      table.getColumns().add(firstNameCol);

      TableColumn<HashMap<String, String>, String> lastNameCol = new TableColumn<>("Last Name");
      lastNameCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get("last_name")));
      table.getColumns().add(lastNameCol);

      TableColumn<HashMap<String, String>, String> bloodGroupCol = new TableColumn<>("Blood Group");
      bloodGroupCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get("blood_group")));
      table.getColumns().add(bloodGroupCol);

      TableColumn<HashMap<String, String>, String> dateTimeOfAppointmentCol = new TableColumn<>("Date and Time of Appointment");
      dateTimeOfAppointmentCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get("date_time_of_appointment")));
      table.getColumns().add(dateTimeOfAppointmentCol);

      TableColumn<HashMap<String, String>, String> reasonForVisitCol = new TableColumn<>("Reason for Visit");
      reasonForVisitCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get("reason_for_visit")));
      table.getColumns().add(reasonForVisitCol);

      TableColumn<HashMap<String, String>, String> diagnosisCol = new TableColumn<>("Diagnosis");
      diagnosisCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get("diagnosis")));
      table.getColumns().add(diagnosisCol);

      TableColumn<HashMap<String, String>, String> smokingAndAlcoholStatusCol = new TableColumn<>("Smoking and Alcohol Status");
      smokingAndAlcoholStatusCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get("smoking_and_alcohol_status")));
      table.getColumns().add(smokingAndAlcoholStatusCol);

      TableColumn<HashMap<String, String>, String> additionalNotesCol = new TableColumn<>("Additional Notes");
      additionalNotesCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get("additional_notes")));
      table.getColumns().add(additionalNotesCol);

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

    VBox root = new VBox(10);
    root.setAlignment(Pos.CENTER);

    try(JdbcConnectionSource connectionSource = new JdbcConnectionSource("jdbc:sqlite:db.sqlite3", "sa", "")) {
      UserDAO userDao = DaoManager.createDao(connectionSource, User.class);
      PatientDAO patientDao = DaoManager.createDao(connectionSource, Patient.class);
      DoctorDAO doctorDao = DaoManager.createDao(connectionSource, Doctor.class);
      TableUtils.createTableIfNotExists(connectionSource, User.class);
      TableUtils.createTableIfNotExists(connectionSource, Patient.class);
      TableUtils.createTableIfNotExists(connectionSource, Doctor.class);

      Label lblUserStatus = new Label("User not logged in");

      loggedInUser = new RunOnChange<>(null, () -> {
        if (loggedInUser.get() != null) {
          lblUserStatus.setText("User logged in: " + loggedInUser.get().getUsername());
        } else {
          lblUserStatus.setText("User not logged in");
        }
      });

      Button btnLogin = new Button("Login");
      btnLogin.setOnAction(e -> {
        Scene sceneLoginForm = loginForm(userDao);
        primaryStage.setScene(sceneLoginForm);
      });

      Button btnPatientRegister = new Button("Patient registration");
      btnPatientRegister.setOnAction(e -> primaryStage.setScene(userRegistrationForm(userDao, patientDao, doctorDao, "patientRegistrationForm")));

      Button btnDoctorRegister = new Button("Doctor registration");
      btnDoctorRegister.setOnAction(e -> primaryStage.setScene(userRegistrationForm(userDao, patientDao, doctorDao, "doctorRegistrationForm")));

      Button btnListUsers = new Button("List users");
      btnListUsers.setOnAction(e -> {
        if (loggedInUser.get() == null) {
          Dialog<String> dialog = new Dialog<>();
          dialog.setTitle("Error");
          dialog.setContentText("You must be logged in to list users");
          dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
          dialog.showAndWait();
          return;
        }
        primaryStage.setScene(userList(userDao));
      });

      Button btnListPatients = new Button("List patients");
      btnListPatients.setOnAction(e -> {
        if (loggedInUser.get() == null) {
          Dialog<String> dialog = new Dialog<>();
          dialog.setTitle("Error");
          dialog.setContentText("You must be logged in to list patients");
          dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
          dialog.showAndWait();
          return;
        }
        primaryStage.setScene(listPatients(patientDao));
      });

      Button btnExit = new Button("Exit");
      btnExit.setOnAction(e -> primaryStage.close());

      root.getChildren().addAll(lblUserStatus, btnLogin, btnPatientRegister, btnDoctorRegister, btnListUsers, btnListPatients, btnExit);

      this.primaryScene = new Scene(root, 700, 700);
      primaryStage.setScene(this.primaryScene);
      primaryStage.show();
    } catch (Exception ex) {
      System.err.println("Error creating connection source:\n" + ex);
      ex.printStackTrace(System.err);
      System.exit(1);
    }
  }
}
