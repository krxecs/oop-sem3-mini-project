package com.example.hms.util.auth;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "patients", daoClass = PatientDAOImpl.class)
public class Patient {
  @DatabaseField(columnName = "patient_id", generatedId = true)
  private long patientId;

  @DatabaseField(columnName = "user_id", foreign = true, foreignAutoRefresh = true, unique = true)
  private User user;

  @DatabaseField(columnName = "smoking_and_alcohol_status")
  private String smokingAndAlcoholStatus;

  @DatabaseField(columnName = "additional_notes")
  private String additionalNotes;

  public Patient() { }

  public Patient(User user, String smokingAndAlcoholStatus, String additionalNotes) {
    this.user = user;
    this.smokingAndAlcoholStatus = smokingAndAlcoholStatus;
    this.additionalNotes = additionalNotes;
  }

  public long getPatientId() { return patientId; }
  public void setPatientId(long patientId) { this.patientId = patientId; }

  public User getUser() { return user; }
  public void setUser(User user) { this.user = user; }

  public String getSmokingAndAlcoholStatus() { return smokingAndAlcoholStatus; }
  public void setSmokingAndAlcoholStatus(String smokingAndAlcoholStatus) { this.smokingAndAlcoholStatus = smokingAndAlcoholStatus; }

  public String getAdditionalNotes() { return additionalNotes; }
  public void setAdditionalNotes(String additionalNotes) { this.additionalNotes = additionalNotes; }
}
