/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import static java.lang.System.getProperty;


public class JsonUtility {

  public static <T> T readObjectFromFile(String fullJsonTxtFileName, Class<T> clazz) throws IOException {
    String classPathFile = JsonUtility.class.getClassLoader().getResource(fullJsonTxtFileName).getFile();
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(new File(classPathFile), clazz);
  }

  public static String getJsonStringFor(Object object) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    StringWriter writer = new StringWriter();
    objectMapper.writeValue(writer, object);
    return writer.toString();
  }

}

