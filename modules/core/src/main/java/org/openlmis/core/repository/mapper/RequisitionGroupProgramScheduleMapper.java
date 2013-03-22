/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.core.domain.RequisitionGroupProgramSchedule;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequisitionGroupProgramScheduleMapper {

  @Insert("INSERT INTO requisition_group_program_schedules" +
      "(requisitionGroupId, programId, scheduleId, directDelivery, dropOffFacilityId, modifiedBy, modifiedDate) " +
      "VALUES(#{requisitionGroup.id}, #{program.id}, #{schedule.id}, #{directDelivery}, #{dropOffFacility.id}, #{modifiedBy}, #{modifiedDate})")
  Integer insert(RequisitionGroupProgramSchedule requisitionGroupProgramSchedule);

  @Select("SELECT programId FROM requisition_group_program_schedules WHERE requisitionGroupId = #{requisitionGroupId}")
  List<Integer> getProgramIDsById(Integer requisitionGroupId);

  @Select("SELECT scheduleId FROM requisition_group_program_schedules WHERE requisitionGroupId = #{requisitionGroupId} AND programId = #{programId}")
  List<Integer> getScheduleIDsForRequisitionGroupAndProgram(@Param(value = "requisitionGroupId") Integer requisitionGroupId, @Param(value = "programId") Integer programId);
}