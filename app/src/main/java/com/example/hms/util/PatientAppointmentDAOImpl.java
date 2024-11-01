package com.example.hms.util;

import com.example.hms.util.auth.Doctor;
import com.example.hms.util.auth.Patient;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.Date;

public class PatientAppointmentDAOImpl extends BaseDaoImpl<PatientAppointment, Long> implements PatientAppointmentDAO {
  public PatientAppointmentDAOImpl(ConnectionSource connectionSource) throws SQLException {
    super(connectionSource, PatientAppointment.class);
  }
  @Override
  public void addAppointmentForPatient(Patient patient, Date dateTimeOfAppointment, Doctor doctor, String reasonForVisit, String diagnosis) throws Exception {
    PatientAppointment appointment = new PatientAppointment(patient, doctor, dateTimeOfAppointment, reasonForVisit, diagnosis);
    create(appointment);
  }

  @Override
  public void cancelOrCompletedAppointment(long appointmentId) throws Exception {
    deleteById(appointmentId);
  }
}
