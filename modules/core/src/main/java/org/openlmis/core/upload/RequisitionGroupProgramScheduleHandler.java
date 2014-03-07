/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.upload;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.RequisitionGroupProgramSchedule;
import org.openlmis.core.service.RequisitionGroupProgramScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * RequisitionGroupProgramScheduleHandler is used for uploads of Requisition Group, Program and Schedule mapping.
 * It uploads each RequisitionGroupProgramSchedule record by record.
 */
@NoArgsConstructor
@Component
public class RequisitionGroupProgramScheduleHandler extends AbstractModelPersistenceHandler {


  private RequisitionGroupProgramScheduleService requisitionGroupProgramScheduleService;

  @Autowired
  public RequisitionGroupProgramScheduleHandler(RequisitionGroupProgramScheduleService requisitionGroupProgramScheduleService) {
    this.requisitionGroupProgramScheduleService = requisitionGroupProgramScheduleService;
  }

  @Override
  protected BaseModel getExisting(BaseModel record) {
    RequisitionGroupProgramSchedule requisitionGroupProgramSchedule = (RequisitionGroupProgramSchedule) record;
    return requisitionGroupProgramScheduleService.getScheduleForRequisitionGroupCodeAndProgramCode(requisitionGroupProgramSchedule);
  }

  @Override
  protected void save(BaseModel record) {
    requisitionGroupProgramScheduleService.save((RequisitionGroupProgramSchedule) record);
  }

  @Override
  public String getMessageKey() {
    return "error.duplicate.requisition.group.program.schedule";
  }

}
