/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.RequisitionGroupProgramSchedule;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequisitionGroupProgramScheduleMapper {

  @Insert("INSERT INTO requisition_group_program_schedules" +
    "(requisitionGroupId, programId, scheduleId, directDelivery, dropOffFacilityId, createdBy, modifiedBy, modifiedDate) " +
    "VALUES(#{requisitionGroup.id}, #{program.id}, #{processingSchedule.id}, #{directDelivery}, #{dropOffFacility.id}, #{createdBy}, #{modifiedBy}, #{modifiedDate})")
  @Options(useGeneratedKeys = true)
  Integer insert(RequisitionGroupProgramSchedule requisitionGroupProgramSchedule);

  @Select("SELECT programId FROM requisition_group_program_schedules WHERE requisitionGroupId = #{requisitionGroupId}")
  List<Long> getProgramIDsById(Long requisitionGroupId);

  @Select("SELECT * FROM requisition_group_program_schedules WHERE requisitionGroupId = #{requisitionGroupId} AND programId = #{programId}")
  @Results(value = {
    @Result(property = "program.id", column = "programId"),
    @Result(property = "processingSchedule.id", column = "scheduleId"),
    @Result(property = "requisitionGroup.id", column = "requisitionGroupId"),
    @Result(property = "dropOffFacility.id", column = "dropOffFacilityId")
  })
  RequisitionGroupProgramSchedule getScheduleForRequisitionGroupIdAndProgramId(
    @Param(value = "requisitionGroupId") Long requisitionGroupId,
    @Param(value = "programId") Long programId);

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
    "dropOffFacilityId=#{requisitionGroupProgramSchedule.dropOffFacility.id}, " +
    "modifiedBy=#{requisitionGroupProgramSchedule.modifiedBy} , modifiedDate=#{requisitionGroupProgramSchedule.modifiedDate} " +
    "where id=#{requisitionGroupProgramSchedule.id}")
  void update(@Param(value = "requisitionGroupProgramSchedule") RequisitionGroupProgramSchedule requisitionGroupProgramSchedule);
}