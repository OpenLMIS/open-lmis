package org.openlmis.restapi.config;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.util.HashSet;
import java.util.Set;

public class FilterProductConfig {
  public static final Integer FILTER_THRESHOLD_VERSION = 86;
  /**
   * filter wrong kit product
   */
  public static String[] WRONG_KIT_PRODUCTS = new String[]{"SCOD10", "SCOD10-AL", "SCOD12", "SCOD12-AL"};
  public static final Set<String> WRONG_KIT_PRODUCTS_SET = ConvertArrayToSet(WRONG_KIT_PRODUCTS);
  /**
   * filter right kit product
   */
  public static String[] RIGHT_KIT_PRODUCTS = new String[]{"26A01", "26B01", "26A02", "26B02"};
  public static final Set<String> RIGHT_KIT_PRODUCTS_SET = ConvertArrayToSet(RIGHT_KIT_PRODUCTS);

  public static final Set<String> ALL_FILTER_KIT_PRODUCTS_SET = ConvertArrayToSet((String[]) ArrayUtils.addAll(RIGHT_KIT_PRODUCTS, WRONG_KIT_PRODUCTS));

  public static Set ConvertArrayToSet(String[] strings) {
    Set<String> hashSets = new HashSet<>();
    for (String str : strings) {
      hashSets.add(str);
    }
    return hashSets;
  }

  public static boolean isVersionCodeOverThanFilterThresholdVersion(String versionCode) {
    if (!StringUtils.isEmpty(versionCode) && Integer.valueOf(versionCode) >= FILTER_THRESHOLD_VERSION) {
      return true;
    }
    return false;
  }
}
