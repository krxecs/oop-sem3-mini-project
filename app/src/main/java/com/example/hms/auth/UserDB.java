package com.example.hms.auth;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.UUID;

public final class UserDB {
  HashMap<UUID, UserAttributes> userDbInMem;

  public UserDB(HashMap<UUID, UserAttributes> userDbInMem) {
    this.userDbInMem = userDbInMem;
  }

  public HashMap<UUID, UserAttributes> getUserDbInMem() { return userDbInMem; }
  public static class Adapter implements JsonSerializer<UserDB>, JsonDeserializer<UserDB> {
    @Override
    public JsonElement serialize(UserDB src, Type typeOfSrc, JsonSerializationContext context) {
      JsonObject jo1 = new JsonObject();
      for (UUID k : src.userDbInMem.keySet()) {
        JsonObject jo = new JsonObject();
        for (String k1 : src.userDbInMem.get(k).getAttributes().keySet()) {
          if (src.userDbInMem.get(k).getAttributes().get(k1) instanceof Number)
            jo.addProperty(k1, (Number)src.userDbInMem.get(k).getAttributes().get(k1));
          else if (src.userDbInMem.get(k).getAttributes().get(k1) instanceof Boolean)
            jo.addProperty(k1, (Boolean)src.userDbInMem.get(k).getAttributes().get(k1));
          else
            jo.addProperty(k1, src.userDbInMem.get(k).getAttributes().get(k1).toString());
        }

        jo1.add(k.toString(), jo);
      }
      return jo1;
    }

    @Override
    public UserDB deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
      JsonObject jo = json.getAsJsonObject();
      HashMap<UUID, UserAttributes> userDbInMem = new HashMap<>();
      for (String k : jo.keySet())
        userDbInMem.put(UUID.fromString(k), context.deserialize(jo.get(k), UserAttributes.class));

      return new UserDB(userDbInMem);
    }
  }
}
