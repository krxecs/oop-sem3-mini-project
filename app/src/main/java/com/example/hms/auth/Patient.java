package com.example.hms.auth;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "patients", daoClass = PatientDAOImpl.class)
public class Patient {
  @DatabaseField(columnName = "patient_id", generatedId = true)
  private long patientId;

  @DatabaseField(columnName = "user_id", foreign = true, foreignAutoRefresh = true, unique = true)
  private User user;

  @DatabaseField(columnName = "blood_group", canBeNull = false)
  private String blood_group;

  @DatabaseField(columnName = "date_time_of_appointment", canBeNull = false)
  private Date dateTimeOfAppointment;

  @DatabaseField(columnName = "reason_for_visit")
  private String reasonForVisit;

  @DatabaseField(columnName = "diagnosis")
  private String diagnosis;

  @DatabaseField(columnName = "smoking_and_alcohol_status")
  private String smokingAndAlcoholStatus;

  @DatabaseField(columnName = "additional_notes")
  private String additionalNotes;

  public Patient() { }

  public Patient(User user, String bloodGroup, Date dateTimeOfAppointment, String reasonForVisit, String diagnosis, String smokingAndAlcoholStatus, String additionalNotes) {
    this.user = user;
    this.blood_group = bloodGroup;
    this.dateTimeOfAppointment = dateTimeOfAppointment;
    this.reasonForVisit = reasonForVisit;
    this.diagnosis = diagnosis;
    this.smokingAndAlcoholStatus = smokingAndAlcoholStatus;
    this.additionalNotes = additionalNotes;
  }

  public long getPatientId() { return patientId; }
  public void setPatientId(long patientId) { this.patientId = patientId; }

  public User getUser() { return user; }
  public void setUser(User user) { this.user = user; }

  public String getBloodGroup() { return blood_group; }
  public void setBloodGroup(String blood_group) { this.blood_group = blood_group; }

  public Date getDateTimeOfAppointment() { return dateTimeOfAppointment; }
  public void setDateTimeOfAppointment(Date dateTimeOfAppointment) { this.dateTimeOfAppointment = dateTimeOfAppointment; }

  public String getReasonForVisit() { return reasonForVisit; }
  public void setReasonForVisit(String reasonForVisit) { this.reasonForVisit = reasonForVisit; }

  public String getDiagnosis() { return diagnosis; }
  public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

  public String getSmokingAndAlcoholStatus() { return smokingAndAlcoholStatus; }
  public void setSmokingAndAlcoholStatus(String smokingAndAlcoholStatus) { this.smokingAndAlcoholStatus = smokingAndAlcoholStatus; }

  public String getAdditionalNotes() { return additionalNotes; }
  public void setAdditionalNotes(String additionalNotes) { this.additionalNotes = additionalNotes; }
}
