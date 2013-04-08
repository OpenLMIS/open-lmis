/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.RequisitionGroupProgramSchedule;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequisitionGroupProgramScheduleMapper {

  @Insert("INSERT INTO requisition_group_program_schedules" +
    "(requisitionGroupId, programId, scheduleId, directDelivery, dropOffFacilityId, modifiedBy, modifiedDate) " +
    "VALUES(#{requisitionGroup.id}, #{program.id}, #{processingSchedule.id}, #{directDelivery}, #{dropOffFacility.id}, #{modifiedBy}, #{modifiedDate})")
    @Options(useGeneratedKeys = true)
  Integer insert(RequisitionGroupProgramSchedule requisitionGroupProgramSchedule);

  @Select("SELECT programId FROM requisition_group_program_schedules WHERE requisitionGroupId = #{requisitionGroupId}")
  List<Integer> getProgramIDsById(Integer requisitionGroupId);

  @Select("SELECT * FROM requisition_group_program_schedules WHERE requisitionGroupId = #{requisitionGroupId} AND programId = #{programId}")
  @Results(value = {
    @Result(property = "program.id", column = "programId"),
    @Result(property = "processingSchedule.id", column = "scheduleId"),
    @Result(property = "requisitionGroup.id", column = "requisitionGroupId"),
    @Result(property = "dropOffFacility.id", column = "dropOffFacilityId")
  })
  RequisitionGroupProgramSchedule getScheduleForRequisitionGroupIdAndProgramId(
    @Param(value = "requisitionGroupId") Integer requisitionGroupId,
    @Param(value = "programId") Integer programId);

  @Select({"SELECT rgps.* FROM Requisition_Group_Program_Schedules rgps",
    "INNER JOIN Programs p ON rgps.programId = p.id",
    "INNER JOIN Requisition_Groups rg ON rgps.requisitionGroupId = rg.id",
    "WHERE lower(rg.code) = lower(#{requisitionGroupCode}) AND",
    "lower(p.code) = lower(#{programCode})"})
  @Results(value = {
    @Result(property = "program.id", column = "programId"),
    @Result(property = "processingSchedule.id", column = "scheduleId"),
    @Result(property = "requisitionGroup.id", column = "requisitionGroupId"),
    @Result(property = "dropOffFacility.id", column = "dropOffFacilityId")
  })
  RequisitionGroupProgramSchedule getScheduleForRequisitionGroupCodeAndProgramCode(
    @Param(value = "requisitionGroupCode") String requisitionGroupCode,
    @Param(value = "programCode") String programCode);

  @Update("UPDATE Requisition_Group_Program_Schedules SET " +
    "programId=#{requisitionGroupProgramSchedule.program.id}, " +
    "scheduleId=#{requisitionGroupProgramSchedule.processingSchedule.id}, " +
    "directDelivery=#{requisitionGroupProgramSchedule.directDelivery}, " +
    "dropOffFacilityId=#{requisitionGroupProgramSchedule.dropOffFacility.id} " +
    "where id=#{requisitionGroupProgramSchedule.id}")
  void update(@Param(value = "requisitionGroupProgramSchedule") RequisitionGroupProgramSchedule requisitionGroupProgramSchedule);
}