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

package org.openlmis.equipment.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EquipmentInventoryStatus extends BaseModel {

  private Long inventoryId;
  private Long statusId;
  private Long notFunctionalStatusId;

  @Override
  public boolean equals(Object other) {
    // When comparing, should be equivalent when statuses are the same and not functional statuses are both null, or
    // both not null and equal to each other.
    try {
      EquipmentInventoryStatus otherStatus = (EquipmentInventoryStatus)other;
      return otherStatus != null
          && statusId.equals(otherStatus.getStatusId())
          && ((notFunctionalStatusId == null && otherStatus.notFunctionalStatusId == null)
          || (notFunctionalStatusId != null && otherStatus.notFunctionalStatusId != null
          && notFunctionalStatusId.equals(otherStatus.notFunctionalStatusId)));
    } catch (ClassCastException cce) {
      throw new IllegalArgumentException();
    }
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }
}
