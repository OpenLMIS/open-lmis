/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openlmis.core.service;

import org.openlmis.core.domain.DeliveryZone;
import org.openlmis.core.domain.DeliveryZoneProgramSchedule;
import org.openlmis.core.repository.DeliveryZoneProgramScheduleRepository;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.ProcessingSchedule;
import org.openlmis.core.domain.Program;
import org.openlmis.core.exception.DataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    if (program == null) throw new DataException("program.code.invalid");
    if (!program.isPush()) throw new DataException("error.program.not.push");

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
