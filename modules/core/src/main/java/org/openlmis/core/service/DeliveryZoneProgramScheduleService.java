/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
package org.openlmis.core.service;

import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.DeliveryZoneProgramScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Exposes the services for handling DeliveryZoneProgramSchedule entity.
 */

@Service
public class DeliveryZoneProgramScheduleService {

  @Autowired
  DeliveryZoneProgramScheduleRepository repository;

  @Autowired
  ProgramService programService;

  @Autowired
  ProcessingScheduleService scheduleService;

  @Autowired
  DeliveryZoneService deliveryZoneService;


  public void save(DeliveryZoneProgramSchedule deliveryZoneProgramSchedule) {
    fillProgram(deliveryZoneProgramSchedule);
    fillDeliveryZone(deliveryZoneProgramSchedule);
    fillSchedule(deliveryZoneProgramSchedule);

    if (deliveryZoneProgramSchedule.getId() == null)
      repository.insert(deliveryZoneProgramSchedule);
    else
      repository.update(deliveryZoneProgramSchedule);
  }

  private void fillSchedule(DeliveryZoneProgramSchedule deliveryZoneProgramSchedule) {
    ProcessingSchedule schedule = scheduleService.getByCode(deliveryZoneProgramSchedule.getSchedule().getCode());
    if (schedule == null) throw new DataException("schedule.code.invalid");

    deliveryZoneProgramSchedule.setSchedule(schedule);
  }

  private void fillDeliveryZone(DeliveryZoneProgramSchedule deliveryZoneProgramSchedule) {
    DeliveryZone deliveryZone = deliveryZoneService.getByCode(deliveryZoneProgramSchedule.getDeliveryZone().getCode());
    if (deliveryZone == null) throw new DataException("deliveryZone.code.invalid");

    deliveryZoneProgramSchedule.setDeliveryZone(deliveryZone);
  }

  private void fillProgram(DeliveryZoneProgramSchedule deliveryZoneProgramSchedule) {
    Program program = programService.getByCode(deliveryZoneProgramSchedule.getProgram().getCode());
    if (program == null)
      throw new DataException("program.code.invalid");
    if (!program.getPush())
      throw new DataException("error.program.not.push");

    deliveryZoneProgramSchedule.setProgram(program);
  }

  public DeliveryZoneProgramSchedule getByDeliveryZoneCodeAndProgramCode(String deliveryZoneCode, String programCode) {
    return repository.getByDeliveryZoneCodeAndProgramCode(deliveryZoneCode, programCode);
  }

  public List<Long> getProgramIdsForDeliveryZones(Long deliveryZoneId) {
    return repository.getProgramIdsForDeliveryZones(deliveryZoneId);
  }

  public List<ProcessingPeriod> getPeriodsForDeliveryZoneAndProgram(Long zoneId, Long programId) {
    ProcessingSchedule schedule = repository.getProcessingScheduleByZoneAndProgram(zoneId, programId);
    return scheduleService.getAllPeriodsBefore(schedule.getId(), null);
  }

  public ProcessingSchedule getProcessingScheduleByZoneAndProgram(long zoneId, long programId) {
    return repository.getProcessingScheduleByZoneAndProgram(zoneId, programId);
  }
}
