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
import org.openlmis.distribution.domain.DistributionRefrigerators;
import org.openlmis.distribution.domain.EpiInventory;
import org.openlmis.distribution.domain.EpiUse;
import org.openlmis.distribution.domain.FacilityDistribution;
import org.openlmis.distribution.domain.FacilityVisit;
import org.openlmis.distribution.domain.VaccinationAdultCoverage;
import org.openlmis.distribution.domain.VaccinationChildCoverage;
import org.openlmis.distribution.domain.VaccinationFullCoverage;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

/**
 *  This DTO represents a container for FacilityVisit entity, EpiUseDTO, EpiInventoryDTO, DistributionRefrigeratorsDTO,
 *  VaccinationFullCoverageDTO, ChildCoverageDTO, AdultCoverageDTO. It holds all the client side forms included in a
 *  distribution.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = NON_EMPTY)
public class FacilityDistributionDTO {

  private Long facilityId;
  private String facilityCode;
  private String facilityName;
  private Long population;
  private String geographicZone;

  private FacilityVisit facilityVisit;
  private EpiUseDTO epiUse;
  private EpiInventoryDTO epiInventory;
  private DistributionRefrigeratorsDTO refrigerators;
  private VaccinationFullCoverageDTO fullCoverage;
  private ChildCoverageDTO childCoverage;
  private AdultCoverageDTO adultCoverage;

  public FacilityDistributionDTO(FacilityVisit facilityVisit, EpiUseDTO epiUse, EpiInventoryDTO epiInventory,
                                 DistributionRefrigeratorsDTO refrigerators, VaccinationFullCoverageDTO fullCoverage,
                                 ChildCoverageDTO childCoverage, AdultCoverageDTO adultCoverage) {
    this.facilityVisit = facilityVisit;
    this.epiUse = epiUse;
    this.refrigerators = refrigerators;
    this.epiInventory = epiInventory;
    this.fullCoverage = fullCoverage;
    this.childCoverage = childCoverage;
    this.adultCoverage = adultCoverage;
  }

  public FacilityDistribution transform() {
    return new FacilityDistribution(facilityVisit, epiUse.transform(), refrigerators.transform(),
      epiInventory.transform(), fullCoverage.transform(), childCoverage.transform(), adultCoverage.transform());
  }

  public void setDistributionId(Long distributionId) {
    facilityVisit.setDistributionId(distributionId);
  }

  public void setModifiedBy(Long modifiedBy) {
    facilityVisit.setModifiedBy(modifiedBy);
    epiUse.setModifiedBy(modifiedBy);
    epiInventory.setModifiedBy(modifiedBy);
    refrigerators.setCreatedBy(modifiedBy);
    refrigerators.setModifiedBy(modifiedBy);
    fullCoverage.setModifiedBy(modifiedBy);
    fullCoverage.setCreatedBy(modifiedBy);
    childCoverage.setModifiedBy(modifiedBy);
    adultCoverage.setModifiedBy(modifiedBy);
  }
}
