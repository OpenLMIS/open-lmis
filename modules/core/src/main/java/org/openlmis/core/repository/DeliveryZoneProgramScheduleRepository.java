/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
package org.openlmis.core.repository;

import org.openlmis.core.domain.DeliveryZoneProgramSchedule;
import org.openlmis.core.domain.ProcessingSchedule;
import org.openlmis.core.repository.mapper.DeliveryZoneProgramScheduleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * DeliveryZoneProgramScheduleRepository is repository class for DeliveryZoneProgramSchedule related database operations.
 */

@Repository
public class DeliveryZoneProgramScheduleRepository {

  @Autowired
  DeliveryZoneProgramScheduleMapper mapper;

  public void insert(DeliveryZoneProgramSchedule schedule) {
    mapper.insert(schedule);
  }

  public void update(DeliveryZoneProgramSchedule schedule) {
    mapper.update(schedule);
  }

  public DeliveryZoneProgramSchedule getByDeliveryZoneCodeAndProgramCode(String deliveryZoneCode, String programCode) {
    return mapper.getByDeliveryZoneCodeAndProgramCode(deliveryZoneCode, programCode);
  }

  public List<Long> getProgramIdsForDeliveryZones(Long deliveryZoneId) {
    return mapper.getProgramsIdsForDeliveryZones(deliveryZoneId);
  }

  public ProcessingSchedule getProcessingScheduleByZoneAndProgram(Long zoneId, Long programId) {
    return mapper.getProcessingScheduleByZoneAndProgram(zoneId, programId);
  }
}
