package com.example.hms.util.auth;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "administrators", daoClass = AdministratorDAOImpl.class)
public class Administrator {
  @DatabaseField(columnName = "administrator_id", generatedId = true)
  private long administratorId;

  @DatabaseField(columnName = "user_id", foreign = true, foreignAutoRefresh = true, unique = true)
  private User user;

  public Administrator() { }

  public Administrator(User user) {
    this.user = user;
  }

  public long getAdministratorId() { return administratorId; }
  public void setAdministratorId(long administratorId) { this.administratorId = administratorId; }

  public User getUser() { return user; }
  public void setUser(User user) { this.user = user; }
}
