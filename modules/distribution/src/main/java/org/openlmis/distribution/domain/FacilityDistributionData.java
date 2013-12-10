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

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@JsonSerialize(include = NON_EMPTY)
public class FacilityDistributionData {

  private FacilityVisit facilityVisit = new FacilityVisit();
  private EpiUse epiUse = new EpiUse();

  public FacilityDistributionData(EpiUse epiUse) {
    this.epiUse = epiUse;
  }

  public void setDistributionId(Long distributionId) {
    facilityVisit.setDistributionId(distributionId);
    epiUse.setFacilityId(distributionId);
  }

  public void setFacilityId(Long facilityId) {
    facilityVisit.setFacilityId(facilityId);
    epiUse.setFacilityId(facilityId);
  }

  public void setCreatedBy(Long createdBy) {
    facilityVisit.setCreatedBy(createdBy);
    epiUse.setCreatedBy(createdBy);
  }

}
