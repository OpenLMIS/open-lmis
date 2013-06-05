package org.openlmis.report.mapper.lookup;

import org.apache.ibatis.annotations.Select;
import org.openlmis.report.model.dto.RequisitionGroup;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * e-lmis
 * Created by: Elias Muluneh
 * Date: 4/12/13
 * Time: 2:39 AM
 */
@Repository
public interface RequisitionGroupReportMapper {

    @Select("SELECT id, name, code " +
            "   FROM " +
            "       requisition_groups order by name")
    List<RequisitionGroup> getAll();

    @Select("SELECT id, name, code " +
            "   FROM " +
            "       requisition_groups where id = #{param1} order by name")
    List<RequisitionGroup> getById(int id);

    @Select("SELECT g.id, g.name, g.code " +
            "   FROM " +
            "       requisition_groups g" +
            "       join requisition_group_program_schedules ps on ps.requisitiongroupid = g.id " +
            " where " +
            " ps.programid = cast( #{param1} as int4) and ps.scheduleid = cast( #{param2} as int4) " +
            " and  g.id in (select rgm.requisitiongroupid from requisition_group_members rgm join programs_supported ps on rgm.facilityid = ps.facilityid where ps.programid = cast(#{param1} as int4) ) " +
            " order by g.name")
    List<RequisitionGroup> getByProgramAndSchedule(int program, int schedule);

    @Select("SELECT g.id, g.name, g.code " +
            "   FROM " +
            "       requisition_groups g" +
            "       join requisition_group_program_schedules ps on ps.requisitiongroupid = g.id " +
            " where " +
            " ps.programid = cast( #{param1} as int4) " +
            " and  g.id in (select rgm.requisitiongroupid from requisition_group_members rgm join programs_supported ps on rgm.facilityid = ps.facilityid where ps.programid = cast(#{param1} as int4) ) " +
            " order by g.name")
    List<RequisitionGroup> getByProgram(int program);
}
