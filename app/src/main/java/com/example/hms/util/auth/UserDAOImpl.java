package com.example.hms.util.auth;

import com.example.hms.util.crypto.PHCHash;
import com.example.hms.util.crypto.PasswordHash;
import com.github.f4b6a3.uuid.UuidCreator;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.UUID;

public class UserDAOImpl extends BaseDaoImpl<User, UUID> implements UserDAO {
  public UserDAOImpl(ConnectionSource connectionSource) throws SQLException {
    super(connectionSource, User.class);
  }

  @Override
  public User addUser(String username, char[] password, String email, String firstName, String middleName, String lastName, java.sql.Date dateOfBirth, String phoneNumber, String bloodGroup) throws Exception {
    User user = getUser(username);
    if (user != null) {
      throw new Exception("User already exists");
    }
    PHCHash passwordHash = new PasswordHash().generateHash(password, PasswordHash.generateSalt());
    User newUser = new User(UuidCreator.getTimeOrderedEpochPlusN(), username, passwordHash, email, firstName, middleName, lastName, dateOfBirth, phoneNumber, bloodGroup);
    create(newUser);
    return newUser;
  }

  @Override
  public User getUser(String username) throws Exception {
    return queryForEq("username", username).stream().findFirst().orElse(null);
  }

  @Override
  public User login(String username, char[] password) throws Exception {
    User user = getUser(username);
    if (user == null) {
      throw new Exception("User not found");
    }
    if (!PasswordHash.verifyPassword(password, user.getPasswordHash())) {
      throw new Exception("Invalid password");
    }
    return user;
  }

  @Override
  public void changePassword(User user, char[] oldPassword, char[] newPassword) throws Exception {
    if (!PasswordHash.verifyPassword(oldPassword, user.getPasswordHash())) {
      throw new Exception("Invalid old password");
    }
    PHCHash passwordHash = new PasswordHash().generateHash(newPassword, PasswordHash.generateSalt());
    user.setPasswordHash(passwordHash);
    update(user);
  }
}
