package com.example.hms.util.auth;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

public class DoctorDAOImpl extends BaseDaoImpl<Doctor, Long> implements DoctorDAO {
  public DoctorDAOImpl(ConnectionSource connectionSource) throws SQLException {
    super(connectionSource, Doctor.class);
  }
  @Override
  public void addDoctorForUser(User user, String department) throws Exception {
    Doctor doctor = new Doctor(user, department);
    create(doctor);
  }

  @Override
  public void removeDoctor(long doctorId) throws Exception {
    deleteById(doctorId);
  }

  @Override
  public Doctor getDoctorByUsername(String username) throws Exception {
    QueryBuilder<Doctor, Long> queryBuilder = queryBuilder();
    queryBuilder.where().eq("username", username);
    return queryBuilder.queryForFirst();
  }

  @Override
  public Doctor getDoctorByDoctorId(long doctorId) throws Exception {
    return queryForId(doctorId);
  }

  @Override
  public List<Doctor> getDoctorsByDepartment(String department) throws Exception {
    QueryBuilder<Doctor, Long> queryBuilder = queryBuilder();
    queryBuilder.where().eq("department", department);
    return queryBuilder.query();
  }

  @Override
  public List<Doctor> getDoctorsByUser(User user) throws Exception {
    QueryBuilder<Doctor, Long> queryBuilder = queryBuilder();
    queryBuilder.where().eq("user_id", user.getUserId());
    return queryBuilder.query();
  }

  @Override
  public List<Doctor> getDoctors() throws Exception {
    return queryForAll();
  }

  @Override
  public Doctor getDoctorObjectForUser(User user) {
    QueryBuilder<Doctor, Long> queryBuilder = queryBuilder();
    Doctor d = null;
    try {
      queryBuilder.where().eq("user_id", user.getUserId());
      d = queryBuilder.queryForFirst();
    } catch (SQLException e) {
      throw new RuntimeException("Unable to get Doctor object for user", e);
    }

    return d;
  }
}
