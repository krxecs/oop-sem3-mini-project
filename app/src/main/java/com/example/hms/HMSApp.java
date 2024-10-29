package com.example.hms;

import java.util.*;

import com.example.hms.auth.ListAppointments;
import com.example.hms.auth.LoginPage;
import com.example.hms.auth.admin.UserModification;
import com.example.hms.util.PatientAppointment;
import com.example.hms.util.PatientAppointmentDAO;
import com.example.hms.util.RunOnChange;
import com.example.hms.util.auth.*;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

public final class HMSApp extends Application {
  Stage primaryStage;
  Scene primaryScene;
  RunOnChange<User> loggedInUser;

  //Scene adminRegistrationForm(UserDAO userDAO)
  public static Scene userRegistrationForm(UserDAO userDAO, PatientDAO patientDAO, DoctorDAO doctorDAO, Scene nextScene, Dao<Administrator, Long> adminDAO, boolean is_admin, EventHandler<ActionEvent> goToMainWindow, Stage primaryStage) {
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

    Label bloodGroupLabel = new Label("Blood Group: ");
    TextField bloodGroupField = new TextField();
    gp.add(bloodGroupLabel, 0, 6);
    gp.add(bloodGroupField, 1, 6);

    Label usernameLabel = new Label("Username: ");
    TextField usernameField = new TextField();
    gp.add(usernameLabel, 0, 7);
    gp.add(usernameField, 1, 7);

    Label passwordLabel = new Label("Password: ");
    PasswordField passwordField = new PasswordField();
    gp.add(passwordLabel, 0, 8);
    gp.add(passwordField, 1, 8);

    Label confirmPasswordLabel = new Label("Confirm Password: ");
    PasswordField confirmPasswordField = new PasswordField();
    gp.add(confirmPasswordLabel, 0, 9);
    gp.add(confirmPasswordField, 1, 9);

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
          phoneNumberField.getText(),
          bloodGroupField.getText()
        );
        lblStatus.setText("User registered successfully");
        if (nextScene != null) {
          primaryStage.setScene(nextScene);
        }

