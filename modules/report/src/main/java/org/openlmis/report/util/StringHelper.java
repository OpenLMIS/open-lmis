package org.openlmis.report.util;


import org.apache.commons.lang3.StringUtils;
import org.openlmis.core.domain.BaseModel;

import java.util.List;
import java.util.Map;

public class StringHelper {

  public static boolean isBlank(Map<String, String[]> map, String key)  {
    if(map.containsKey(key)){
      return StringUtils.isBlank(map.get(key)[0]);
    }
    return true;
  }

  public static String getValue(Map<String, String[]> map, String key){
    if(!isBlank(map, key)){
      return map.get(key)[0];
    }
    return null;
  }

  public static String getStringFromListIds(List<? extends BaseModel> list) {
    StringBuilder str = new StringBuilder();
    str.append("{");
    for (BaseModel item : list) {
      str.append(item.getId());
      str.append(",");
    }
    if (str.length() > 1) {
      str.deleteCharAt(str.length()-1);
    }
    str.append("}");

    return str.toString();
  }
}
