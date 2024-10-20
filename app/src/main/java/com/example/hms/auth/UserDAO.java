package com.example.hms.auth;

import com.j256.ormlite.dao.Dao;

import java.util.UUID;

public interface UserDAO extends Dao<User, UUID> {
  User addUser(String username, char[] password, String email, String firstName, String middleName, String lastName, java.sql.Date dateOfBirth, String phoneNumber) throws Exception;
  User getUser(String username) throws Exception;
  User login(String username, char[] password) throws Exception;
}