        if (is_admin) {
          Administrator admin = new Administrator(user);
          adminDAO.create(admin);
        }
      } catch (Exception ex) {
        lblStatus.setText("Error registering user");
        System.err.println("Error registering user:\n" + ex);
        ex.printStackTrace(System.err);
      }
    });

    Button btnGoToMainWindow = new Button("Go to main window");
    btnGoToMainWindow.setOnAction(goToMainWindow);

    root.getChildren().addAll(gp, btnRegister, btnGoToMainWindow, lblStatus);
    return new Scene(root, 300, 200);
  }

  void patientAppointmentForm(PatientDAO patientDAO, PatientAppointmentDAO paDAO, DoctorDAO doctorDAO, User user, EventHandler<ActionEvent> goToMainWindow, EventHandler<ActionEvent> onSuccess) {
    VBox root = new VBox(10);
    root.setAlignment(Pos.CENTER);

    GridPane gp = new GridPane();
    gp.setAlignment(Pos.CENTER);
    gp.setHgap(10);
    gp.setVgap(10);

    Label doctorLabel = new Label("Doctor: ");
    ComboBox<Doctor> comboDoctor = new ComboBox<>();
    Callback<ListView<Doctor>, ListCell<Doctor>> callback = new Callback<>() {
      @Override
      public ListCell<Doctor> call(ListView<Doctor> p) {
        return new ListCell<>() {
          @Override
          protected void updateItem(Doctor doctor, boolean empty) {
            super.updateItem(doctor, empty);
            if (doctor == null || empty)
              setText("");
            else
              setText(doctor.getUser().getFirstName() + " " + doctor.getUser().getLastName());
          }
        };
      }
    };
    List<Doctor> doctors = new LinkedList<>();
    try {
      doctors = doctorDAO.queryForAll();
    } catch (Exception ex) {
      System.err.println("Error getting doctors:\n" + ex);
      ex.printStackTrace(System.err);
    }
    comboDoctor.setCellFactory(callback);
    comboDoctor.setButtonCell(callback.call(null));
    comboDoctor.getItems().addAll(doctors);
    gp.add(doctorLabel, 0, 0);
    gp.add(comboDoctor, 1, 0);

    Label dateTimeOfVisitLabel = new Label("Date and Time of Visit: ");
    DatePicker dateTimeOfVisitField = new DatePicker();
    gp.add(dateTimeOfVisitLabel, 0, 1);
    gp.add(dateTimeOfVisitField, 1, 1);

    Label reasonForVisitLabel = new Label("Reason for Visit: ");
    TextField reasonForVisitField = new TextField();
    gp.add(reasonForVisitLabel, 0, 3);
    gp.add(reasonForVisitField, 1, 3);

    Label diagnosisLabel = new Label("Diagnosis: ");
    TextField diagnosisField = new TextField();
    gp.add(diagnosisLabel, 0, 4);
    gp.add(diagnosisField, 1, 4);

    Button btnRegister = new Button("Register");

    Label lblStatus = new Label();

    btnRegister.setOnAction(e -> {
      Date dateTimeOfVisit = Date.from(dateTimeOfVisitField.getValue().atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
      try {
        paDAO.addAppointmentForPatient(
          patientDAO.getPatientObjectForUser(user),
          dateTimeOfVisit,
          comboDoctor.getValue(),
          reasonForVisitField.getText(),
          diagnosisField.getText()
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

    Scene rootScene = new Scene(root, 300, 200);

    if (patientDAO.getPatientObjectForUser(user) != null) {
      root.getChildren().addAll(gp, btnRegister, btnGoToMainWindow, lblStatus);
    } else {
      Scene patientRegistrationScene = patientRegistrationForm(patientDAO, user, e -> primaryStage.setScene(rootScene));
      this.primaryStage.setScene(patientRegistrationScene);
    }

    root.getChildren().addAll(gp, btnRegister, btnGoToMainWindow, lblStatus);
    this.primaryStage.setScene(rootScene);
  }

  Scene doctorRegistrationForm(DoctorDAO doctorDAO, User user, EventHandler<ActionEvent> onSuccess) {
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
        onSuccess.handle(e);
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

  void goToMainWindow() { primaryStage.setScene(this.primaryScene); }

  Scene patientRegistrationForm(PatientDAO patientDAO, User user, EventHandler<ActionEvent> onSuccess) {
    VBox root = new VBox(10);
    root.setAlignment(Pos.CENTER);

    GridPane gp = new GridPane();
    gp.setAlignment(Pos.CENTER);

    Label smokingAndAlcoholStatusLabel = new Label("Smoking and Alcohol Status: ");
    TextField smokingAndAlcoholStatusField = new TextField();
    gp.add(smokingAndAlcoholStatusLabel, 0, 0);
    gp.add(smokingAndAlcoholStatusField, 1, 0);

    Label additionalNotesLabel = new Label("Additional Notes: ");
    TextField additionalNotesField = new TextField();
    gp.add(additionalNotesLabel, 0, 1);
    gp.add(additionalNotesField, 1, 1);

    Button btnRegister = new Button("Register");

    Label lblStatus = new Label();

    btnRegister.setOnAction(e -> {
      try {
        patientDAO.addPatientForUser(user, smokingAndAlcoholStatusField.getText(), additionalNotesField.getText());
        lblStatus.setText("Patient registered successfully");
        onSuccess.handle(e);
      } catch (Exception ex) {
        errorDialog("Error registering patient");
        System.err.println("Error registering patient:\n" + ex);
        ex.printStackTrace(System.err);
      }
    });

    Button btnGoToMainWindow = new Button("Go to main window");
    btnGoToMainWindow.setOnAction(e -> goToMainWindow());

    root.getChildren().addAll(gp, btnRegister, btnGoToMainWindow, lblStatus);

    return new Scene(root, 300, 200);
  }

  Scene loggingInAdminPage(PatientDAO patientDAO, DoctorDAO doctorDAO, UserDAO userDAO, AdministratorDAO adminDAO, PatientAppointmentDAO paDAO, RunOnChange<User> loggedInUser) {
    VBox root = new VBox(10);
    root.setAlignment(Pos.CENTER);

    Label lblUserStatus = new Label("User logged in: " + loggedInUser.get().getUsername());

    Button btnDoctorRegistration = new Button("Doctor registration");
    btnDoctorRegistration.setOnAction(e -> {
      primaryStage.setScene(doctorRegistrationForm(doctorDAO, loggedInUser.get(), e1 -> goToMainWindow()));
    });

    Button btnPatientAppointment = new Button("Patient appointment");
    btnPatientAppointment.setOnAction(e -> {
      patientAppointmentForm(patientDAO, paDAO, doctorDAO, loggedInUser.get(), e1 -> goToMainWindow(), e1 -> goToMainWindow());
    });

    Button btnUserMod = new Button("User modification");
    UserModification um = new UserModification(userDAO, patientDAO, doctorDAO, adminDAO, primaryStage);
    Scene userModScene = um.getScene(e1 -> goToMainWindow());
    btnUserMod.setOnAction(e -> primaryStage.setScene(userModScene));

    Button btnLogout = new Button("Logout");
    btnLogout.setOnAction(e -> {
      loggedInUser.set(null);
      primaryStage.setScene(this.primaryScene);
    });

    root.getChildren().addAll(lblUserStatus, btnDoctorRegistration, btnPatientAppointment, btnUserMod, btnLogout);
    return new Scene(root, 300, 200);
  }

  Scene loggedInPage(PatientDAO patientDAO, DoctorDAO doctorDAO, UserDAO userDAO, PatientAppointmentDAO paDAO, RunOnChange<User> loggedInUser) {
    VBox root = new VBox(10);
    root.setAlignment(Pos.CENTER);

    Label lblUserStatus = new Label("User logged in: " + loggedInUser.get().getUsername());

    Button btnPatientAppointment = new Button("Patient appointment");
    btnPatientAppointment.setOnAction(e -> {
      patientAppointmentForm(patientDAO, paDAO, doctorDAO, loggedInUser.get(), e1 -> goToMainWindow(), e1 -> goToMainWindow());
    });

    if (doctorDAO.getDoctorObjectForUser(loggedInUser.get()) != null) {
      Button btnListPatients = new Button("List patients");
      btnListPatients.setOnAction(e -> {
        Doctor d;
        try {
          d = doctorDAO.getDoctorObjectForUser(loggedInUser.get());
        } catch (Exception ex) {
          errorDialog("You must be a doctor to list patients");
          return;
        }
        primaryStage.setScene(ListAppointments.getScene(patientDAO, paDAO, d, e1 -> goToMainWindow()));
      });
      root.getChildren().add(btnListPatients);
    }

    Button btnLogout = new Button("Logout");
    btnLogout.setOnAction(e -> {
      loggedInUser.set(null);
      primaryStage.setScene(this.primaryScene);
    });

    root.getChildren().addAll(lblUserStatus, btnPatientAppointment, btnLogout);
    return new Scene(root, 300, 200);
  }

  Scene loggedOutPage(UserDAO userDAO, PatientDAO patientDAO, DoctorDAO doctorDAO, PatientAppointmentDAO paDAO, AdministratorDAO adminDAO, RunOnChange<User> user) {
    VBox root = new VBox(10);
    root.setAlignment(Pos.CENTER);

    Label lblUserStatus = new Label("User not logged in");

    Button btnLogin = new Button("Login");
    btnLogin.setOnAction(e -> {
      LoginPage lp = new LoginPage(user);
      primaryStage.setScene(lp.getScene(userDAO, event -> {
        try {
          System.out.println("User logged in: " + user.get().getUsername());
          if (adminDAO.getAdministratorObjectForUser(user.get()) != null) {
            System.out.println("User is admin");
            primaryStage.setScene(loggingInAdminPage(patientDAO, doctorDAO, userDAO, adminDAO, paDAO, user));
          } else
            primaryStage.setScene(loggedInPage(patientDAO, doctorDAO, userDAO, paDAO, user));
        } catch (Exception ex) {
          System.err.println("Error logging in admin:\n" + ex);
          ex.printStackTrace(System.err);
        }
      }, user));
    });

    Button btnRegister = new Button("Register");
    btnRegister.setOnAction(e -> primaryStage.setScene(HMSApp.userRegistrationForm(userDAO, patientDAO, doctorDAO, primaryScene, adminDAO, false, ev -> goToMainWindow(), primaryStage)));

    root.getChildren().addAll(lblUserStatus, btnLogin, btnRegister);
    return new Scene(root, 300, 200);
  }

  void errorDialog(String err) {
    Alert alert = new Alert(Alert.AlertType.ERROR, "Error");
    alert.setHeaderText(null);
    alert.setContentText(err);
    alert.showAndWait();
  }

  Scene firstUseForm(UserDAO userDAO, PatientDAO patientDAO, DoctorDAO doctorDAO, AdministratorDAO adminDAO) {
    VBox root = new VBox(10);
    root.setAlignment(Pos.CENTER);

    Label lblFirstUse = new Label("First use detected. Please register an admin user.");

    Button btnRegisterAdmin = new Button("Register admin user");
    btnRegisterAdmin.setOnAction(e -> primaryStage.setScene(HMSApp.userRegistrationForm(userDAO, patientDAO, doctorDAO, this.primaryScene, adminDAO, true, ev -> goToMainWindow(), primaryStage)));

    root.getChildren().addAll(lblFirstUse, btnRegisterAdmin);
    return new Scene(root, 300, 200);
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
      AdministratorDAO adminDAO = DaoManager.createDao(connectionSource, Administrator.class);
      PatientAppointmentDAO patientAppointmentDao = DaoManager.createDao(connectionSource, PatientAppointment.class);
      TableUtils.createTableIfNotExists(connectionSource, User.class);
      TableUtils.createTableIfNotExists(connectionSource, Patient.class);
      TableUtils.createTableIfNotExists(connectionSource, Doctor.class);
      TableUtils.createTableIfNotExists(connectionSource, PatientAppointment.class);
      TableUtils.createTableIfNotExists(connectionSource, Administrator.class);

      //Label lblUserStatus = new Label("User not logged in");

      //loggedInUser = new RunOnChange<>(null, () -> {
      //  if (loggedInUser.get() != null) {
      //    lblUserStatus.setText("User logged in: " + loggedInUser.get().getUsername());
      //  } else {
      //    lblUserStatus.setText("User not logged in");
      //  }
      //});

      //Button btnLogin = new Button("Login");
      //btnLogin.setOnAction(e -> {
      //  LoginPage lp = new LoginPage(loggedInUser);
      //  primaryStage.setScene(lp.getScene(userDao, event -> primaryStage.setScene(this.primaryScene), loggedInUser));
      //});

      //Button btnPatientRegister = new Button("User registration");
      //btnPatientRegister.setOnAction(e -> primaryStage.setScene(userRegistrationForm(userDao, patientDao, doctorDao, null)));

      ////Button btnDoctorRegister = new Button("Doctor registration");
      ////btnDoctorRegister.setOnAction(e -> primaryStage.setScene(userRegistrationForm(userDao, patientDao, doctorDao));

      //Button btnListUsers = new Button("List users");
      //btnListUsers.setOnAction(e -> {
      //  if (loggedInUser.get() == null) {
      //    errorDialog("You must be logged in to list users.");
      //    return;
      //  }
      //  primaryStage.setScene(userList(userDao));
      //});

      //Button btnListPatients = new Button("List patients");
      //btnListPatients.setOnAction(e -> {
      //  Doctor d;
      //  if (loggedInUser.get() == null) {
      //    Dialog<String> dialog = new Dialog<>();
      //    dialog.setTitle("Error");
      //    dialog.setContentText("You must be logged in to list patients");
      //    dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
      //    dialog.showAndWait();
      //    return;
      //  }

      //  try {
      //    d = doctorDao.getDoctorObjectForUser(loggedInUser.get());
      //  } catch (Exception ex) {
      //    errorDialog("You must be a doctor to list patients");
      //    return;
      //  }
      //  primaryStage.setScene(ListAppointments.getScene(patientDao, patientAppointmentDao, d, e1 -> goToMainWindow()));
      //});

      //Button btnExit = new Button("Exit");
      //btnExit.setOnAction(e -> primaryStage.close());

      //root.getChildren().addAll(lblUserStatus, btnLogin, btnPatientRegister, /*btnDoctorRegister,*/ btnListUsers, btnListPatients, btnExit);

      //this.primaryScene = new Scene(root, 700, 700);
      loggedInUser = new RunOnChange<>(null, () -> {
        if (loggedInUser.get() != null) {
          if (adminDAO.getAdministratorObjectForUser(loggedInUser.get()) != null) {
            this.primaryScene = loggingInAdminPage(patientDao, doctorDao, userDao, adminDAO, patientAppointmentDao, loggedInUser);
          } else {
            this.primaryScene = loggedInPage(patientDao, doctorDao, userDao, patientAppointmentDao, loggedInUser);
          }
        } else {
          this.primaryScene = loggedOutPage(userDao, patientDao, doctorDao, patientAppointmentDao, adminDAO, loggedInUser);
        }

        primaryStage.setScene(this.primaryScene);
      });
      Scene rootScene = loggedOutPage(userDao, patientDao, doctorDao, patientAppointmentDao, adminDAO, loggedInUser);
      this.primaryStage = primaryStage;
      this.primaryScene = rootScene;

      if (userDao.queryForAll().isEmpty()) {
        primaryStage.setScene(firstUseForm(userDao, patientDao, doctorDao, adminDAO));
      } else {
        primaryStage.setScene(rootScene);
      }
      primaryStage.show();
    } catch (Exception ex) {
      System.err.println("Error creating connection source:\n" + ex);
      ex.printStackTrace(System.err);
      System.exit(1);
    }
  }
}
