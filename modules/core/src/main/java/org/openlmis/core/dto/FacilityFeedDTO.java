/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Delegate;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.Transformer;
import org.openlmis.core.domain.*;

import java.util.List;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_EMPTY;
import static lombok.AccessLevel.NONE;
import static org.apache.commons.collections.CollectionUtils.collect;

/**
 * FacilityFeedDTO consolidates facility information like facility code, name, type, geographic zone, etc.
 * to be used while displaying facility information to user,  for eg. in feed.
 */
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonSerialize(include = NON_EMPTY)
public class FacilityFeedDTO extends BaseFeedDTO {
  private interface ExcludedDelegates {
    public GeographicZone getGeographicZone();

    public FacilityType getFacilityType();

    public FacilityOperator getOperatedBy();

    public List<ProgramSupported> getSupportedPrograms();
  }

  @Delegate(excludes = ExcludedDelegates.class)
  @Getter(NONE)
  private Facility facility;
  private String geographicZone;
  private String facilityType;
  private String parentFacility;
  private String operatedBy;
  private List<String> programsSupported;

  @SuppressWarnings("unchecked")
  public FacilityFeedDTO(Facility facility, Facility parentFacility) {
    this.facility = facility;
    this.facilityType = facility.getFacilityType().getName();
    this.geographicZone = facility.getGeographicZone().getName();
    this.operatedBy = (facility.getOperatedBy() != null) ? facility.getOperatedBy().getText() : null;
    this.parentFacility = parentFacility != null ? parentFacility.getCode() : null;
    this.programsSupported = (List<String>) collect(facility.getSupportedPrograms(), new Transformer() {
      @Override
      public Object transform(Object o) {
        return ((ProgramSupported) o).getProgram().getCode();
      }
    });
  }
}
