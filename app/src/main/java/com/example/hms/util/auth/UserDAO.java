package com.example.hms.util.auth;

import com.j256.ormlite.dao.Dao;

import java.util.UUID;

public interface UserDAO extends Dao<User, UUID> {
  User addUser(String username, char[] password, String email, String firstName, String middleName, String lastName, java.sql.Date dateOfBirth, String phoneNumber, String bloodGroup) throws Exception;
  User getUser(String username) throws Exception;
  User login(String username, char[] password) throws Exception;
  void changePassword(User user, char[] oldPassword, char[] newPassword) throws Exception;
}
