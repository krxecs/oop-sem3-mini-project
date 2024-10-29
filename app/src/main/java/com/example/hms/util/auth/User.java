package com.example.hms.util.auth;

import com.example.hms.util.crypto.PHCHash;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.UUID;

@DatabaseTable(tableName = "users", daoClass = UserDAOImpl.class)
public class User {
  @DatabaseField(columnName = "user_id", id = true)
  private UUID userId;

  @DatabaseField(columnName = "username", unique = true, canBeNull = false)
  private String username;

  @DatabaseField(columnName = "password_hash", dataType = DataType.BYTE_ARRAY, canBeNull = false)
  private byte[] passwordHash;

  @DatabaseField(columnName = "first_name", canBeNull = false)
  private String firstName;

  @DatabaseField(columnName = "middle_name")
  private String middleName;

  @DatabaseField(columnName = "last_name", canBeNull = false)
  private String lastName;

  @DatabaseField(columnName = "email", unique = true, canBeNull = false)
  private String email;

  @DatabaseField(columnName = "dob", canBeNull = false)
  private java.sql.Date dateOfBirth;

  @DatabaseField(columnName = "phone_no", canBeNull = false)
  private String phoneNumber;

  @DatabaseField(columnName = "blood_group", canBeNull = false)
  private String bloodGroup;

  public User() { }

  public User(UUID userId, String username, PHCHash passwordHash, String email, String firstName, String middleName, String lastName, java.sql.Date dateOfBirth, String phoneNumber, String bloodGroup) {
    this.userId = userId;
    this.username = username;
    this.passwordHash = passwordHash.getFormattedHash();
    this.email = email;
    this.firstName = firstName;
    this.middleName = middleName;
    this.lastName = lastName;
    this.dateOfBirth = dateOfBirth;
    this.phoneNumber = phoneNumber;
    this.bloodGroup = bloodGroup;
  }

  public UUID getUserId() { return userId; }

  public String getUsername() { return username; }

  public PHCHash getPasswordHash() { return PHCHash.fromHashedByteArray(passwordHash); }

  public String getEmail() { return email; }

  public String getFirstName() { return firstName; }

  public String getMiddleName() { return middleName; }

  public String getLastName() { return lastName; }

  public java.sql.Date getDateOfBirth() { return dateOfBirth; }

  public String getPhoneNumber() { return phoneNumber; }

  public String getBloodGroup() { return bloodGroup; }
}
