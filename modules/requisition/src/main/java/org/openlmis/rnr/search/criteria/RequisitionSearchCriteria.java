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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.Boolean.FALSE;
import static lombok.AccessLevel.NONE;

/**
 * This class holds various parameters on which rnr can be searched against in database.
 */

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class RequisitionSearchCriteria {

  Long userId;
  Long facilityId;
  Long programId;
  Long periodId;
  boolean withoutLineItems;
  String dateRangeStart;
  String dateRangeEnd;

  @Getter(NONE)
  Boolean emergency = FALSE;

  public Boolean isEmergency() {
    return emergency;
  }

  public Date getRangeStart() {
    Date rangeStartDate;
    try {
      rangeStartDate = new SimpleDateFormat("dd-MM-yyyy").parse(this.dateRangeStart);
    } catch (ParseException e) {
      rangeStartDate = null;
    }
    return rangeStartDate;
  }

  public Date getRangeEnd() {
    Date rangeEndDate;
    try {
      rangeEndDate = new SimpleDateFormat("dd-MM-yyyy").parse(this.dateRangeEnd);
    } catch (ParseException e) {
      rangeEndDate = null;
    }
    return rangeEndDate;
  }
}
