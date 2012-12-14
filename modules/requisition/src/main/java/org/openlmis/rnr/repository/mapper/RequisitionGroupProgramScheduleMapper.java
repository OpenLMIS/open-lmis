package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.openlmis.rnr.domain.RequisitionGroupProgramSchedule;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequisitionGroupProgramScheduleMapper {

    @Insert("INSERT into requisition_group_program_schedule(requisition_group_id, program_id, schedule_id, modified_by, modified_date) VALUES(#{requisitionGroup.id}, #{program.id}, #{schedule.id}, #{modifiedBy}, #{modifiedDate})")
    Integer insert(RequisitionGroupProgramSchedule requisitionGroupProgramSchedule);

    @Select("SELECT program_id FROM requisition_group_program_schedule WHERE requisition_group_id = #{rgId}")
    List<Integer> getProgramIDsbyId(Integer rgId);
}