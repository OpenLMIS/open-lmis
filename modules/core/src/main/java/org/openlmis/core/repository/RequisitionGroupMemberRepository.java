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
import org.openlmis.core.domain.RequisitionGroup;
import org.openlmis.core.domain.RequisitionGroupMember;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.RequisitionGroupMemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * RequisitionGroupMemberRepository is Repository class for RequisitionGroupMember related database operations.
 */

@Repository
@NoArgsConstructor
public class RequisitionGroupMemberRepository {

  private RequisitionGroupMemberMapper mapper;

  @Autowired
  public RequisitionGroupMemberRepository(RequisitionGroupMemberMapper requisitionGroupMemberMapper) {
    this.mapper = requisitionGroupMemberMapper;
  }

  public void insert(RequisitionGroupMember requisitionGroupMember) {
    try {
      mapper.insert(requisitionGroupMember);
    } catch (DataIntegrityViolationException ex) {
      throw new DataException("error.facility.requisition.group.mapping.exists");
    }
  }

  public List<Long> getRequisitionGroupProgramIdsForFacilityId(Long facilityId) {
    return mapper.getRequisitionGroupProgramIdsForFacilityId(facilityId);
  }

  public RequisitionGroupMember getRequisitionGroupMemberForRequisitionGroupIdAndFacilityId(
    RequisitionGroup requisitionGroup, Facility facility) {
    return mapper.getMappingByRequisitionGroupIdAndFacilityId(requisitionGroup, facility);
  }

  public void update(RequisitionGroupMember requisitionGroupMember) {
    mapper.update(requisitionGroupMember);
  }

  public void removeRequisitionGroupMember(RequisitionGroup requisitionGroup, Facility facility)
  {
      mapper.removeRequisitionGroupMember(requisitionGroup, facility);
  }

  public List<RequisitionGroupMember> getAllRequisitionGroupMembersByFacility(Long facilityId) {
    return mapper.getAllRequisitionGroupMembersByFacility(facilityId);
  }

  public void updateMembersForVirtualFacilities(Facility parentFacility) {
    mapper.deleteMembersForVirtualFacility(parentFacility);
    mapper.copyToVirtualFacilities(parentFacility);
  }

  public void deleteMembersFor(Facility facility) {
    mapper.deleteMembersFor(facility);
  }

  public List<RequisitionGroupMember> getMembersBy(Long requisitionGroupId) {
    return mapper.getMembersBy(requisitionGroupId);
  }

  public void deleteMembersForGroup(Long requisitionGroupId) {
    mapper.deleteMemberForGroup(requisitionGroupId);
  }
}
