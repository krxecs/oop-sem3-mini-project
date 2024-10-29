package com.example.hms.util;

import com.example.hms.util.auth.Doctor;
import com.example.hms.util.auth.Patient;
import com.j256.ormlite.dao.Dao;

import java.util.Date;

public interface PatientAppointmentDAO extends Dao<PatientAppointment, Long> {
  void addAppointmentForPatient(Patient patient, Date dateTimeOfAppointment, Doctor doctor, String reasonForVisit, String diagnosis) throws Exception;
  void cancelAppointment(long appointmentId) throws Exception;
}
