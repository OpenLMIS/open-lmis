/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.hash;


import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class to encode the password for a user. The encoding used is base 62.
 */
public class Encoder {
  private static final String PASSWORD_HASH_ALGORITHM = "SHA-512";
  private static final String PASSWORD_HASH_ENCODING = "UTF-8";

  public static String hash(String plainText) {
    String hashValue = null;
    if (plainText == null) return null;
    try {
      MessageDigest msgDigest = MessageDigest.getInstance(PASSWORD_HASH_ALGORITHM);
      msgDigest.update(plainText.getBytes(PASSWORD_HASH_ENCODING));
      //TODO : use a salt for hashing

      byte rawByte[] = msgDigest.digest();
      byte[] encodedBytes = Base64.encodeBase64(rawByte);

      return base64ToBase62(new String(encodedBytes));
    } catch (UnsupportedEncodingException e) {
      //consume this exception-caller does not need to know of the implementation for encoding
    } catch (NoSuchAlgorithmException e) {
      //consume this exception-caller does not need to know of this implementation for hashing algorithm
    }
    return hashValue;
  }

  protected static String base64ToBase62(String base64) {
    StringBuffer buf = new StringBuffer(base64.length() * 2);

    for (int i = 0; i < base64.length(); i++) {
      char ch = base64.charAt(i);
      switch (ch) {
        case 'i':
          buf.append("ii");
          break;

        case '+':
          buf.append("ip");
          break;

        case '/':
          buf.append("is");
          break;

        case '=':
          buf.append("ie");
          break;

        case '\n':
          // Strip out
          break;

        default:
          buf.append(ch);
      }
    }


    return buf.toString();
  }
}