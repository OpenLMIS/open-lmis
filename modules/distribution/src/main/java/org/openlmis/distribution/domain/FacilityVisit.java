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

import com.google.common.base.Optional;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.serializer.DateDeserializer;
import org.openlmis.distribution.dto.FacilityVisitDTO;
import org.openlmis.distribution.dto.Reading;

import java.util.Date;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

/**
 *  FacilityVisit represents an entity which keeps track of whether facility was visited or not. If visited then
 *  records visit date, vehicle and facilitator's information. And if not visited then records reason for not visiting.
 */

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = NON_EMPTY)
public class FacilityVisit extends BaseModel {

  private Long distributionId;
  private Long facilityId;
  private Long facilityCatchmentPopulation;
  private Facilitator confirmedBy;
  private Facilitator verifiedBy;
  private String observations;

  @JsonDeserialize(using = DateDeserializer.class)
  private Date visitDate;

  private Boolean visited;
  private String vehicleId;
  private ReasonForNotVisiting reasonForNotVisiting;
  private String otherReasonDescription;

  private Boolean synced;

  public FacilityVisit(Facility facility, Distribution distribution) {
    this.distributionId = distribution.getId();
    this.facilityId = facility.getId();
    this.facilityCatchmentPopulation = facility.getCatchmentPopulation();
    this.createdBy = distribution.getCreatedBy();
    this.modifiedBy = distribution.getModifiedBy();
  }

  public void setApplicableVisitInfo() {
    if (this.visited == null) {
      return;
    }
    if (this.visited) {
      this.reasonForNotVisiting = null;
      this.otherReasonDescription = null;
      return;
    }
    this.observations = null;
    this.confirmedBy = new Facilitator();
    this.verifiedBy = new Facilitator();
    this.vehicleId = null;
    this.visitDate = null;
  }

  public FacilityVisitDTO transform() {
    Facilitator confirmedBy = Optional.fromNullable(this.confirmedBy).or(new Facilitator());
    Facilitator verifiedBy = Optional.fromNullable(this.verifiedBy).or(new Facilitator());

    FacilityVisitDTO dto = new FacilityVisitDTO();
    dto.setId(id);
    dto.setCreatedBy(createdBy);
    dto.setCreatedDate(createdDate);
    dto.setModifiedBy(modifiedBy);
    dto.setModifiedDate(modifiedDate);
    dto.setDistributionId(distributionId);
    dto.setFacilityId(facilityId);
    dto.setFacilityCatchmentPopulation(facilityCatchmentPopulation);
    dto.setConfirmedBy(confirmedBy.transform());
    dto.setVerifiedBy(verifiedBy.transform());
    dto.setObservations(new Reading(observations));
    dto.setVisitDate(new Reading(visitDate, "yyyy-MM-dd"));
    dto.setVisited(new Reading(visited));
    dto.setVehicleId(new Reading(vehicleId));
    dto.setReasonForNotVisiting(new Reading(reasonForNotVisiting));
    dto.setOtherReasonDescription(new Reading(otherReasonDescription));
    dto.setSynced(new Reading(synced));

    dto.getConfirmedBy().getName().setNotRecorded(false);
    dto.getConfirmedBy().getTitle().setNotRecorded(false);
    dto.getVerifiedBy().getName().setNotRecorded(false);
    dto.getVerifiedBy().getTitle().setNotRecorded(false);
    dto.getObservations().setNotRecorded(false);
    dto.getVisitDate().setNotRecorded(false);
    dto.getVisited().setNotRecorded(false);
    dto.getVehicleId().setNotRecorded(false);
    dto.getReasonForNotVisiting().setNotRecorded(false);
    dto.getOtherReasonDescription().setNotRecorded(false);
    dto.getSynced().setNotRecorded(false);

    return dto;
  }
}
