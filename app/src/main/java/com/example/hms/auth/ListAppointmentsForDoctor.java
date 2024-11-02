package com.example.hms.auth;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;

import com.j256.ormlite.dao.Dao;

import com.example.hms.util.auth.PatientDAO;
import com.example.hms.util.PatientAppointment;
import com.example.hms.util.auth.Doctor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ListAppointmentsForDoctor {
  public static Scene getScene(PatientDAO patientDAO, Dao<PatientAppointment, Long> patientAppointmentDAO, Doctor doctor, EventHandler<ActionEvent> goToMainWindow) {
    VBox root = new VBox();
    root.setAlignment(Pos.CENTER);
    root.setSpacing(10);

    Label label = new Label("Appointments");
    root.getChildren().add(label);

    TableView<HashMap<String, String>> table = new TableView<>();
    ObservableList<HashMap<String, String>> data = FXCollections.observableArrayList();
    var ref = new Object() {
      List<PatientAppointment> appointments = new LinkedList<>();
    };
    try {
      ref.appointments = patientAppointmentDAO.queryForEq("doctor_id", doctor.getDoctorId());
    } catch (Exception e) {
      e.printStackTrace();
    }
    for (PatientAppointment appointment : ref.appointments) {
      DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
      HashMap<String, String> row = new HashMap<>();
      row.put("Patient Username", appointment.getPatient().getUser().getUsername());
      row.put("Patient Name", appointment.getPatient().getUser().getFirstName() + " " + appointment.getPatient().getUser().getLastName());
      row.put("Date and Time", formatter.format(LocalDateTime.ofInstant(appointment.getDateTimeOfAppointment().toInstant(), ZoneId.systemDefault())));
      row.put("Reason for Visit", appointment.getReasonForVisit());
      row.put("Diagnosis", appointment.getDiagnosis());
      data.add(row);
    }

    TableColumn<HashMap<String, String>, String> patientUsernameColumn = new TableColumn<>("Patient Username");
    patientUsernameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("Patient Username")));
    table.getColumns().add(patientUsernameColumn);

    TableColumn<HashMap<String, String>, String> patientColumn = new TableColumn<>("Patient Name");
    patientColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("Patient Name")));
    table.getColumns().add(patientColumn);

    TableColumn<HashMap<String, String>, String> dateTimeColumn = new TableColumn<>("Date and Time");
    dateTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("Date and Time")));
    table.getColumns().add(dateTimeColumn);

    TableColumn<HashMap<String, String>, String> reasonColumn = new TableColumn<>("Reason for Visit");
    reasonColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("Reason for Visit")));
    table.getColumns().add(reasonColumn);

    TableColumn<HashMap<String, String>, String> diagnosisColumn = new TableColumn<>("Diagnosis");
    diagnosisColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("Diagnosis")));
    table.getColumns().add(diagnosisColumn);

    table.setItems(data);

    Button btnDischarge = new Button("Discharge");
    btnDischarge.setOnAction(e -> {
      HashMap<String, String> selectedRow = table.getSelectionModel().getSelectedItem();
      if (selectedRow == null) {
        return;
      }
      String patientUsername = selectedRow.get("Patient Username");
      PatientAppointment appointment = ref.appointments.stream().filter(a -> a.getPatient().getUser().getUsername().equals(patientUsername)).findFirst().get();
      try {
        patientAppointmentDAO.delete(appointment);
        data.remove(selectedRow);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    });

    Button btnBack = new Button("Back");
    btnBack.setOnAction(goToMainWindow);

    root.getChildren().addAll(table, btnDischarge, btnBack);

    return new Scene(root, 800, 600);
  }
}
