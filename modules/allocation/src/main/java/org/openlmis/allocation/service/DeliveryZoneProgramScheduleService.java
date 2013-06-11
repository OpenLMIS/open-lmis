/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openlmis.allocation.service;

import org.openlmis.allocation.domain.DeliveryZone;
import org.openlmis.allocation.domain.DeliveryZoneProgramSchedule;
import org.openlmis.allocation.repository.DeliveryZoneProgramScheduleRepository;
import org.openlmis.core.domain.ProcessingSchedule;
import org.openlmis.core.domain.Program;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.ProcessingScheduleService;
import org.openlmis.core.service.ProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeliveryZoneProgramScheduleService {

  public static final String PROGRAM_CODE_INVALID = "program.code.invalid";
  public static final String ERROR_PROGRAM_NOT_PUSH = "error.program.not.push";

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
    if (program == null) throw new DataException(PROGRAM_CODE_INVALID);
    if (!program.isPush()) throw new DataException(ERROR_PROGRAM_NOT_PUSH);

    deliveryZoneProgramSchedule.setProgram(program);
  }

  public DeliveryZoneProgramSchedule getByDeliveryZoneCodeAndProgramCode(String deliveryZoneCode, String programCode) {
    return repository.getByDeliveryZoneCodeAndProgramCode(deliveryZoneCode, programCode);
  }

  public List<Long> getProgramIdsForDeliveryZones(Long deliveryZoneId) {
    return repository.getProgramIdsForDeliveryZones(deliveryZoneId);
  }
}
