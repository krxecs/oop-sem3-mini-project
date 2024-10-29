package com.example.hms.util.auth;

import com.j256.ormlite.dao.Dao;

public interface AdministratorDAO extends Dao<Administrator, Long> {
  void addAdministratorForUser(User user, String additionalNotes) throws Exception;
  Administrator getAdministratorObjectForUser(User user);
}
