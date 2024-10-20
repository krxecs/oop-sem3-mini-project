package com.example.hms.auth;

import com.j256.ormlite.dao.Dao;

import java.util.UUID;

public interface UserDAO extends Dao<User, UUID> {
  void addUser(String username, char[] password, String email, String firstName, String middleName, String lastName) throws Exception;
  User getUser(String username) throws Exception;
  User login(String username, char[] password) throws Exception;
}
