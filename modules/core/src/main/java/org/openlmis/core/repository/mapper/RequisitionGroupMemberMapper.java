/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.RequisitionGroup;
import org.openlmis.core.domain.RequisitionGroupMember;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequisitionGroupMemberMapper {

  @Insert("INSERT INTO requisition_group_members" +
    "(requisitionGroupId, facilityId, createdBy, modifiedBy, modifiedDate) " +
    "VALUES (#{requisitionGroup.id}, #{facility.id}, #{createdBy}, #{modifiedBy}, #{modifiedDate})")
  @Options(useGeneratedKeys = true)
  Integer insert(RequisitionGroupMember requisitionGroupMember);

  @Select({"SELECT rgps.programId FROM requisition_groups rg",
    "INNER JOIN requisition_group_program_schedules rgps ON rg.id = rgps.requisitionGroupId",
    "INNER JOIN requisition_group_members rgm ON rg.id = rgm.requisitionGroupId",
    "WHERE rgm.facilityId = #{facilityId}"})
  List<Long> getRequisitionGroupProgramIdsForFacilityId(Long facilityId);

  @Select({"SELECT *",
    "FROM requisition_group_members",
    "WHERE requisitionGroupId = #{requisitionGroup.id}",
    "AND facilityId = #{facility.id}"})
  RequisitionGroupMember getMappingByRequisitionGroupIdAndFacilityId(
    @Param(value = "requisitionGroup") RequisitionGroup requisitionGroup,
    @Param(value = "facility") Facility facility);

  @Update("UPDATE requisition_group_members " +
    "SET modifiedBy=#{modifiedBy}, modifiedDate=#{modifiedDate} WHERE " +
    "requisitionGroupId = #{requisitionGroup.id} AND facilityId = #{facility.id}")
  void update(RequisitionGroupMember requisitionGroupMember);
}