/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository.helper;

import org.openlmis.core.domain.BaseModel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to convert object lists to comma separated lists of their Ids
 * @param <T> Any class that extends BaseModel
 */
@Component
public class CommaSeparator<T extends BaseModel> {

  public String commaSeparateIds(List<T> list) {
    List<Long> ids = new ArrayList<>();

    for (T t : list) {
      ids.add(t.getId());
    }
    return ids.toString().replace("[", "{").replace("]", "}");
  }
}
