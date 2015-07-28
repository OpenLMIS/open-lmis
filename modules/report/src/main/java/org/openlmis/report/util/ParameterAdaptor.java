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
          } else if(f.getType() == Boolean.class){
            f.set(result, Boolean.parseBoolean(value));
          }
        }else{
          if(f.getType() == Long.class){
            f.set(result, 0L);
          }else if(f.getType() == Integer.class){
            f.set(result, 0);
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
      f.setAccessible(true);
      if(f.isAnnotationPresent(RequiredParam.class) && (f.get(o) == null || (f.getType() == Long.class && f.get(o).equals(0L)))){
        throw new Exception(String.format("Required Parameter Missing - %s", f.getName()));
      }
    }
    return (T)o;

  }

}
