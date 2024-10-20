package com.example.hms.util.crypto;

import static org.bouncycastle.util.Arrays.constantTimeAreEqual;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

final class InternalByteArrayOutputStream extends ByteArrayOutputStream {
  public void securelyErase() {
    Arrays.fill(buf, (byte)0);
    super.reset();
  }
}

public final class PHCHash {
  private final String alg;
  private final int ver;
  private final HashMap<String, byte[]> params;
  private final byte[] salt, hash;

  public PHCHash(String alg, int ver, HashMap<String, byte[]> params,
                 byte[] salt, byte[] hash) {
    this.alg = alg;
    this.ver = ver;
    this.params = params;
    this.salt = salt;
    this.hash = hash;
  }

  private static HashMap<String, byte[]>
  paramsFromBytes(ByteArrayInputStream is) {
    HashMap<String, byte[]> params = new HashMap<>();
    for (int ch = is.read(); ch != '$';) {
      StringBuilder key = new StringBuilder();
      InternalByteArrayOutputStream valueStream = new InternalByteArrayOutputStream();

      if (ch == ',')
        ch = is.read();

      for (; ch != '='; ch = is.read())
        key.append((char)ch);
      for (ch = is.read(); ch != ',' && ch != '$'; ch = is.read())
        valueStream.write(ch);

      byte[] valueArray = valueStream.toByteArray();
      valueStream.securelyErase();
      params.put(key.toString(), valueArray);
    }
    return params;
  }

  /* Spec found at
   * https://github.com/P-H-C/phc-string-format/blob/master/phc-sf-spec.md */
  public static PHCHash fromHashedByteArray(byte[] hash) {
    ByteArrayInputStream is = new ByteArrayInputStream(hash);

    int ch = is.read();
    if (ch != '$' && ch != -1)
      throw new IllegalArgumentException("Invalid PHC hash");

    StringBuilder algSb = new StringBuilder();
    for (ch = is.read(); ch != '$' && ch != -1; ch = is.read())
      algSb.append((char)ch);

    String alg = algSb.toString();

    if (!alg.matches("[a-z0-9\\-]*"))
      throw new IllegalArgumentException("Invalid PHC hash");

    StringBuilder verSb = new StringBuilder();
    while ((ch = is.read()) != '$')
      verSb.append((char)ch);

    int ver;
    try {
      String verString = verSb.toString();
      String[] verParts = verString.split("=");
      if (verParts.length != 2 || !verParts[0].equals("v"))
        throw new IllegalArgumentException("Invalid PHC hash");
      ver = Integer.parseInt(verParts[1]);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Invalid PHC hash", e);
    }

    HashMap<String, byte[]> params = paramsFromBytes(is);

    if (is.available() == 0)
      return new PHCHash(alg, ver, params, null, null);

    ByteArrayOutputStream saltStream = new ByteArrayOutputStream();
    while ((ch = is.read()) != '$') {
      saltStream.write(ch);
    }

    ByteArrayOutputStream hashStream = new ByteArrayOutputStream();
    while ((ch = is.read()) != -1) {
      hashStream.write(ch);
    }

    Base64.Decoder decoder = Base64.getDecoder();
    byte[] salt = decoder.decode(saltStream.toByteArray());
    byte[] rawHash = decoder.decode(hashStream.toByteArray());

    return new PHCHash(alg, ver, params, salt, rawHash);
  }

  public String algorithm() { return alg; }
  public int version() { return ver; }
  public HashMap<String, byte[]> parameters() { return params; }

  public byte[] salt() { return salt; }
  public byte[] rawHash() { return hash; }

  public byte[] getFormattedHash() {
    InternalByteArrayOutputStream out = new InternalByteArrayOutputStream();
    out.write('$');
    out.write(alg.getBytes(), 0, alg.length());

    String vStart = "$v=";
    out.write(vStart.getBytes(), 0, vStart.length());
    out.write(Integer.toString(ver).getBytes(), 0,
              Integer.toString(ver).length());

    if (params.size() <= 0)
      throw new IllegalArgumentException("No parameters found");

    out.write('$');
    boolean first = true;
    for (Map.Entry<String, byte[]> entry : params.entrySet()) {
      if (!first)
        out.write(',');

      out.write(entry.getKey().getBytes(), 0, entry.getKey().length());
      out.write('=');
      out.write(entry.getValue(), 0, entry.getValue().length);
      first = false;
    }

    Base64.Encoder encoder = Base64.getEncoder().withoutPadding();
    out.write('$');
    byte[] saltInB64 = encoder.encode(this.salt);
    out.write(saltInB64, 0, saltInB64.length);

    out.write('$');
    byte[] hashInB64 = encoder.encode(this.hash);
    out.write(hashInB64, 0, hashInB64.length);

    return out.toByteArray();
  }

  @Override
  public String toString() { return new String(getFormattedHash()); }

  @Override
  public boolean equals(Object obj) {
    if (! (obj instanceof PHCHash))
      return false;

    PHCHash hobj = (PHCHash)obj;
    return hobj.alg.equals(this.alg) && hobj.ver == this.ver &&
        hobj.params.equals(this.params) &&
        constantTimeAreEqual(hobj.salt, this.salt) &&
        constantTimeAreEqual(hobj.hash, this.hash);
  }
}
