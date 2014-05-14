package org.openlmis.report.util;


import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class StringHelper {

  public static boolean isBlank(Map<String, String[]> map, String key)  {
    if(map.containsKey(key)){
      return StringUtils.isBlank(map.get(key)[0]);
    }
    return true;
  }
}
