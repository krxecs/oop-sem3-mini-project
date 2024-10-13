package com.example.hms.auth;

import com.example.hms.util.crypto.PHCHash;
import com.example.hms.util.crypto.PasswordHash;
import com.github.f4b6a3.uuid.UuidCreator;
import com.google.gson.*;

import java.io.*;
import java.util.HashMap;
import java.util.UUID;

public final class UserDatabase {
  private final File file;
  private final Gson gson = new GsonBuilder()
      .registerTypeHierarchyAdapter(UserDB.class, new UserDB.Adapter())
      .registerTypeHierarchyAdapter(UserAttributes.class, new UserAttributes.Adapter())
      .registerTypeHierarchyAdapter(PHCHash.class, new PHCHash.Adapter())
      .enableComplexMapKeySerialization()
      .setPrettyPrinting()
      .create();

  private final UserDB userDB;

  public UserDatabase(String fileName) throws IOException {
    UserDB udb;
    file = new File(fileName);
    try (Reader br = new BufferedReader(new FileReader(file))) {
      udb = gson.fromJson(br, UserDB.class);
    } catch (FileNotFoundException e) {
      udb = new UserDB(new HashMap<>());
      file.createNewFile();
    }
    System.out.println(udb);
    userDB = udb;
  }

  public void addUser(String username, char[] password) {
    addUser(UuidCreator.getTimeOrderedEpochPlusN(), username, password);
  }
  public void addUser(UUID userId, String username, char[] password) {
    PasswordHash ph = new PasswordHash();
    PHCHash h = ph.generateHash(password, PasswordHash.generateSalt());

    boolean found = false;
    for (UUID k : userDB.getUserDbInMem().keySet()) {
      UserAttributes ua = userDB.getUserDbInMem().get(k);
      if (ua.toString().equals(username)) {
        found = true;
        break;
      }
    }

    if (found)
      throw new IllegalArgumentException("User already exists");

    HashMap<String, Object> attributes = new HashMap<>();
    attributes.put("username", username);
    attributes.put("passwordHash", h);
    userDB.getUserDbInMem().put(userId, new UserAttributes(attributes));
  }

  public UUID login(String username, char[] password) {
    for (UUID k : userDB.getUserDbInMem().keySet()) {
      UserAttributes ua = userDB.getUserDbInMem().get(k);
      if (ua.getAttributes().get("username").equals(username)) {
        PHCHash h = (PHCHash) ua.getAttributes().get("passwordHash");
        if (PasswordHash.verifyPassword(password, h))
          return k;
      }
    }
    return null;
  }

  public void removeUser(String username) {
    HashMap<UUID, UserAttributes> m = userDB.getUserDbInMem();
    m.keySet().removeIf(k -> m.get(k).toString().equals(username));
  }

  public HashMap<UUID, UserAttributes> listUsers() {
    return userDB.getUserDbInMem();
  }

  public void flush() throws IOException {
    try (Writer w = new FileWriter(file)) {
      gson.toJson(userDB, w);
    }
  }
}
