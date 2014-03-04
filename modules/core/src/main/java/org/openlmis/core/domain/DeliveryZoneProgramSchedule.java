/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

/**
 * DeliveryZoneProgramSchedule represents DeliveryZoneProgramSchedule mapping entity for upload. It is a mapping between delivery zone, program and schedule.
 * Defines the contract for creation/upload, program code, delivery zone code and processing schedule code is mandatory for a
 * DeliveryZoneProgramSchedule mapping.
 */
@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
public class DeliveryZoneProgramSchedule extends BaseModel implements Importable {

  @ImportField(name = "Program", nested = "code", mandatory = true)
  private Program program;

  @ImportField(name = "Delivery Zone", nested = "code", mandatory = true)
  private DeliveryZone deliveryZone;

  @ImportField(name = "Schedule", nested = "code", mandatory = true)
  private ProcessingSchedule schedule;

  public DeliveryZoneProgramSchedule(Long deliveryZoneId, Long programId, Long processingScheduleId) {
    deliveryZone = new DeliveryZone(deliveryZoneId);
    program = new Program(programId);
    schedule = new ProcessingSchedule(processingScheduleId);
  }
}
