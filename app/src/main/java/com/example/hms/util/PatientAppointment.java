package com.example.hms.util;

import com.example.hms.util.auth.Doctor;
import com.example.hms.util.auth.Patient;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "patient_appointments", daoClass = PatientAppointmentDAOImpl.class)
public class PatientAppointment {
  @DatabaseField(columnName = "id", generatedId = true)
  private long id;

  @DatabaseField(columnName = "patient", foreign = true, foreignAutoRefresh = true)
  private Patient patient;

  @DatabaseField(columnName = "date_time_of_appointment", canBeNull = false)
  private Date dateTimeOfAppointment;

  @DatabaseField(columnName = "doctor", foreign = true, foreignAutoRefresh = true)
  private Doctor doctor;

  @DatabaseField(columnName = "reason_for_visit")
  private String reasonForVisit;

  @DatabaseField(columnName = "diagnosis")
  private String diagnosis;

  public PatientAppointment() {}

  public PatientAppointment(Patient patient, Date dateTimeOfAppointment, String reasonForVisit, String diagnosis) {
    this.patient = patient;
    this.dateTimeOfAppointment = dateTimeOfAppointment;
    this.reasonForVisit = reasonForVisit;
    this.diagnosis = diagnosis;
  }

  public long getId() { return id; }

  public Patient getPatient() { return patient; }

  public Date getDateTimeOfAppointment() { return dateTimeOfAppointment; }

  public String getReasonForVisit() { return reasonForVisit; }

  public String getDiagnosis() { return diagnosis; }
}
