package com.example.hms.auth;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.Date;

public class PatientDAOImpl extends BaseDaoImpl<Patient, Long> implements PatientDAO {
  public PatientDAOImpl(ConnectionSource connectionSource) throws SQLException {
    super(connectionSource, Patient.class);
  }
  @Override
  public void addPatientForUser(User user, String bloodGroup, Date dateTimeOfAppointment, String reasonForVisit, String diagnosis, String smokingAndAlcoholStatus, String additionalNotes) throws Exception {
    Patient patient = new Patient(user, bloodGroup, dateTimeOfAppointment, reasonForVisit, diagnosis, smokingAndAlcoholStatus, additionalNotes);
    create(patient);
  }

  @Override
  public void dischargePatient(long patientId) throws Exception {
    deleteById(patientId);
  }
}