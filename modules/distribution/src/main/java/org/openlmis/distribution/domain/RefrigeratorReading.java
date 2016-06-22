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


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Refrigerator;
import org.openlmis.distribution.dto.Reading;
import org.openlmis.distribution.dto.RefrigeratorReadingDTO;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

/**
 *  RefrigeratorReading represents an entity which keeps track of performance and operating issues of a refrigerator.
 *  It holds Refrigerator Problem entity.
 */

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(include = NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RefrigeratorReading extends BaseModel {

  private Refrigerator refrigerator;

  //Readings
  private Long facilityVisitId;
  private Float temperature;
  private String functioningCorrectly;
  private Integer lowAlarmEvents;
  private Integer highAlarmEvents;
  private String problemSinceLastTime;
  private RefrigeratorProblem problem;
  private String notes;

  public RefrigeratorReading(Refrigerator refrigerator) {
    this.refrigerator = refrigerator;
  }

  public void setCreatedBy(Long createdBy) {
    this.createdBy = createdBy;
    this.problem.setCreatedBy(createdBy);
  }

  public void setModifiedBy(Long modifiedBy) {
    this.modifiedBy = modifiedBy;
    this.problem.setModifiedBy(modifiedBy);
  }

  public RefrigeratorReadingDTO transform() {
    RefrigeratorReadingDTO dto = new RefrigeratorReadingDTO();
    dto.setId(id);
    dto.setCreatedBy(createdBy);
    dto.setCreatedDate(createdDate);
    dto.setModifiedBy(modifiedBy);
    dto.setModifiedDate(modifiedDate);
    dto.setRefrigerator(refrigerator);
    dto.setFacilityVisitId(facilityVisitId);
    dto.setTemperature(new Reading(temperature));
    dto.setFunctioningCorrectly(new Reading(functioningCorrectly));
    dto.setLowAlarmEvents(new Reading(lowAlarmEvents));
    dto.setHighAlarmEvents(new Reading(highAlarmEvents));
    dto.setProblemSinceLastTime(new Reading(problemSinceLastTime));
    dto.setProblems(problem.transform());
    dto.setNotes(new Reading(notes));

    return dto;
  }
}

