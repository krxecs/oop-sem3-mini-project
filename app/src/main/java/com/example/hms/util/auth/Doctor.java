package com.example.hms.util.auth;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "doctors", daoClass = DoctorDAOImpl.class)
public class Doctor {
  @DatabaseField(columnName = "doctor_id", generatedId = true)
  private long doctorId;

  @DatabaseField(columnName = "user_id", foreign = true, foreignAutoRefresh = true, unique = true)
  private User user;

  @DatabaseField(columnName = "department", canBeNull = false)
  private String department;

  public Doctor() { }

  public Doctor(User user, String department) {
    this.user = user;
    this.department = department;
  }

  public long getDoctorId() { return doctorId; }

  public void setDoctorId(long doctorId) { this.doctorId = doctorId; }

  public User getUser() { return user; }

  public void setUser(User user) { this.user = user; }

  public String getDepartment() { return department; }

  public void setDepartment(String department) { this.department = department; }
}
