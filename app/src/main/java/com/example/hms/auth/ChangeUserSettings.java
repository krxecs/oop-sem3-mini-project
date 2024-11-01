package com.example.hms.auth;

import com.example.hms.util.RunOnChange;
import com.example.hms.util.auth.DoctorDAO;
import com.example.hms.util.auth.PatientDAO;
import com.example.hms.util.auth.User;
import com.example.hms.util.auth.UserDAO;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.StringWriter;
import java.sql.SQLException;

public class ChangeUserSettings {
  private RunOnChange<User> user;
  private EventHandler<ActionEvent> goToMainWindow;

  public ChangeUserSettings(RunOnChange<User> user, EventHandler<ActionEvent> goToMainWindow) {
    this.user = user;
    this.goToMainWindow = goToMainWindow;
  }

  public javafx.scene.Parent passwordChangeParent(UserDAO userDAO) {
    VBox root = new VBox(10);
    root.setAlignment(Pos.CENTER);

    Label lblPasswordChange = new Label("Password Change");

    GridPane passwordChangeGridPane = new GridPane();
    passwordChangeGridPane.setHgap(10);
    passwordChangeGridPane.setVgap(10);

    Label lblCurrentPassword = new Label("Current Password:");
    PasswordField txtCurrentPassword = new PasswordField();
    passwordChangeGridPane.add(lblCurrentPassword, 0, 0);
    passwordChangeGridPane.add(txtCurrentPassword, 1, 0);

    Label lblNewPassword = new Label("New Password:");
    PasswordField txtNewPassword = new PasswordField();
    passwordChangeGridPane.add(lblNewPassword, 0, 1);
    passwordChangeGridPane.add(txtNewPassword, 1, 1);

    Label lblConfirmNewPassword = new Label("Confirm New Password:");
    PasswordField txtConfirmNewPassword = new PasswordField();
    passwordChangeGridPane.add(lblConfirmNewPassword, 0, 2);
    passwordChangeGridPane.add(txtConfirmNewPassword, 1, 2);

    Button btnChangePassword = new Button("Change Password");
    btnChangePassword.setOnAction(e -> {
      if (!txtNewPassword.getText().equals(txtConfirmNewPassword.getText())) {
        Alert alert = new Alert(Alert.AlertType.ERROR, "New password and confirm new password do not match");
        alert.showAndWait();
        return;
      }
      try {
        userDAO.changePassword(user.get(), txtCurrentPassword.getText().toCharArray(), txtNewPassword.getText().toCharArray());
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Password changed successfully");
        alert.showAndWait();
      } catch (Exception ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR, "Error changing password");
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new java.io.PrintWriter(sw));
        alert.setContentText(sw.toString());
        alert.showAndWait();
        ex.printStackTrace();
      }
    });

    root.getChildren().addAll(lblPasswordChange, passwordChangeGridPane, btnChangePassword);

    return root;
  }

  public Scene getScene(UserDAO userDAO, DoctorDAO doctorDAO, PatientDAO patientDAO) {
    HBox root = new HBox(10);
    root.setAlignment(Pos.CENTER);

    VBox userSettingsVBox = new VBox(10);
    userSettingsVBox.setAlignment(Pos.CENTER);

    Label lblUserSettings = new Label("User Settings");

    GridPane userGridPane = new GridPane();
    userGridPane.setHgap(10);
    userGridPane.setVgap(10);

    Label lblUserId = new Label("User ID:");
    Label lblUserIdActual = new Label(user.get().getUserId().toString());
    userGridPane.add(lblUserId, 0, 0);
    userGridPane.add(lblUserIdActual, 1, 0);

    Label lblUsername = new Label("Username:");
    TextField txtUsername = new TextField(user.get().getUsername());
    userGridPane.add(lblUsername, 0, 1);
    userGridPane.add(txtUsername, 1, 1);

    Label lblFirstName = new Label("First Name:");
    TextField txtFirstName = new TextField(user.get().getFirstName());
    userGridPane.add(lblFirstName, 0, 2);
    userGridPane.add(txtFirstName, 1, 2);

    Label lblMiddleName = new Label("Middle Name:");
    TextField txtMiddleName = new TextField(user.get().getMiddleName());
    userGridPane.add(lblMiddleName, 0, 3);
    userGridPane.add(txtMiddleName, 1, 3);

    Label lblLastName = new Label("Last Name:");
    TextField txtLastName = new TextField(user.get().getLastName());
    userGridPane.add(lblLastName, 0, 4);
    userGridPane.add(txtLastName, 1, 4);

    Label lblEmail = new Label("Email:");
    TextField txtEmail = new TextField(user.get().getEmail());
    userGridPane.add(lblEmail, 0, 5);
    userGridPane.add(txtEmail, 1, 5);

    Label lblPhoneNumber = new Label("Phone Number:");
    TextField txtPhoneNumber = new TextField(user.get().getPhoneNumber());
    userGridPane.add(lblPhoneNumber, 0, 6);
    userGridPane.add(txtPhoneNumber, 1, 6);

    Label lblBloodGroup = new Label("Blood Group:");
    TextField txtBloodGroup = new TextField(user.get().getBloodGroup());
    userGridPane.add(lblBloodGroup, 0, 7);
    userGridPane.add(txtBloodGroup, 1, 7);

    Label lblDob = new Label("Date of Birth:");
    DatePicker dpDob = new DatePicker(user.get().getDateOfBirth().toLocalDate());
    userGridPane.add(lblDob, 0, 8);
    userGridPane.add(dpDob, 1, 8);

    Button btnUpdateUserSettings = new Button("Update user settings");
    btnUpdateUserSettings.setOnAction(e -> {
      user.get().setUsername(txtUsername.getText());
      user.get().setFirstName(txtFirstName.getText());
      user.get().setMiddleName(txtMiddleName.getText());
      user.get().setLastName(txtLastName.getText());
      user.get().setEmail(txtEmail.getText());
      user.get().setPhoneNumber(txtPhoneNumber.getText());
      user.get().setBloodGroup(txtBloodGroup.getText());
      user.get().setDateOfBirth(java.sql.Date.valueOf(dpDob.getValue()));
      try {
        userDAO.update(user.get());
      } catch (SQLException ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR, "Error updating user settings");
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new java.io.PrintWriter(sw));
        alert.setContentText(sw.toString());
        alert.showAndWait();
        ex.printStackTrace();
      }
    });

    Button btnGoToMainWindow = new Button("Go to main window");
    btnGoToMainWindow.setOnAction(goToMainWindow);

    userSettingsVBox.getChildren().addAll(lblUserSettings, userGridPane, btnUpdateUserSettings, btnGoToMainWindow);

    VBox rightVbox = new VBox(10);
    rightVbox.setAlignment(Pos.CENTER);

    rightVbox.getChildren().add(passwordChangeParent(userDAO));

    root.getChildren().addAll(userSettingsVBox, new Separator(Orientation.VERTICAL), rightVbox);

    if (patientDAO.getPatientObjectForUser(user.get()) != null) {
      rightVbox.getChildren().add(new Separator(Orientation.HORIZONTAL));

      VBox patientVBox = new VBox(10);
      patientVBox.setAlignment(Pos.CENTER);

      Label lblPatientSettings = new Label("Patient Settings");

      GridPane patientGridPane = new GridPane();
      patientGridPane.setHgap(10);
      patientGridPane.setVgap(10);

      Label lblSmokingAndAlcoholStatus = new Label("Smoking and Alcohol Status:");
      TextField txtSmokingAndAlcoholStatus = new TextField(patientDAO.getPatientObjectForUser(user.get()).getSmokingAndAlcoholStatus());
      patientGridPane.add(lblSmokingAndAlcoholStatus, 0, 0);
      patientGridPane.add(txtSmokingAndAlcoholStatus, 1, 0);

      Label lblAdditionalNotes = new Label("Additional Notes:");
      TextField txtAdditionalNotes = new TextField(patientDAO.getPatientObjectForUser(user.get()).getAdditionalNotes());
      patientGridPane.add(lblAdditionalNotes, 0, 1);
      patientGridPane.add(txtAdditionalNotes, 1, 1);

      Button btnUpdatePatientSettings = new Button("Update patient settings");
      btnUpdatePatientSettings.setOnAction(e -> {
        patientDAO.getPatientObjectForUser(user.get()).setSmokingAndAlcoholStatus(txtSmokingAndAlcoholStatus.getText());
        patientDAO.getPatientObjectForUser(user.get()).setAdditionalNotes(txtAdditionalNotes.getText());
        try {
          patientDAO.update(patientDAO.getPatientObjectForUser(user.get()));
        } catch (SQLException ex) {
          Alert alert = new Alert(Alert.AlertType.ERROR, "Error updating patient settings");
          StringWriter sw = new StringWriter();
          ex.printStackTrace(new java.io.PrintWriter(sw));
          alert.setContentText(sw.toString());
          alert.showAndWait();
          ex.printStackTrace();
        }
      });

      patientVBox.getChildren().addAll(lblPatientSettings, patientGridPane, btnUpdatePatientSettings);
      rightVbox.getChildren().add(patientVBox);
    }

    return new Scene(root, 1000, 800);
  }
}
