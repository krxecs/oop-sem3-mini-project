package com.example.hms.auth;

import com.example.hms.util.crypto.PHCHash;
import com.example.hms.util.crypto.PasswordHash;
import com.github.f4b6a3.uuid.UuidCreator;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.SQLException;
import java.util.UUID;

@DatabaseTable(tableName = "users", daoClass = UserDAOImpl.class)
public class User {
  @DatabaseField(columnName = "user_id", id = true)
  private UUID userId;

  @DatabaseField(columnName = "username", unique = true)
  private String username;

  @DatabaseField(columnName = "password_hash", dataType = DataType.BYTE_ARRAY)
  private byte[] passwordHash;

  @DatabaseField(columnName = "first_name")
  private String firstName;

  @DatabaseField(columnName = "middle_name")
  private String middleName;

  @DatabaseField(columnName = "last_name")
  private String lastName;

  @DatabaseField(columnName = "email")
  private String email;

  public User() { }

  public User(UUID userId, String username, PHCHash passwordHash, String email, String firstName, String middleName, String lastName) {
    this.userId = userId;
    this.username = username;
    this.passwordHash = passwordHash.getFormattedHash();
    this.email = email;
    this.firstName = firstName;
    this.middleName = middleName;
    this.lastName = lastName;
  }

  public UUID getUserId() { return userId; }

  public String getUsername() { return username; }

  public PHCHash getPasswordHash() { return PHCHash.fromHashedByteArray(passwordHash); }

  public String getEmail() { return email; }

  public String getFirstName() { return firstName; }

  public String getMiddleName() { return middleName; }

  public String getLastName() { return lastName; }
}
