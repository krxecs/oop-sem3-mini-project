package com.example.hms.auth;

import com.example.hms.util.PatientAppointment;
import com.example.hms.util.auth.Doctor;
import com.example.hms.util.auth.PatientDAO;
import com.example.hms.util.auth.UserDAO;
import com.j256.ormlite.dao.Dao;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DoctorPage {
  public void set(PatientDAO patientDAO, Doctor doctor, Dao<PatientAppointment, Long> patientAppointmentDao, Stage stage) {
    VBox root = new VBox(10);
    root.setAlignment(Pos.CENTER);
    Scene rootScene = new Scene(root, 800, 600);

    Button btnListAppointments = new Button("List Appointments");
    btnListAppointments.setOnAction(e -> {
      stage.setScene(ListAppointments.getScene(patientDAO, patientAppointmentDao, doctor, e1 -> stage.setScene(rootScene)));
    });
    root.getChildren().add(btnListAppointments);
  }
}
