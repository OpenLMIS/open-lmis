/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
package org.openlmis.rnr.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.rnr.search.criteria.RequisitionSearchCriteria;

import java.util.Date;

import static com.natpryce.makeiteasy.Property.newProperty;

public class RequisitionSearchCriteriaBuilder {

  public static Property<RequisitionSearchCriteria, Long> facilityIdProperty = newProperty();
  public static Property<RequisitionSearchCriteria, Long> programIdProperty = newProperty();
  public static Property<RequisitionSearchCriteria, Long> periodIdProperty = newProperty();
  public static Property<RequisitionSearchCriteria, Long> userIdProperty = newProperty();
  public static Property<RequisitionSearchCriteria, Boolean> emergencyFlag = newProperty();
  public static Property<RequisitionSearchCriteria, Boolean> withoutLineItemFlag = newProperty();
  public static Property<RequisitionSearchCriteria, String> startDate = newProperty();
  public static Property<RequisitionSearchCriteria, String> endDate = newProperty();

  private static final Long DEFAULT_FACILITY_ID = null;
  private static final Long DEFAULT_PROGRAM_ID = null;
  private static final Long DEFAULT_USER_ID = 1L;
  private static final Long DEFAULT_PERIOD_ID = null;
  private static final Date DEFAULT_START_DATE = null;
  private static final Date DEFAULT_END_DATE = null;

  public static final Instantiator<RequisitionSearchCriteria> defaultSearchCriteria = new Instantiator<RequisitionSearchCriteria>() {

    @Override
    public RequisitionSearchCriteria instantiate(PropertyLookup<RequisitionSearchCriteria> lookup) {
      RequisitionSearchCriteria searchCriteria = new RequisitionSearchCriteria();
      searchCriteria.setUserId(lookup.valueOf(userIdProperty, DEFAULT_USER_ID));
      searchCriteria.setFacilityId(lookup.valueOf(facilityIdProperty, DEFAULT_FACILITY_ID));
      searchCriteria.setProgramId(lookup.valueOf(programIdProperty, DEFAULT_PROGRAM_ID));
      searchCriteria.setPeriodId(lookup.valueOf(periodIdProperty, DEFAULT_PERIOD_ID));
      searchCriteria.setEmergency(lookup.valueOf(emergencyFlag, false));
      searchCriteria.setWithoutLineItems(lookup.valueOf(withoutLineItemFlag, false));
      searchCriteria.setDateRangeStart(lookup.valueOf(startDate, String.valueOf(DEFAULT_START_DATE)));
      searchCriteria.setDateRangeEnd(lookup.valueOf(endDate, String.valueOf(DEFAULT_END_DATE)));

      return searchCriteria;
    }
  };
}
