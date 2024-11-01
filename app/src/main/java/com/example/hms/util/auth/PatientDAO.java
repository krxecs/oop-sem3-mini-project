package com.example.hms.util.auth;

import com.example.hms.util.PatientAppointment;
import com.j256.ormlite.dao.Dao;

public interface PatientDAO extends Dao<Patient, Long> {
  void addPatientForUser(User user, String bloodGroup, String additionalNotes) throws Exception;
  Patient getPatientObjectForUser(User user);
  void deletePatient(PatientAppointment appointment);
}
