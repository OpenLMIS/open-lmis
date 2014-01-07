/*
 *
 *  * This program is part of the OpenLMIS logistics management information system platform software.
 *  * Copyright © 2013 VillageReach
 *  *
 *  * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  *  
 *  * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 *
 */

package org.openlmis.distribution.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.domain.Facility;

import java.util.List;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(include = NON_EMPTY)
public class FacilityDistribution {

  private FacilityVisit facilityVisit;
  private EpiUse epiUse;
  private DistributionRefrigerators refrigerators;
  private EpiInventory epiInventory;
  private VaccinationCoverage coverage;

  public FacilityDistribution(FacilityVisit facilityVisit, Facility facility, Distribution distribution, List<RefrigeratorReading> readings) {
    this.facilityVisit = facilityVisit;
    this.epiUse = new EpiUse(facility, facilityVisit);
    this.refrigerators = new DistributionRefrigerators(facility, facilityVisit.getDistributionId(), readings);
    this.epiInventory = new EpiInventory(facility, distribution);
  }
}
