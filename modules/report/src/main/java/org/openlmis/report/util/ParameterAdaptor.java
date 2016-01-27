/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.report.util;

import groovyjarjarcommonscli.MissingArgumentException;
import org.openlmis.report.annotations.RequiredParam;
import org.openlmis.report.exception.RequiredParameterMissingException;

import java.lang.reflect.Field;
import java.util.Map;

public class ParameterAdaptor {

  private ParameterAdaptor(){

  }

  public static <T> T parse( Map<String, String[]> params ,Class<T> ParamObjectType) {

    try {
      T result = ParamObjectType.newInstance();
      for (Field f : ParamObjectType.getDeclaredFields()) {
        Class<?> fieldType = f.getType();
        f.setAccessible(true);
        String value = StringHelper.getValue(params, f.getName());
        if (value == null) {
          populateDefaultValue(result, f, fieldType, 0L, 0, false);
        } else {
          adaptDataType(result, f, fieldType, value);
        }
      }
      return validate(result);
    }catch(Exception exp){
       throw new RequiredParameterMissingException(exp.getMessage());
    }
  }

  private static <T> void adaptDataType(T result, Field f, Class<?> fieldType, String value) throws IllegalAccessException {
    if (fieldType == String.class) {
      f.set(result, value);
    }
    else if (fieldType == Long.class) {
      f.set(result, Long.parseLong(value));
    } else if (fieldType == Integer.class) {
      f.set(result, Integer.parseInt(value));
    } else if(fieldType == Boolean.class){
      f.set(result, Boolean.parseBoolean(value));
    }
  }

  private static <T> void populateDefaultValue(T result, Field f, Class<?> fieldType, long value, int value2, boolean value3) throws IllegalAccessException {
    if (fieldType == Long.class) {
      f.set(result, value);
    } else if (fieldType == Integer.class) {
      f.set(result, value2);
    } else if (fieldType == Boolean.class) {
      f.set(result, value3);
    }
  }

  public static <T> T validate(Object o) throws Exception{
    for(Field f: o.getClass().getDeclaredFields()){
      f.setAccessible(true);
      if(f.isAnnotationPresent(RequiredParam.class) && (f.get(o) == null || (f.getType() == Long.class && f.get(o).equals(0L)))){
        throw new RequiredParameterMissingException(String.format("Required Parameter Missing - %s", f.getName()));
      }
    }
    return (T)o;

  }

}
