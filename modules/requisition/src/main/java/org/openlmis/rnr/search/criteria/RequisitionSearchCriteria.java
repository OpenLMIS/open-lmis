/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.rnr.search.criteria;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.Date;

import static java.lang.Boolean.FALSE;
import static lombok.AccessLevel.NONE;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class RequisitionSearchCriteria {

  Long userId;
  Long facilityId;
  Long programId;
  Long periodId;
  boolean withoutLineItems;
  Date dateRangeStart;
  Date dateRangeEnd;

  @Getter(NONE)
  Boolean emergency = FALSE;

  public Boolean isEmergency() {
    return emergency;
  }
}
