package org.openlmis.report.util;

import org.openlmis.report.annotations.RequiredParam;

import java.lang.reflect.Field;
import java.util.Map;

public class ParameterAdaptor {

  public static <T> T parse( Map<String, String[]> params ,Class<T> type) {

    try {
      T result = type.newInstance();

      for (Field f : type.getDeclaredFields()) {
        f.setAccessible(true);
        String value = StringHelper.getValue(params, f.getName());
        if (value != null) {
          if (f.getType() == String.class) {
            f.set(result, value);
          } else if (f.getType() == Long.class) {
            f.set(result, Long.parseLong(value));
          } else if (f.getType() == Integer.class) {
            f.set(result, Integer.parseInt(value));
          }
        }
      }
      return validate(result);
    }catch(Exception exp){
       throw new RuntimeException(exp);
    }finally {

    }
  }

  public static <T> T validate(Object o) throws Exception{
    for(Field f: o.getClass().getDeclaredFields()){
      if(f.isAnnotationPresent(RequiredParam.class) && f.get(o) == null){
        throw new Exception("Required Parameter Missing");
      }
    }
    return (T)o;

  }

}
