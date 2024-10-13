package com.example.hms.auth;

import com.example.hms.util.crypto.PHCHash;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public final class UserAttributes {
  private final HashMap<String, Object> attributes;

  public UserAttributes(HashMap<String, Object> attributes) {
    this.attributes = attributes;
  }

  public HashMap<String, Object> getAttributes() { return attributes; }

  public static class Adapter implements JsonDeserializer<UserAttributes>, JsonSerializer<UserAttributes> {
    @Override
    public JsonElement serialize(UserAttributes src, Type typeOfSrc, JsonSerializationContext context) {
      JsonObject jo = new JsonObject();
      jo.addProperty("passwordHash", new String(((PHCHash)src.attributes.get("passwordHash")).getFormattedHash()));

      for (String key : src.attributes.keySet()) {
        if (key.equals("passwordHash"))
          continue;

        if (src.attributes.get(key) instanceof Number)
          jo.addProperty(key, (Number)src.attributes.get(key));
        else if (src.attributes.get(key) instanceof Boolean)
          jo.addProperty(key, (Boolean)src.attributes.get(key));
        else if (src.attributes.get(key) instanceof PHCHash)
          jo.add(key, context.serialize(src.attributes.get(key), PHCHash.class));
        else
          jo.addProperty(key, src.attributes.get(key).toString());
      }
      return jo;
    }

    @Override
    public UserAttributes deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
      JsonObject jo = json.getAsJsonObject();
      Map<String, JsonElement> map = jo.asMap();
      HashMap<String, Object> attrs = new HashMap<>();
      for (String key : map.keySet()) {
        if (key.equals("passwordHash"))
          attrs.put(key, context.deserialize(map.get(key), PHCHash.class));
        else if (map.get(key).isJsonPrimitive()) {
          JsonPrimitive jp = map.get(key).getAsJsonPrimitive();
          if (jp.isNumber())
            attrs.put(key, jp.getAsNumber());
          else if (jp.isBoolean())
            attrs.put(key, jp.getAsBoolean());
          else
            attrs.put(key, jp.getAsString());
        }
      }
      return new UserAttributes(attrs);
    }
  }
}
