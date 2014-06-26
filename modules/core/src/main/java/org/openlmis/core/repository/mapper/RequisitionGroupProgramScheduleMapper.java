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

/**
 * RequisitionGroupProgramScheduleMapper maps the RequisitionGroupProgramSchedule mapping entity to corresponding
 * representation in database.
 */
@Repository
public interface RequisitionGroupProgramScheduleMapper {

  @Insert("INSERT INTO requisition_group_program_schedules" +
    "(requisitionGroupId, programId, scheduleId, directDelivery, dropOffFacilityId, createdBy, modifiedBy, modifiedDate) " +
    "VALUES(#{requisitionGroup.id}, #{program.id}, #{processingSchedule.id}, #{directDelivery}, #{dropOffFacility.id}, #{createdBy}, #{createdBy}, COALESCE(#{modifiedDate}, CURRENT_TIMESTAMP))")
  @Options(useGeneratedKeys = true)
  Integer insert(RequisitionGroupProgramSchedule requisitionGroupProgramSchedule);

  @Select("SELECT programId FROM requisition_group_program_schedules WHERE requisitionGroupId = #{requisitionGroupId}")
  List<Long> getProgramIDsById(Long requisitionGroupId);

  @Select(
    "SELECT * FROM requisition_group_program_schedules WHERE requisitionGroupId = #{requisitionGroupId} AND programId = #{programId}")
  @Results(value = {
    @Result(property = "program.id", column = "programId"),
    @Result(property = "processingSchedule.id", column = "scheduleId"),
    @Result(property = "requisitionGroup.id", column = "requisitionGroupId"),
    @Result(property = "dropOffFacility.id", column = "dropOffFacilityId")
  })
  RequisitionGroupProgramSchedule getScheduleForRequisitionGroupIdAndProgramId(
    @Param(value = "requisitionGroupId") Long requisitionGroupId,
    @Param(value = "programId") Long programId);

  @Select({"SELECT rgps.* FROM requisition_group_program_schedules rgps",
    "INNER JOIN Programs p ON rgps.programId = p.id",
    "INNER JOIN Requisition_Groups rg ON rgps.requisitionGroupId = rg.id",
    "WHERE LOWER(rg.code) = LOWER(#{requisitionGroupCode}) AND",
    "LOWER(p.code) = LOWER(#{programCode})"})
  @Results(value = {
    @Result(property = "program.id", column = "programId"),
    @Result(property = "processingSchedule.id", column = "scheduleId"),
    @Result(property = "requisitionGroup.id", column = "requisitionGroupId"),
    @Result(property = "dropOffFacility.id", column = "dropOffFacilityId")
  })
  RequisitionGroupProgramSchedule getScheduleForRequisitionGroupCodeAndProgramCode(
    @Param(value = "requisitionGroupCode") String requisitionGroupCode,
    @Param(value = "programCode") String programCode);

  @Update({"UPDATE requisition_group_program_schedules SET",
    "programId = #{program.id}, scheduleId = #{processingSchedule.id}, directDelivery = #{directDelivery},",
    "dropOffFacilityId = #{dropOffFacility.id}, modifiedBy = #{modifiedBy},",
    "modifiedDate = (COALESCE(#{modifiedDate}, NOW()))",
    "WHERE id = #{id}"})
  void update(RequisitionGroupProgramSchedule requisitionGroupProgramSchedule);

  @Select({"SELECT * FROM requisition_group_program_schedules WHERE requisitionGroupId = #{requisitionGroupId}"})
  @Results(value = {
    @Result(property = "requisitionGroup.id", column = "requisitionGroupId"),
    @Result(property = "program", column = "programId", javaType = Long.class,
      one = @One(select = "org.openlmis.core.repository.mapper.ProgramMapper.getById")),
    @Result(property = "processingSchedule", column = "scheduleId", javaType = Long.class,
      one = @One(select = "org.openlmis.core.repository.mapper.ProcessingScheduleMapper.get")),
    @Result(property = "dropOffFacility", column = "dropOffFacilityId", javaType = Long.class,
      one = @One(select = "org.openlmis.core.repository.mapper.FacilityMapper.getLWById"))
  })
  List<RequisitionGroupProgramSchedule> getByRequisitionGroupId(Long requisitionGroupId);

  @Delete({"DELETE FROM requisition_group_program_schedules WHERE requisitionGroupId = #{requisitionGroupId}"})
  void deleteRequisitionGroupProgramSchedulesFor(Long requisitionGroupId);
}