package com.example.hms.auth;

import com.j256.ormlite.dao.Dao;

import java.util.List;

public interface DoctorDAO extends Dao<Doctor, Long> {
  void addDoctorForUser(User user, String department) throws Exception;
  void removeDoctor(long doctorId) throws Exception;
  Doctor getDoctorByUsername(String username) throws Exception;
  Doctor getDoctorByDoctorId(long doctorId) throws Exception;
  List<Doctor> getDoctorsByDepartment(String department) throws Exception;
  List<Doctor> getDoctorsByUser(User user) throws Exception;
  List<Doctor> getDoctors() throws Exception;
}
