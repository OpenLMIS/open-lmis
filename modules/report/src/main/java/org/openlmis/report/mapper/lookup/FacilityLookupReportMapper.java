/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report.mapper.lookup;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.report.model.dto.Facility;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacilityLookupReportMapper {

    @Select("SELECT id, code, name" +
            "   FROM " +
            "       facilities order by name")
    List<Facility> getAll();

    @Select("SELECT * " +
            "   FROM " +
            "       facilities where code = #{code}")
    Facility getFacilityByCode(String code);

    @Select("SELECT f.id, f.code, f.name" +
      "   FROM " +
      "       facilities f join programs_supported ps " +
      "          on ps.facilityid = f.id " +
      "          where ps.programid = #{program} and ps.active = true  order by f.name")
    List<Facility> getFacilitiesByProgram(@Param("program") Long program);

  @Select("SELECT f.id, f.code, f.name" +
      "   FROM " +
      "       facilities f  " +
      "          join programs_supported ps \n" +
      "            on ps.facilityid = f.id\n" +
      "          join requisition_group_members m \n" +
      "            on m.facilityId = f.id\n" +
      "          join requisition_group_program_schedules rps\n" +
      "            on m.requisitionGroupId = rps.requisitionGroupId and ps.programId = rps.programId " +
      "        where " +
      "             ps.programid = #{program} " +
      "             and rps.scheduleid = #{schedule} " +
      "             and ps.active = true  " +
      "        order by f.name")
    List<Facility> getFacilitiesByProgramSchedule(@Param("program") Long program, @Param("schedule") Long schedule);

  @Select("SELECT f.id, f.code, f.name" +
      "   FROM " +
      "       facilities f " +
      "          join programs_supported ps \n" +
      "            on ps.facilityid = f.id\n" +
      "          join requisition_group_members m \n" +
      "            on m.facilityId = f.id\n" +
      "          join requisition_group_program_schedules rps\n" +
      "            on m.requisitionGroupId = rps.requisitionGroupId and ps.programId = rps.programId\n" +
      "        where " +
      "             ps.programid = #{program} " +
      "             and rps.scheduleid = #{schedule} " +
      "             and f.typeid = #{type} " +
      "             and ps.active = true  " +
      "        order by f.name")
    List<Facility> getFacilitiesByPrgraomScheduleType(@Param("program") Long program, @Param("schedule") Long schedule, @Param("type") Long type);

}
