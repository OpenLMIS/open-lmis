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
import org.openlmis.core.domain.Refrigerator;
import org.openlmis.distribution.domain.RefrigeratorProblem;
import org.openlmis.distribution.domain.RefrigeratorReading;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

/**
 *  This DTO contains facilityVisitId, Refrigerator entity, Refrigerator Problem entity and client side
 *  representation of Refrigerator Reading attributes.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = NON_EMPTY)
@EqualsAndHashCode(callSuper = false)
public class RefrigeratorReadingDTO extends BaseModel {

  Refrigerator refrigerator;
  //Readings
  Long facilityVisitId;
  Reading temperature;
  Reading functioningCorrectly;
  Reading lowAlarmEvents;
  Reading highAlarmEvents;
  Reading problemSinceLastTime;
  RefrigeratorProblemDTO problems;
  Reading notes;

  public RefrigeratorReading transform() {
    refrigerator.setModifiedBy(this.modifiedBy);
    refrigerator.setCreatedBy(this.createdBy);
    refrigerator.validate();

    Float temperature = Reading.safeRead(this.temperature).parseFloat();
    String functioningCorrectly = Reading.safeRead(this.functioningCorrectly).getEffectiveValue();
    Integer lowAlarmEvents = Reading.safeRead(this.lowAlarmEvents).parsePositiveInt();
    Integer highAlarmEvents = Reading.safeRead(this.highAlarmEvents).parsePositiveInt();
    String problemSinceLastTime = Reading.safeRead(this.problemSinceLastTime).getEffectiveValue();
    RefrigeratorProblemDTO problems = Optional.fromNullable(this.problems).or(new RefrigeratorProblemDTO());
    String notes = Reading.safeRead(this.notes).getEffectiveValue();

    RefrigeratorProblem problem = problems.transform();

    if ("N".equalsIgnoreCase(functioningCorrectly)) {
      problem.validate();
    } else {
      problem = new RefrigeratorProblem();
    }

    RefrigeratorReading reading = new RefrigeratorReading(this.refrigerator, this.facilityVisitId,
            temperature, functioningCorrectly, lowAlarmEvents, highAlarmEvents, problemSinceLastTime,
            problem, notes);
    reading.setCreatedBy(this.createdBy);
    reading.setModifiedBy(this.modifiedBy);
    return reading;
  }
}
