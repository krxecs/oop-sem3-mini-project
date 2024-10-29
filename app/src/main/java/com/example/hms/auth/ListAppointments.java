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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ListAppointments {
    public static Scene getScene(PatientDAO patientDAO, Dao<PatientAppointment, Long> patientAppointmentDAO, Doctor doctor, EventHandler<ActionEvent> goToMainWindow) {
    VBox root = new VBox(10);
    root.setAlignment(Pos.CENTER);

    TableView<HashMap<String, String>> table = new TableView<>();
    table.setEditable(false);

    Label lblDoctor = new Label("Doctor: " + doctor.getUser().getFirstName() + " " + doctor.getUser().getLastName());

    List<PatientAppointment> patients = new LinkedList<>();
    boolean success = false;
    try {
      patients = patientAppointmentDAO.queryForEq("doctor_id", doctor.getDoctorId());
      success = true;
    } catch (Exception ex) {
      System.err.println("Error listing patients:\n" + ex);
      ex.printStackTrace(System.err);
    }

    if (success) {
      ObservableList<HashMap<String, String>> data = FXCollections.observableArrayList();
      for (PatientAppointment pa : patients) {
        HashMap<String, String> row = new HashMap<>();
        row.put("patient_id", Long.toString(pa.getPatient().getPatientId()));
        row.put("user_id", pa.getPatient().getUser().getUserId().toString());
        row.put("first_name", pa.getPatient().getUser().getFirstName());
        row.put("last_name", pa.getPatient().getUser().getLastName());
        row.put("blood_group", pa.getPatient().getUser().getBloodGroup());
        row.put("date_time_of_appointment", pa.getDateTimeOfAppointment().toString());
        row.put("reason_for_visit", pa.getReasonForVisit());
        row.put("diagnosis", pa.getDiagnosis());
        row.put("smoking_and_alcohol_status", pa.getPatient().getSmokingAndAlcoholStatus());
        row.put("additional_notes", pa.getPatient().getAdditionalNotes());

        System.out.println(pa.getDateTimeOfAppointment().toString());
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
    btnGoToMainWindow.setOnAction(goToMainWindow);

    Scene scene = new Scene(root, 300, 200);
    root.getChildren().addAll(lblDoctor, table, btnGoToMainWindow);
    return scene;
  }
}
