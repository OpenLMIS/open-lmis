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

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.RequisitionGroupProgramSchedule;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.core.repository.mapper.ProcessingScheduleMapper;
import org.openlmis.core.repository.mapper.RequisitionGroupMapper;
import org.openlmis.core.repository.mapper.RequisitionGroupProgramScheduleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * RequisitionGroupProgramScheduleRepository is Repository class for RequisitionGroupProgramSchedule related database operations.
 */

@Repository
@NoArgsConstructor
public class RequisitionGroupProgramScheduleRepository {

  private RequisitionGroupProgramScheduleMapper mapper;
  private RequisitionGroupMapper requisitionGroupMapper;
  private ProgramRepository programRepository;
  private ProcessingScheduleMapper processingScheduleMapper;
  private FacilityMapper facilityMapper;

  @Autowired
  public RequisitionGroupProgramScheduleRepository(
    RequisitionGroupProgramScheduleMapper requisitionGroupProgramScheduleMapper,
    RequisitionGroupMapper requisitionGroupMapper,
    ProgramRepository programRepository,
    ProcessingScheduleMapper processingScheduleMapper,
    FacilityMapper facilityMapper) {

    this.mapper = requisitionGroupProgramScheduleMapper;
    this.requisitionGroupMapper = requisitionGroupMapper;
    this.programRepository = programRepository;
    this.processingScheduleMapper = processingScheduleMapper;
    this.facilityMapper = facilityMapper;
  }

  public void insert(RequisitionGroupProgramSchedule requisitionGroupProgramSchedule) {
    populateIdsForRequisitionProgramScheduleEntities(requisitionGroupProgramSchedule);
    validateRequisitionGroupSchedule(requisitionGroupProgramSchedule);
    validateProgramType(requisitionGroupProgramSchedule);
    try {
      mapper.insert(requisitionGroupProgramSchedule);
    } catch (DuplicateKeyException e) {
      throw new DataException("error.duplicate.requisition.group.program.combination");
    }
  }

  private void validateProgramType(RequisitionGroupProgramSchedule requisitionGroupProgramSchedule) {
    Program program = programRepository.getById(requisitionGroupProgramSchedule.getProgram().getId());
    if (program.getPush()) {
      throw new DataException("error.program.type.not.supported.requisitions");
    }
  }

  public void update(RequisitionGroupProgramSchedule requisitionGroupProgramSchedule) {
    populateIdsForRequisitionProgramScheduleEntities(requisitionGroupProgramSchedule);
    validateRequisitionGroupSchedule(requisitionGroupProgramSchedule);
    validateProgramType(requisitionGroupProgramSchedule);
    mapper.update(requisitionGroupProgramSchedule);
  }

  private void populateIdsForRequisitionProgramScheduleEntities(RequisitionGroupProgramSchedule requisitionGroupProgramSchedule) {
    requisitionGroupProgramSchedule.getRequisitionGroup().setId(
      requisitionGroupMapper.getIdForCode(
        requisitionGroupProgramSchedule.getRequisitionGroup().getCode()));

    requisitionGroupProgramSchedule.getProgram().setId(
      programRepository.getIdByCode(
        requisitionGroupProgramSchedule.getProgram().getCode()));

    requisitionGroupProgramSchedule.getProcessingSchedule().setId(
      processingScheduleMapper.getIdForCode(
        requisitionGroupProgramSchedule.getProcessingSchedule().getCode()));

    Facility dropOffFacility = requisitionGroupProgramSchedule.getDropOffFacility();

    if (dropOffFacility != null)
      requisitionGroupProgramSchedule.getDropOffFacility().setId(
        facilityMapper.getIdForCode(
          dropOffFacility.getCode()));
  }

  private void validateRequisitionGroupSchedule(RequisitionGroupProgramSchedule requisitionGroupProgramSchedule) {
    if (requisitionGroupProgramSchedule.getRequisitionGroup().getId() == null)
      throw new DataException("error.requisition.group.not.exists");

    if (requisitionGroupProgramSchedule.getProcessingSchedule().getId() == null)
      throw new DataException("error.schedule.not.exists");

    if (!requisitionGroupProgramSchedule.isDirectDelivery() && requisitionGroupProgramSchedule.getDropOffFacility() == null)
      throw new DataException("error.drop.off.facility.not.defined");

    if (requisitionGroupProgramSchedule.getDropOffFacility() != null && requisitionGroupProgramSchedule.getDropOffFacility().getId() == null)
      throw new DataException("error.drop.off.facility.not.present");
  }

  public RequisitionGroupProgramSchedule getScheduleForRequisitionGroupAndProgram(Long requisitionGroupId, Long programId) {
    return mapper.getScheduleForRequisitionGroupIdAndProgramId(requisitionGroupId, programId);
  }

  public List<Long> getProgramIDsForRequisitionGroup(Long requisitionGroupId) {
    return mapper.getProgramIDsById(requisitionGroupId);
  }

  public RequisitionGroupProgramSchedule getScheduleForRequisitionGroupCodeAndProgramCode(String requisitionGroupCode, String programCode) {
    return mapper.getScheduleForRequisitionGroupCodeAndProgramCode(requisitionGroupCode, programCode);
  }

  public List<RequisitionGroupProgramSchedule> getByRequisitionGroupId(Long requisitionGroupId) {
    return mapper.getByRequisitionGroupId(requisitionGroupId);
  }

  public void deleteRequisitionGroupProgramSchedulesFor(Long requisitionGroupId) {
    mapper.deleteRequisitionGroupProgramSchedulesFor(requisitionGroupId);
  }
}
