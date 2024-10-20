package com.example.hms;

import com.example.hms.auth.User;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "patients")
public class Patient {
  @DatabaseField(columnName = "patient_id", generatedId = true)
  private long patientId;

  @DatabaseField(columnName = "user_id", foreign = true, foreignAutoRefresh = true, unique = true)
  private User user;

  @DatabaseField(columnName = "blood_group")
  private String blood_group;

  public Patient() { }

  public Patient(User user, String blood_group) {
    this.user = user;
    this.blood_group = blood_group;
  }

  public long getPatientId() { return patientId; }
  public void setPatientId(long patientId) { this.patientId = patientId; }

  public User getUser() { return user; }
  public void setUser(User user) { this.user = user; }

  public String getBloodGroup() { return blood_group; }
  public void setBloodGroup(String blood_group) { this.blood_group = blood_group; }
}
