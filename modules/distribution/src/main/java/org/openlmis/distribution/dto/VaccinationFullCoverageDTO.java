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
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.distribution.domain.VaccinationFullCoverage;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_EMPTY;

/**
 *  DTO for VaccinationFullCoverage. It contains facilityVisitId and
 *  client side representation of VaccinationFullCoverage attributes.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(include = NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class VaccinationFullCoverageDTO extends BaseModel {

  private Long facilityVisitId;
  private Reading femaleHealthCenterReading;
  private Reading femaleMobileBrigadeReading;
  private Reading maleHealthCenterReading;
  private Reading maleMobileBrigadeReading;

  public VaccinationFullCoverage transform() {
    VaccinationFullCoverage vaccinationFullCoverage = new VaccinationFullCoverage(this.facilityVisitId, this.femaleHealthCenterReading.parsePositiveInt(),
      this.femaleMobileBrigadeReading.parsePositiveInt(),
      this.maleHealthCenterReading.parsePositiveInt(),
      this.maleMobileBrigadeReading.parsePositiveInt());
    vaccinationFullCoverage.setModifiedBy(this.modifiedBy);
    vaccinationFullCoverage.setCreatedBy(this.createdBy);
    return vaccinationFullCoverage;
  }
}
