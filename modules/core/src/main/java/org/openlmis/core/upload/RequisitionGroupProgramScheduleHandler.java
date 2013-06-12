/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.RequisitionGroupProgramSchedule;
import org.openlmis.core.service.RequisitionGroupProgramScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@NoArgsConstructor
@Component()
public class RequisitionGroupProgramScheduleHandler extends AbstractModelPersistenceHandler {


  private static final String DUPLICATE_REQUISITION_GROUP_AND_PROGRAM_FOUND = "Duplicate Requisition Group Code And Program Code Combination found";

  private RequisitionGroupProgramScheduleService requisitionGroupProgramScheduleService;

  @Autowired
  public RequisitionGroupProgramScheduleHandler(RequisitionGroupProgramScheduleService requisitionGroupProgramScheduleService) {
    this.requisitionGroupProgramScheduleService = requisitionGroupProgramScheduleService;
  }

  @Override
  protected BaseModel getExisting(BaseModel record) {
    RequisitionGroupProgramSchedule requisitionGroupProgramSchedule = (RequisitionGroupProgramSchedule)record;
    return requisitionGroupProgramScheduleService.getScheduleForRequisitionGroupCodeAndProgramCode(requisitionGroupProgramSchedule);
  }

  @Override
  protected void save(BaseModel record) {
    requisitionGroupProgramScheduleService.save((RequisitionGroupProgramSchedule) record);
  }

  @Override
  protected String getDuplicateMessageKey() {
    return DUPLICATE_REQUISITION_GROUP_AND_PROGRAM_FOUND;
  }

}
