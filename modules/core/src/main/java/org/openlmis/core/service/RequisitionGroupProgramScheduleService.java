/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.RequisitionGroupProgramSchedule;
import org.openlmis.core.repository.RequisitionGroupProgramScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class RequisitionGroupProgramScheduleService {
  private RequisitionGroupProgramScheduleRepository requisitionGroupProgramScheduleRepository;

  @Autowired
  public RequisitionGroupProgramScheduleService(RequisitionGroupProgramScheduleRepository requisitionGroupProgramScheduleRepository) {
    this.requisitionGroupProgramScheduleRepository = requisitionGroupProgramScheduleRepository;
  }


  public void save(RequisitionGroupProgramSchedule requisitionGroupProgramSchedule) {
    if (requisitionGroupProgramSchedule.getId() != null) {
      requisitionGroupProgramScheduleRepository.update(requisitionGroupProgramSchedule);
      return;
    }
    requisitionGroupProgramScheduleRepository.insert(requisitionGroupProgramSchedule);
  }

  public RequisitionGroupProgramSchedule getScheduleForRequisitionGroupCodeAndProgramCode(RequisitionGroupProgramSchedule requisitionGroupProgramSchedule) {
    return requisitionGroupProgramScheduleRepository.getScheduleForRequisitionGroupCodeAndProgramCode(
      requisitionGroupProgramSchedule.getRequisitionGroup().getCode(), requisitionGroupProgramSchedule.getProgram().getCode());
  }
}
