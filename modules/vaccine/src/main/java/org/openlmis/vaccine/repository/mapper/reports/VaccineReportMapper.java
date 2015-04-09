/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.vaccine.repository.mapper.reports;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.vaccine.domain.reports.VaccineReport;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaccineReportMapper {

  @Insert("INSERT into vaccine_reports (periodId, programId, facilityId, status, supervisoryNodeId, majorImmunizationActivities, fixedImmunizationSessions, outreachImmunizationSessions,outreachImmunizationSessionsCanceled, createdBy, createdDate, modifiedBy, modifiedDate) " +
    " values (#{periodId}, #{programId}, #{facilityId}, #{status}, #{supervisoryNodeId}, #{majorImmunizationActivities}, #{fixedImmunizationSessions}, #{outreachImmunizationSessions}, #{outreachImmunizationSessionsCanceled}, #{createdBy}, NOW(), #{modifiedBy}, NOW() )")
  @Options(useGeneratedKeys = true)
  Integer insert(VaccineReport report);

  @Select("SELECT * from vaccine_reports where id = #{id}")
  VaccineReport getById(@Param("id") Long id);

  @Select("SELECT * from vaccine_reports where facilityId = #{facilityId} and programId = #{programId} and periodId = #{periodId}")
  VaccineReport getByPeriodFacilityProgram(@Param("facilityId") Long facilityId, @Param("periodId") Long periodId, @Param("programId") Long programId);

  @Select("SELECT * from vaccine_reports where id = #{id}")
  @Results(value = {
    @Result(property = "id", column = "id"),
    @Result(property = "facilityId", column = "facilityId"),
    @Result(property = "periodId", column = "periodId"),
    @Result(property = "programId", column = "programId"),
    @Result(property = "logisticsLineItems", javaType = List.class, column = "id",
      many = @Many(select = "org.openlmis.vaccine.repository.mapper.reports.VaccineReportLogisticsLineItemMapper.getLineItems")),
    @Result(property = "coverageItems", javaType = List.class, column = "id",
      many = @Many(select = "org.openlmis.vaccine.repository.mapper.reports.VaccineReportCoverageMapper.getLineItems")),
    @Result(property = "adverseEffectLineItems", javaType = List.class, column = "id",
      many = @Many(select = "org.openlmis.vaccine.repository.mapper.reports.VaccineReportAdverseEffectMapper.getLineItems")),
    @Result(property = "columnTemplate", javaType = List.class, column = "programId",
      many = @Many(select = "org.openlmis.vaccine.repository.mapper.VaccineColumnTemplateMapper.getForProgram")),
    @Result(property = "coldChainLineItems", javaType = List.class, column = "id",
      many = @Many(select = "org.openlmis.vaccine.repository.mapper.reports.VaccineReportColdChainMapper.getLineItems")),
    @Result(property = "campaignLineItems", javaType = List.class, column = "id",
      many = @Many(select = "org.openlmis.vaccine.repository.mapper.reports.VaccineReportCampaignLineItemMapper.getLineItems")),
    @Result(property = "diseaseLineItems", javaType = List.class, column = "id",
      many = @Many(select = "org.openlmis.vaccine.repository.mapper.reports.VaccineReportDiseaseLineItemMapper.getLineItems")),
    @Result(property = "vitaminSupplementationLineItems", javaType = List.class, column = "id",
      many = @Many(select = "org.openlmis.vaccine.repository.mapper.reports.VitaminSupplementationLineItemMapper.getLineItems")),
    @Result(property = "period", javaType = ProcessingPeriod.class, column = "periodId",
      many = @Many(select = "org.openlmis.core.repository.mapper.ProcessingPeriodMapper.getById")),
    @Result(property = "facility", javaType = Facility.class, column = "facilityId",
      many = @Many(select = "org.openlmis.core.repository.mapper.FacilityMapper.getById"))
  })
  VaccineReport getByIdWithFullDetails(@Param("id") Long id);

  @Update("UPDATE vaccine_reports" +
      " set" +
      " periodId = #{periodId}, " +
      " programId = #{programId}, " +
      " facilityId = #{facilityId}, " +
      " status = #{status}, " +
      " supervisoryNodeId = #{supervisoryNodeId}, " +
      " majorImmunizationActivities = #{majorImmunizationActivities}, " +
      " fixedImmunizationSessions = #{fixedImmunizationSessions}, " +
      " outreachImmunizationSessions = #{outreachImmunizationSessions}, " +
      " outreachImmunizationSessionsCanceled = #{outreachImmunizationSessionsCanceled}, " +
      " modifiedBy = #{modifiedBy}, " +
      " modifiedDate = NOW() " +
    "where id = #{id}")
  void update(VaccineReport report);

  @Select("select max(s.scheduleId) id from requisition_group_program_schedules s " +
    " join requisition_group_members m " +
    "     on m.requisitionGroupId = s.requisitionGroupId " +
    " where " +
    "   s.programId = #{programId} " +
    "   and m.facilityId = #{facilityId} ")
  Long getScheduleFor(@Param("facilityId") Long facilityId, @Param("programId") Long programId);

  @Select("select * from vaccine_reports " +
    "   where " +
    "     facilityId = #{facilityId} and programId = #{programId} order by id desc limit 1")
  VaccineReport getLastReport(@Param("facilityId") Long facilityId, @Param("programId") Long programId);
}
