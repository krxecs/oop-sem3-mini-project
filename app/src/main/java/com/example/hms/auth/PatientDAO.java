package com.example.hms.auth;

import com.j256.ormlite.dao.Dao;

import java.util.Date;

public interface PatientDAO extends Dao<Patient, Long> {
  void addPatientForUser(User user, String bloodGroup, Date dateTimeOfAppointment, String reasonForVisit, String diagnosis, String smokingAndAlcoholStatus, String additionalNotes) throws Exception;
  void dischargePatient(long patientId) throws Exception;
}
