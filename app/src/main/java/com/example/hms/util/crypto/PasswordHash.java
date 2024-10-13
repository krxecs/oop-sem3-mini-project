package com.example.hms.util.crypto;

import java.util.HashMap;
import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;

public final class PasswordHash {
  private final int hashLength;
  private final HashMap<String, byte[]> params;

  public PasswordHash(int hashLength, HashMap<String, byte[]> params) {
    this.hashLength = hashLength;
    this.params = params;
  }

  public PasswordHash(int hashLength) { this(hashLength, defaultParams()); }

  public PasswordHash() { this(32); }

  public static HashMap<String, byte[]> defaultParams() {
    HashMap<String, byte[]> params = new HashMap<>();
    params.put("m", "47104".getBytes());
    params.put("t", "1".getBytes());
    params.put("p", "1".getBytes());
    return params;
  }

  public static byte[] generateSalt(int size) {
    byte[] salt = new byte[size];
    new java.security.SecureRandom().nextBytes(salt);
    return salt;
  }

  public static byte[] generateSalt() { return generateSalt(16); }

  public PHCHash generateHash(char[] password, byte[] salt) {
    Argon2BytesGenerator generator = new Argon2BytesGenerator();
    Argon2Parameters.Builder builder =
        new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
            .withSalt(salt)
            .withParallelism(Integer.parseInt(new String(params.get("p"))))
            .withMemoryAsKB(Integer.parseInt(new String(params.get("m"))))
            .withIterations(Integer.parseInt(new String(params.get("t"))));

    generator.init(builder.build());
    byte[] result = new byte[hashLength];
    generator.generateBytes(password, result);

    return new PHCHash("argon2id", 19, params, salt, result);
  }

  public static boolean verifyPassword(char[] password, PHCHash h) {
    PasswordHash ctx = new PasswordHash(h.rawHash().length, h.parameters());
    PHCHash genHash = ctx.generateHash(password, h.salt());
    return genHash.equals(h);
  }
}
