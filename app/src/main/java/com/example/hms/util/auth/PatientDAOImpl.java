package com.example.hms.util.auth;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.Date;

public class PatientDAOImpl extends BaseDaoImpl<Patient, Long> implements PatientDAO {
  public PatientDAOImpl(ConnectionSource connectionSource) throws SQLException {
    super(connectionSource, Patient.class);
  }
  @Override
  public void addPatientForUser(User user, String smokingAndAlcoholStatus, String additionalNotes) throws Exception {
    Patient patient = new Patient(user, smokingAndAlcoholStatus, additionalNotes);
    create(patient);
  }

  @Override
  public Patient getPatientObjectForUser(User user) {
    try {
      return queryForEq("user_id", user).getFirst();
    } catch (Exception e) {
      return null;
    }
  }
}