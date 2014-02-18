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
import org.openlmis.core.domain.Facility;

import java.util.List;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonSerialize(include = NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FacilityDistribution {
  private Long facilityId;
  private String facilityCode;
  private String facilityName;
  private Long population;
  private String geographicZone;
  private FacilityVisit facilityVisit;
  private EpiUse epiUse;
  private DistributionRefrigerators refrigerators;
  private EpiInventory epiInventory;
  private VaccinationFullCoverage fullCoverage;
  private VaccinationChildCoverage childCoverage;
  private VaccinationAdultCoverage adultCoverage;

  public FacilityDistribution(FacilityVisit facilityVisit,
                              Facility facility,
                              Distribution distribution,
                              List<RefrigeratorReading> readings,
                              List<TargetGroupProduct> childrenTargetGroupProducts,
                              List<TargetGroupProduct> adultTargetGroupProducts, List<ProductVial> productVials) {
    this.setFacility(facility);
    this.facilityVisit = facilityVisit;
    this.epiUse = new EpiUse(facility, facilityVisit);
    this.refrigerators = new DistributionRefrigerators(facilityVisit, readings);
    this.epiInventory = new EpiInventory(facilityVisit, facility, distribution);
    this.childCoverage = new VaccinationChildCoverage(facilityVisit, facility, childrenTargetGroupProducts, productVials);
    this.adultCoverage = new VaccinationAdultCoverage(facilityVisit, facility, adultTargetGroupProducts);
  }

  public FacilityDistribution(FacilityVisit facilityVisit, EpiUse epiUse, DistributionRefrigerators refrigerators, EpiInventory epiInventory,
                              VaccinationFullCoverage fullCoverage, VaccinationChildCoverage childCoverage,  VaccinationAdultCoverage adultCoverage) {
    this.facilityVisit = facilityVisit;
    this.epiUse = epiUse;
    this.refrigerators = refrigerators;
    this.epiInventory = epiInventory;
    this.fullCoverage = fullCoverage;
    this.childCoverage = childCoverage;
    this.adultCoverage = adultCoverage;
  }

  public void setFacility(Facility facility) {
    this.facilityId = facility.getId();
    this.facilityCode = facility.getCode();
    this.facilityName = facility.getName();
    this.population = facility.getCatchmentPopulation();
    this.geographicZone = facility.getGeographicZone().getName();
  }
}
