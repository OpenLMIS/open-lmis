/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.distribution.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.DeliveryZone;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Program;

import java.util.Map;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_EMPTY;

/**
 *  Distribution is a mapping of DeliveryZone, Program and ProcessingPeriod which holds the data recorded for
 *  facilities under that delivery zone. Also it holds DistributionStatus.
 */

@EqualsAndHashCode(callSuper = false)
@Data
@JsonSerialize(include = NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Distribution extends BaseModel {

  DeliveryZone deliveryZone;
  Program program;
  ProcessingPeriod period;
  DistributionStatus status;
  Map<Long, FacilityDistribution> facilityDistributions;

  @SuppressWarnings("unused")
  public String getZpp() {
    return deliveryZone.getId() + "_" + program.getId() + "_" + period.getId();
  }
}
