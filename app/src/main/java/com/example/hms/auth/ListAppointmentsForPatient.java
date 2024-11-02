package com.example.hms.auth;

import com.example.hms.util.PatientAppointment;
import com.example.hms.util.PatientAppointmentDAO;
import com.example.hms.util.RunOnChange;
import com.example.hms.util.auth.PatientDAO;
import com.example.hms.util.auth.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ListAppointmentsForPatient {
  public Scene getScene(PatientDAO patientDAO, PatientAppointmentDAO patientAppointmentDAO, RunOnChange<User> user, EventHandler<ActionEvent> goToMainWindow) {
    VBox root = new VBox(10);
    root.setAlignment(Pos.CENTER);
    root.setSpacing(10);

    Label lblTitle = new Label("Your Appointments");
    root.getChildren().add(lblTitle);

    TableView<HashMap<String, String>> table = new TableView<>();
    ObservableList<HashMap<String, String>> data = FXCollections.observableArrayList();
    var ref = new Object() {
      List<PatientAppointment> appointments = new LinkedList<>();
    };
    try {
      ref.appointments = patientAppointmentDAO.queryForEq("patient", patientDAO.getPatientObjectForUser(user.get()).getPatientId());
    } catch (Exception e) {
      e.printStackTrace();
    }
    for (PatientAppointment appointment : ref.appointments) {
      DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
      HashMap<String, String> row = new HashMap<>();
      row.put("Doctor Name", appointment.getDoctor().getUser().getFirstName() + " " + appointment.getDoctor().getUser().getLastName());
      row.put("Date and Time", formatter.format(LocalDateTime.ofInstant(appointment.getDateTimeOfAppointment().toInstant(), ZoneId.systemDefault())));
      row.put("Reason for Visit", appointment.getReasonForVisit());
      row.put("Diagnosis", appointment.getDiagnosis());
      data.add(row);
    }

    TableColumn<HashMap<String, String>, String> doctorNameColumn = new TableColumn<>("Doctor Name");
    doctorNameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get("Doctor Name")));

    TableColumn<HashMap<String, String>, String> dateTimeColumn = new TableColumn<>("Date and Time");
    dateTimeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get("Date and Time")));

    TableColumn<HashMap<String, String>, String> reasonForVisitColumn = new TableColumn<>("Reason for Visit");
    reasonForVisitColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get("Reason for Visit")));

    TableColumn<HashMap<String, String>, String> diagnosisColumn = new TableColumn<>("Diagnosis");
    diagnosisColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get("Diagnosis")));

    table.getColumns().addAll(doctorNameColumn, dateTimeColumn, reasonForVisitColumn, diagnosisColumn);
    table.setItems(data);

    Button btnCancelAppointment = new Button("Cancel Appointment");
    btnCancelAppointment.setOnAction(e -> {
      HashMap<String, String> selectedRow = table.getSelectionModel().getSelectedItem();
      if (selectedRow == null) {
        return;
      }
      PatientAppointment appointment = ref.appointments.get(table.getSelectionModel().getSelectedIndex());
      try {
        patientAppointmentDAO.delete(appointment);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    });

    Button btnBack = new Button("Back");
    btnBack.setOnAction(goToMainWindow);

    root.getChildren().addAll(table, btnCancelAppointment, btnBack);

    return new Scene(root, 800, 600);
  }
}
