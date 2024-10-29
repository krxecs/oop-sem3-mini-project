package com.example.hms.util.auth;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

public class AdministratorDAOImpl extends BaseDaoImpl<Administrator, Long> implements AdministratorDAO {
  public AdministratorDAOImpl(ConnectionSource connectionSource) throws SQLException {
    super(connectionSource, Administrator.class);
  }
  @Override
  public void addAdministratorForUser(User user, String additionalNotes) throws Exception {
    Administrator administrator = new Administrator(user);
    create(administrator);
  }

  @Override
  public Administrator getAdministratorObjectForUser(User user) {
    try {
      Administrator a = queryForEq("user_id", user).getFirst();
      System.out.println(a);
      return a;
    } catch (Exception e) {
      return null;
    }
  }
}
