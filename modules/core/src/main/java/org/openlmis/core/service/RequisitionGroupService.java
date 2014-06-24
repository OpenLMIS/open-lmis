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
import org.openlmis.core.domain.*;
import org.openlmis.core.repository.RequisitionGroupRepository;
import org.openlmis.core.repository.SupervisoryNodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Exposes the services for handling RequisitionGroup entity.
 */

@Service
@NoArgsConstructor
public class RequisitionGroupService {

  public static final String REQUISITION_GROUP = "requisitionGroup";

  @Autowired
  private RequisitionGroupRepository requisitionGroupRepository;

  @Autowired
  private SupervisoryNodeRepository supervisoryNodeRepository;

  @Autowired
  private RequisitionGroupMemberService requisitionGroupMemberService;

  @Autowired
  private RequisitionGroupProgramScheduleService requisitionGroupProgramScheduleService;

  @Transactional
  public void saveWithMembersAndSchedules(RequisitionGroup requisitionGroup,
                                          List<RequisitionGroupMember> requisitionGroupMembers,
                                          List<RequisitionGroupProgramSchedule> requisitionGroupProgramSchedules,
                                          Long userId) {
    requisitionGroup.setCreatedBy(userId);
    save(requisitionGroup);
    saveRequisitionGroupProgramSchedules(requisitionGroupProgramSchedules, requisitionGroup, userId);
    saveRequisitionGroupMembers(requisitionGroupMembers, requisitionGroup, userId);
  }

  public void save(RequisitionGroup requisitionGroup) {
    Long supervisoryNodeId = supervisoryNodeRepository.getIdForCode(requisitionGroup.getSupervisoryNode().getCode());
    requisitionGroup.getSupervisoryNode().setId(supervisoryNodeId);

    if (requisitionGroup.getId() == null)
      requisitionGroupRepository.insert(requisitionGroup);
    else
      requisitionGroupRepository.update(requisitionGroup);
  }

  public void saveRequisitionGroupMembers(List<RequisitionGroupMember> requisitionGroupMembers,
                                          RequisitionGroup requisitionGroup,
                                          Long userId) {
    for (RequisitionGroupMember requisitionGroupMember : requisitionGroupMembers) {
      requisitionGroupMember.setRequisitionGroup(requisitionGroup);
      requisitionGroupMember.setCreatedBy(userId);
      requisitionGroupMember.setModifiedBy(userId);
      requisitionGroupMemberService.save(requisitionGroupMember);
    }
  }

  public List<RequisitionGroup> getRequisitionGroupsBy(List<SupervisoryNode> supervisoryNodes) {
    return requisitionGroupRepository.getRequisitionGroups(supervisoryNodes);
  }

  public RequisitionGroup getByCode(RequisitionGroup requisitionGroup) {
    return requisitionGroupRepository.getByCode(requisitionGroup);
  }

  public List<RequisitionGroup> search(String searchParam, String columnName, Pagination pagination) {
    if (columnName.equals(REQUISITION_GROUP)) {
      return requisitionGroupRepository.searchByGroupName(searchParam, pagination);
    }
    return requisitionGroupRepository.searchByNodeName(searchParam, pagination);
  }

  public Integer getTotalRecords(String searchParam, String columnName) {
    if (columnName.equals(REQUISITION_GROUP)) {
      return requisitionGroupRepository.getTotalRecordsForSearchOnGroupName(searchParam);
    }
    return requisitionGroupRepository.getTotalRecordsForSearchOnNodeName(searchParam);
  }

  public RequisitionGroup getBy(Long id) {
    return requisitionGroupRepository.getBy(id);
  }

  public List<RequisitionGroupMember> getMembersBy(Long requisitionGroupId) {
    return requisitionGroupMemberService.getMembersBy(requisitionGroupId);
  }

  public void updateWithMembersAndSchedules(RequisitionGroup requisitionGroup,
                                            List<RequisitionGroupMember> requisitionGroupMembers,
                                            List<RequisitionGroupProgramSchedule> requisitionGroupProgramSchedules,
                                            Long userId) {
    requisitionGroup.setModifiedBy(userId);
    save(requisitionGroup);
    deleteAndInsertRequisitionGroupProgramSchedules(requisitionGroup, requisitionGroupProgramSchedules, userId);
    deleteAndInsertRequisitionGroupMembers(requisitionGroup, requisitionGroupMembers, userId);
  }

  private void deleteAndInsertRequisitionGroupMembers(RequisitionGroup requisitionGroup,
                                                      List<RequisitionGroupMember> requisitionGroupMembers,
                                                      Long userId) {
    requisitionGroupMemberService.deleteMembersForGroup(requisitionGroup.getId());
    for (RequisitionGroupMember requisitionGroupMember : requisitionGroupMembers) {
      requisitionGroupMember.setRequisitionGroup(requisitionGroup);
      requisitionGroupMember.setCreatedBy(userId);
      requisitionGroupMember.setModifiedBy(userId);
      requisitionGroupMemberService.insert(requisitionGroupMember);
    }
  }

  private void deleteAndInsertRequisitionGroupProgramSchedules(RequisitionGroup requisitionGroup,
                                                               List<RequisitionGroupProgramSchedule> requisitionGroupProgramSchedules,
                                                               Long userId) {
    requisitionGroupProgramScheduleService.deleteRequisitionGroupProgramSchedulesFor(requisitionGroup.getId());
    saveRequisitionGroupProgramSchedules(requisitionGroupProgramSchedules, requisitionGroup, userId);
  }

  public void saveRequisitionGroupProgramSchedules(List<RequisitionGroupProgramSchedule> requisitionGroupProgramSchedules,
                                                   RequisitionGroup requisitionGroup,
                                                   Long userId) {
    for (RequisitionGroupProgramSchedule requisitionGroupProgramSchedule : requisitionGroupProgramSchedules) {
      requisitionGroupProgramSchedule.setId(null);
      requisitionGroupProgramSchedule.setRequisitionGroup(requisitionGroup);
      requisitionGroupProgramSchedule.setCreatedBy(userId);
      requisitionGroupProgramSchedule.setModifiedBy(userId);
      requisitionGroupProgramScheduleService.save(requisitionGroupProgramSchedule);
    }
  }
}
