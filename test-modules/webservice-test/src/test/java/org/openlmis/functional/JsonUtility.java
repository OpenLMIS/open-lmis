/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
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

