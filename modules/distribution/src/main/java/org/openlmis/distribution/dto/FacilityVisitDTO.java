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

import com.google.common.base.Optional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.distribution.domain.FacilityVisit;
import org.openlmis.distribution.domain.ReasonForNotVisiting;

import java.util.Date;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = NON_EMPTY)
@EqualsAndHashCode(callSuper = false)
public class FacilityVisitDTO extends BaseModel {

  private Long distributionId;
  private Long facilityId;
  private Long facilityCatchmentPopulation;

  private FacilitatorDTO confirmedBy;
  private FacilitatorDTO verifiedBy;
  private Reading observations;

  private Reading visitDate;

  private Reading visited;
  private Reading vehicleId;
  private Reading reasonForNotVisiting;
  private Reading otherReasonDescription;

  private Reading synced;

  public FacilityVisit transform() {
    FacilitatorDTO confirmedBy = Optional.fromNullable(this.confirmedBy).or(new FacilitatorDTO());
    FacilitatorDTO verifiedBy = Optional.fromNullable(this.verifiedBy).or(new FacilitatorDTO());
    String observations = Reading.safeRead(this.observations).getEffectiveValue();
    Date visitDate = Reading.safeRead(this.visitDate).parseDate();
    Boolean visited = Reading.safeRead(this.visited).parseBoolean();
    String vehicleId = Reading.safeRead(this.vehicleId).getEffectiveValue();
    ReasonForNotVisiting reasonForNotVisiting = Reading.safeRead(this.reasonForNotVisiting).parseReasonForNotVisiting();
    String otherReasonDescription = Reading.safeRead(this.otherReasonDescription).getEffectiveValue();
    Boolean synced = Reading.safeRead(this.synced).parseBoolean();

    FacilityVisit facilityVisit = new FacilityVisit();

    facilityVisit.setId(this.id);
    facilityVisit.setCreatedBy(this.createdBy);
    facilityVisit.setModifiedBy(this.modifiedBy);
    facilityVisit.setCreatedDate(this.createdDate);
    facilityVisit.setModifiedDate(this.modifiedDate);

    facilityVisit.setDistributionId(distributionId);
    facilityVisit.setFacilityId(facilityId);

    facilityVisit.setConfirmedBy(confirmedBy.transform());
    facilityVisit.setVerifiedBy(verifiedBy.transform());
    facilityVisit.setObservations(observations);

    facilityVisit.setVisitDate(visitDate);

    facilityVisit.setVisited(visited);
    facilityVisit.setVehicleId(vehicleId);
    facilityVisit.setReasonForNotVisiting(reasonForNotVisiting);
    facilityVisit.setOtherReasonDescription(otherReasonDescription);

    facilityVisit.setSynced(synced);

    return facilityVisit;
  }

}
