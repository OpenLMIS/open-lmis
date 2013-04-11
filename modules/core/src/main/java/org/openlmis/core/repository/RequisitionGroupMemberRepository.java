/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.RequisitionGroup;
import org.openlmis.core.domain.RequisitionGroupMember;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.ProgramMapper;
import org.openlmis.core.repository.mapper.RequisitionGroupMapper;
import org.openlmis.core.repository.mapper.RequisitionGroupMemberMapper;
import org.openlmis.core.repository.mapper.RequisitionGroupProgramScheduleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@NoArgsConstructor
public class RequisitionGroupMemberRepository {

  private RequisitionGroupMemberMapper mapper;
  private RequisitionGroupProgramScheduleMapper requisitionGroupProgramScheduleMapper;
  private RequisitionGroupMapper requisitionGroupMapper;
  private ProgramMapper programMapper;
  private FacilityRepository facilityRepository;
  private RequisitionGroupRepository requisitionGroupRepository;

  @Autowired
  public RequisitionGroupMemberRepository(RequisitionGroupMemberMapper requisitionGroupMemberMapper, RequisitionGroupProgramScheduleMapper requisitionGroupProgramScheduleMapper,
                                          RequisitionGroupMapper requisitionGroupMapper, FacilityRepository facilityRepository, ProgramMapper programMapper, RequisitionGroupRepository requisitionGroupRepository) {
    this.mapper = requisitionGroupMemberMapper;
    this.requisitionGroupProgramScheduleMapper = requisitionGroupProgramScheduleMapper;
    this.requisitionGroupMapper = requisitionGroupMapper;
    this.facilityRepository = facilityRepository;
    this.programMapper = programMapper;
    this.requisitionGroupRepository = requisitionGroupRepository;
  }

  public void insert(RequisitionGroupMember requisitionGroupMember) {
    try {
      mapper.insert(requisitionGroupMember);
    } catch (DataIntegrityViolationException ex) {
      throw new DataException("Facility to Requisition Group mapping already exists");
    }
  }

  public List<Integer> getRequisitionGroupProgramIdsForFacilityId(Integer facilityId) {
    return mapper.getRequisitionGroupProgramIdsForFacilityId(facilityId);
  }

  public RequisitionGroupMember getRequisitionGroupMemberForRequisitionGroupIdAndFacilityId(
    RequisitionGroup requisitionGroup, Facility facility) {
    return mapper.getMappingByRequisitionGroupIdAndFacilityId(requisitionGroup, facility);
  }
}
