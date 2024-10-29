package com.example.hms.util.auth;

import com.j256.ormlite.dao.Dao;

import java.util.Date;

public interface PatientDAO extends Dao<Patient, Long> {
  void addPatientForUser(User user, String bloodGroup, String additionalNotes) throws Exception;
  Patient getPatientObjectForUser(User user);
}
