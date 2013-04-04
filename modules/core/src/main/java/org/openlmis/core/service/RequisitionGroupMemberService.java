/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.RequisitionGroup;
import org.openlmis.core.domain.RequisitionGroupMember;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.*;
import org.openlmis.core.repository.mapper.RequisitionGroupProgramScheduleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.apache.commons.collections.ListUtils.intersection;


@Service
@NoArgsConstructor
public class RequisitionGroupMemberService {
  private RequisitionGroupMemberRepository requisitionGroupMemberRepository;
  private FacilityRepository facilityRepository;
  private RequisitionGroupRepository requisitionGroupRepository;
  private RequisitionGroupProgramScheduleRepository requisitionGroupProgramScheduleRepository;
  private ProgramRepository programRepository;

  @Autowired
  public RequisitionGroupMemberService(RequisitionGroupMemberRepository requisitionGroupMemberRepository,
                                       FacilityRepository facilityRepository, RequisitionGroupRepository requisitionGroupRepository,
                                       RequisitionGroupProgramScheduleRepository requisitionGroupProgramScheduleRepository,
                                       ProgramRepository programRepository) {
    this.requisitionGroupMemberRepository = requisitionGroupMemberRepository;
    this.facilityRepository = facilityRepository;
    this.requisitionGroupRepository = requisitionGroupRepository;
    this.requisitionGroupProgramScheduleRepository = requisitionGroupProgramScheduleRepository;
    this.programRepository = programRepository;
  }

  public void save(RequisitionGroupMember requisitionGroupMember) {
    requisitionGroupMember.getFacility().setId(facilityRepository.getIdForCode(
      requisitionGroupMember.getFacility().getCode()));
    requisitionGroupMember.getRequisitionGroup().setId(
      requisitionGroupRepository.getByCode(requisitionGroupMember.getRequisitionGroup()).getId());

    List<Integer> requisitionGroupProgramIdsForFacility = requisitionGroupMemberRepository.
      getRequisitionGroupProgramIdsForId(requisitionGroupMember.getFacility().getId());

    if (requisitionGroupMember.getRequisitionGroup().getId() == null) {
      throw new DataException("Requisition Group does not exist");
    }

    List<Integer> programIDsForRG = requisitionGroupProgramScheduleRepository.getProgramIDsForRequisitionGroup(requisitionGroupMember.getRequisitionGroup().getId());
    if (programIDsForRG.size() == 0) {
      throw new DataException("No Program(s) mapped for Requisition Group");
    }

    List<Integer> commonProgramsId = intersection(requisitionGroupProgramIdsForFacility, programIDsForRG);
    if (commonProgramsId.size() > 0) {
      Program duplicateProgram = programRepository.getById(commonProgramsId.get(0));
      duplicateProgram.setId(commonProgramsId.get(0));
      RequisitionGroup requisitionGroup = requisitionGroupRepository.
        getRequisitionGroupForProgramAndFacility(duplicateProgram, requisitionGroupMember.getFacility());
      throw new DataException(String.format("Facility %s is already assigned to Requisition Group %s running same program %s",
        requisitionGroupMember.getFacility().getCode(), requisitionGroup.getCode(), duplicateProgram.getCode()));
    }

    requisitionGroupMemberRepository.insert(requisitionGroupMember);
  }
}
