package org.openlmis.report.mapper;

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
            "       requisition_groups")
    List<RequisitionGroup> getAll();

    @Select("SELECT id, name, code " +
            "   FROM " +
            "       requisition_groups where id = #{param1}")
    List<RequisitionGroup> getById(int id);

    @Select("SELECT g.id, g.name, g.code " +
            "   FROM " +
            "       requisition_groups g" +
            "       join requisition_group_program_schedules ps on ps.requisitiongroupid = g.id " +
            " where " +
            " ps.programid = #{param1} and ps.scheduleid = #{param2}")
    List<RequisitionGroup> getByProgramAndSchedule(int program, int schedule);

}
