/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.equipment.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EquipmentInventoryStatus extends BaseModel {

  Long inventoryId;
  Date effectiveDateTime;
  Long statusId;
  Long notFunctionalStatusId;

  public Boolean equals(EquipmentInventoryStatus other) {
    // When comparing, should be equivalent when statuses are the same and not functional statuses are both null, or
    // both not null and equal to each other.
    return other != null
        && statusId.equals(other.getStatusId())
        && ((notFunctionalStatusId == null && other.notFunctionalStatusId == null)
          || (notFunctionalStatusId != null && other.notFunctionalStatusId != null
            && notFunctionalStatusId.equals(other.notFunctionalStatusId)));
  }
}
