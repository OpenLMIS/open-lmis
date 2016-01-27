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
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.RequisitionGroup;
import org.openlmis.core.domain.RequisitionGroupMember;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.apache.commons.collections.ListUtils.intersection;

/**
 * Exposes the services for handling RequisitionGroupMember entity.
 */

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
    setIdsForRequisitionGroupMemberEntitiesAndValidate(requisitionGroupMember);

    if (requisitionGroupMember.getId() == null) {
      validateIfFacilityIsAlreadyAssignedToRequisitionGroupForProgram(requisitionGroupMember);
      requisitionGroupMemberRepository.insert(requisitionGroupMember);
    } else {
      requisitionGroupMemberRepository.update(requisitionGroupMember);
    }
  }

  public void updateMembersForVirtualFacilities(Facility parentFacility) {
    requisitionGroupMemberRepository.updateMembersForVirtualFacilities(parentFacility);
  }

  private void validateIfFacilityIsAlreadyAssignedToRequisitionGroupForProgram(RequisitionGroupMember requisitionGroupMember) {
    List<Long> commonProgramIds = getCommonProgramIdsForRequisitionGroupAndFacility(requisitionGroupMember);

    if (commonProgramIds.size() > 0) {
      Program duplicateProgram = programRepository.getById(commonProgramIds.get(0));
      duplicateProgram.setId(commonProgramIds.get(0));
      RequisitionGroup requisitionGroup = requisitionGroupRepository.
        getRequisitionGroupForProgramAndFacility(duplicateProgram, requisitionGroupMember.getFacility());
//      TODO externalize the message
      throw new DataException(String.format("Facility %s is already assigned to Requisition Group %s running same program %s",
        requisitionGroupMember.getFacility().getCode(), requisitionGroup.getCode(), duplicateProgram.getCode()));
    }
  }

  private List<Long> getCommonProgramIdsForRequisitionGroupAndFacility(RequisitionGroupMember requisitionGroupMember) {
    List<Long> requisitionGroupProgramIdsForFacility = requisitionGroupMemberRepository.
      getRequisitionGroupProgramIdsForFacilityId(requisitionGroupMember.getFacility().getId());

    List<Long> programIDsForRG = requisitionGroupProgramScheduleRepository.
      getProgramIDsForRequisitionGroup(requisitionGroupMember.getRequisitionGroup().getId());

    if (programIDsForRG.size() == 0)
      throw new DataException("error.no.program.mapped.requisition.group");

    return intersection(requisitionGroupProgramIdsForFacility, programIDsForRG);
  }

  private void setIdsForRequisitionGroupMemberEntitiesAndValidate(RequisitionGroupMember requisitionGroupMember) {
    RequisitionGroup requisitionGroup = requisitionGroupRepository.getByCode(requisitionGroupMember.getRequisitionGroup());

    if (requisitionGroup == null)
      throw new DataException("error.requisition.group.not.exist");
    requisitionGroupMember.getRequisitionGroup().setId(requisitionGroup.getId());

    Long facilityId = facilityRepository.getIdForCode(requisitionGroupMember.getFacility().getCode());
    requisitionGroupMember.getFacility().setId(facilityId);
  }

  public RequisitionGroupMember getExisting(RequisitionGroupMember record) {
    Facility facility = facilityRepository.getByCode(record.getFacility().getCode());
    RequisitionGroup requisitionGroup = requisitionGroupRepository.getByCode(record.getRequisitionGroup());
    return requisitionGroupMemberRepository.getRequisitionGroupMemberForRequisitionGroupIdAndFacilityId(requisitionGroup, facility);
  }

  public void removeRequisitionGroupMember(RequisitionGroup requisitionGroup, Facility facility){
      requisitionGroupMemberRepository.removeRequisitionGroupMember(requisitionGroup, facility);
  }

  public List<RequisitionGroupMember> getAllRequisitionGroupMembersByFacility(Long facilityId) {
    return requisitionGroupMemberRepository.getAllRequisitionGroupMembersByFacility(facilityId);
  }

  public void deleteMembersFor(Facility facility) {
    requisitionGroupMemberRepository.deleteMembersFor(facility);
  }

  public List<RequisitionGroupMember> getMembersBy(Long requisitionGroupId) {
    return requisitionGroupMemberRepository.getMembersBy(requisitionGroupId);
  }

  public void deleteMembersForGroup(Long requisitionGroupId) {
    requisitionGroupMemberRepository.deleteMembersForGroup(requisitionGroupId);
  }

  public void insert(RequisitionGroupMember member) {
    Long facilityId = facilityRepository.getIdForCode(member.getFacility().getCode());
    member.getFacility().setId(facilityId);
    validateIfFacilityIsAlreadyAssignedToRequisitionGroupForProgram(member);
    requisitionGroupMemberRepository.insert(member);
  }
}
