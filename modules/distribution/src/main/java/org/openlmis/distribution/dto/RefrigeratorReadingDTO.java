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
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Refrigerator;
import org.openlmis.distribution.domain.RefrigeratorProblem;
import org.openlmis.distribution.domain.RefrigeratorReading;

@AllArgsConstructor
public class RefrigeratorReadingDTO extends BaseModel {

  Long refrigeratorId;
  String brand;
  String model;
  String serialNumber;

  //Readings
  Reading temperature;
  Reading functioningCorrectly;
  Reading lowAlarmEvents;
  Reading highAlarmEvents;
  Reading problemSinceLastTime;
  RefrigeratorProblem problems;
  String notes;

  public RefrigeratorReading transform(Long facilityId) {
    Refrigerator refrigerator = new Refrigerator(brand, serialNumber, model, facilityId);
    refrigerator.setId(refrigeratorId);

    return new RefrigeratorReading(refrigerator, null,
      temperature.parseFloat(),
      functioningCorrectly.getEffectiveValue(),
      lowAlarmEvents.parsePositiveInt(),
      highAlarmEvents.parsePositiveInt(),
      problemSinceLastTime.getEffectiveValue(),
      problems,
      notes);
  }
}
