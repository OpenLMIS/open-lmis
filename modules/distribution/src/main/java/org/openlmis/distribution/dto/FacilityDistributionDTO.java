/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.distribution.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.distribution.domain.FacilityDistribution;
import org.openlmis.distribution.domain.FacilityVisit;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = NON_EMPTY)
public class FacilityDistributionDTO {

  private FacilityVisit facilityVisit;
  private EpiUseDTO epiUse;
  private DistributionRefrigeratorsDTO refrigerators;
  private VaccinationCoverageDTO coverage;

  public FacilityDistribution transform() {
    return new FacilityDistribution(this.facilityVisit, this.epiUse.transform(), this.refrigerators.transform(), null, this.coverage.transform());
  }


  public void setDistributionId(Long distributionId) {
    facilityVisit.setDistributionId(distributionId);
  }

  public void setFacilityId(Long facilityId) {
    facilityVisit.setFacilityId(facilityId);
  }

  public void setModifiedBy(Long modifiedBy) {
    facilityVisit.setModifiedBy(modifiedBy);
    epiUse.setModifiedBy(modifiedBy);
    refrigerators.setCreatedBy(modifiedBy);
    refrigerators.setModifiedBy(modifiedBy);
    coverage.setModifiedBy(modifiedBy);
  }

}
