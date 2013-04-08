/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
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

    try {
      mapper.insert(requisitionGroupProgramSchedule);
    } catch (DuplicateKeyException e) {
      throw new DataException("Duplicate Requisition Group Code And Program Code Combination found");
    }
  }

  public void update(RequisitionGroupProgramSchedule requisitionGroupProgramSchedule) {
    populateIdsForRequisitionProgramScheduleEntities(requisitionGroupProgramSchedule);
    validateRequisitionGroupSchedule(requisitionGroupProgramSchedule);
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
      throw new DataException("Requisition Group Code Does Not Exist");

    if (requisitionGroupProgramSchedule.getProcessingSchedule().getId() == null)
      throw new DataException("Schedule Code Does Not Exist");

    if (requisitionGroupProgramSchedule.isDirectDelivery() && requisitionGroupProgramSchedule.getDropOffFacility() != null)
      throw new DataException("Incorrect combination of Direct Delivery and Drop off Facility");

    if (!requisitionGroupProgramSchedule.isDirectDelivery() && requisitionGroupProgramSchedule.getDropOffFacility() == null)
      throw new DataException("Drop off facility code not defined");

    if (requisitionGroupProgramSchedule.getDropOffFacility() != null && requisitionGroupProgramSchedule.getDropOffFacility().getId() == null)
      throw new DataException("Drop off facility code is not present");
  }

  public RequisitionGroupProgramSchedule getScheduleForRequisitionGroupAndProgram(Integer requisitionGroupId, Integer programId) {
    return mapper.getScheduleForRequisitionGroupIdAndProgramId(requisitionGroupId, programId);
  }

  public List<Integer> getProgramIDsForRequisitionGroup(Integer requisitionGroupId) {
    return mapper.getProgramIDsById(requisitionGroupId);
  }

  public RequisitionGroupProgramSchedule getScheduleForRequisitionGroupCodeAndProgramCode(String requisitionGroupCode, String programCode) {
    return mapper.getScheduleForRequisitionGroupCodeAndProgramCode(requisitionGroupCode, programCode);
  }
}
