package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.openlmis.rnr.domain.RequisitionGroupProgramSchedule;
import org.springframework.stereotype.Repository;

@Repository
public interface RequisitionGroupProgramScheduleMapper {

    @Insert("INSERT into requisition_group_program_schedule(requisition_group_id, program_id, schedule_id) VALUES(#{requisitionGroup.id}, #{program.id}, #{schedule.id})")
    Integer insert(RequisitionGroupProgramSchedule requisitionGroupProgramSchedule);

}
