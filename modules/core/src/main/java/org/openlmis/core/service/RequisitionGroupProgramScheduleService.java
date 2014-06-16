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

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.RequisitionGroupProgramSchedule;
import org.openlmis.core.repository.RequisitionGroupProgramScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Exposes the services for handling RequisitionGroupProgramSchedule entity.
 */

@Service
@NoArgsConstructor
public class RequisitionGroupProgramScheduleService {

  @Autowired
  private RequisitionGroupProgramScheduleRepository repository;

  public void save(RequisitionGroupProgramSchedule requisitionGroupProgramSchedule) {
    if (requisitionGroupProgramSchedule.getId() != null) {
      repository.update(requisitionGroupProgramSchedule);
      return;
    }
    repository.insert(requisitionGroupProgramSchedule);
  }

  public RequisitionGroupProgramSchedule getScheduleForRequisitionGroupCodeAndProgramCode(RequisitionGroupProgramSchedule requisitionGroupProgramSchedule) {
    return repository.getScheduleForRequisitionGroupCodeAndProgramCode(
      requisitionGroupProgramSchedule.getRequisitionGroup().getCode(), requisitionGroupProgramSchedule.getProgram().getCode());
  }

  public List<RequisitionGroupProgramSchedule> getByRequisitionGroupId(Long requisitionGroupId) {
    return repository.getByRequisitionGroupId(requisitionGroupId);
  }

  public void deleteRequisitionGroupProgramSchedulesFor(Long requisitionGroupId) {
    repository.deleteRequisitionGroupProgramSchedulesFor(requisitionGroupId);
  }
}
