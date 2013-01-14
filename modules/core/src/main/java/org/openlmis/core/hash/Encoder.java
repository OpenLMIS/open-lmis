package org.openlmis.core.hash;


import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encoder {
  private static final String PASSWORD_HASH_ALGORITHM = "SHA-512";
  private static final String PASSWORD_HASH_ENCODING = "UTF-8";

  public static String hash(String plainText) {
    String hashValue = null;
    if(plainText == null) return null;
    try {
      MessageDigest msgDigest = MessageDigest.getInstance(PASSWORD_HASH_ALGORITHM);
      msgDigest.update(plainText.getBytes(PASSWORD_HASH_ENCODING));
      //TODO : use a salt for hashing

      byte rawByte[] = msgDigest.digest();
      byte[] encodedBytes = Base64.encodeBase64(rawByte);
      return new String(encodedBytes);
    } catch (UnsupportedEncodingException e) {
      //consume this exception-caller does not need to know of the implementation for encoding
    } catch (NoSuchAlgorithmException e) {
      //consume this exception-caller does not need to know of this implementation for hashing algorithm
    }
    return hashValue;
  }
}