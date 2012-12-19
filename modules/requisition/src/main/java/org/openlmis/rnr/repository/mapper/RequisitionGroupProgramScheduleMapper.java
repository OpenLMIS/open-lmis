package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.openlmis.rnr.domain.RequisitionGroupProgramSchedule;
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

}