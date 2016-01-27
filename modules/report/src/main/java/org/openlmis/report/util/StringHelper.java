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


import org.apache.commons.lang3.StringUtils;
import org.openlmis.core.domain.BaseModel;

import java.util.List;
import java.util.Map;

public class StringHelper {

  private StringHelper() {

  }

  public static boolean isBlank(Map<String, String[]> map, String key) {
    if (map.containsKey(key)) {
      return StringUtils.isBlank(map.get(key)[0]);
    }
    return true;
  }

  public static String getValue(Map<String, String[]> map, String key) {
    if (!isBlank(map, key)) {
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
      str.deleteCharAt(str.length() - 1);
    }
    str.append("}");

    return str.toString();
  }
}
