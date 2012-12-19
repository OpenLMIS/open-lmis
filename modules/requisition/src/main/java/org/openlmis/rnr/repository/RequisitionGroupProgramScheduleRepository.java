package org.openlmis.rnr.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.core.repository.mapper.ProgramMapper;
import org.openlmis.rnr.domain.RequisitionGroupProgramSchedule;
import org.openlmis.rnr.repository.mapper.RequisitionGroupMapper;
import org.openlmis.rnr.repository.mapper.RequisitionGroupProgramScheduleMapper;
import org.openlmis.rnr.repository.mapper.ScheduleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

@Repository
@NoArgsConstructor
public class RequisitionGroupProgramScheduleRepository {

    private RequisitionGroupProgramScheduleMapper requisitionGroupProgramScheduleMapper;
    private RequisitionGroupMapper requisitionGroupMapper;
    private ProgramMapper programMapper;
    private ScheduleMapper scheduleMapper;
    private FacilityMapper facilityMapper;

    @Autowired
    public RequisitionGroupProgramScheduleRepository(RequisitionGroupProgramScheduleMapper requisitionGroupProgramScheduleMapper,
                                                     RequisitionGroupMapper requisitionGroupMapper, ProgramMapper programMapper, ScheduleMapper scheduleMapper, FacilityMapper facilityMapper) {
        this.requisitionGroupProgramScheduleMapper = requisitionGroupProgramScheduleMapper;
        this.requisitionGroupMapper = requisitionGroupMapper;
        this.programMapper = programMapper;
        this.scheduleMapper = scheduleMapper;
        this.facilityMapper = facilityMapper;
    }

    public void insert(RequisitionGroupProgramSchedule requisitionGroupProgramSchedule) {
        try{
            requisitionGroupProgramSchedule.getRequisitionGroup().setId(requisitionGroupMapper.getIdForCode(requisitionGroupProgramSchedule.getRequisitionGroup().getCode()));
            requisitionGroupProgramSchedule.getProgram().setId(programMapper.getIdByCode(requisitionGroupProgramSchedule.getProgram().getCode()));
            requisitionGroupProgramSchedule.getSchedule().setId(scheduleMapper.getIdForCode(requisitionGroupProgramSchedule.getSchedule().getCode()));
            requisitionGroupProgramSchedule.getDropOffFacility().setId(facilityMapper.getIdForCode(requisitionGroupProgramSchedule.getDropOffFacility().getCode()));

            if (requisitionGroupProgramSchedule.getRequisitionGroup().getId() == null) {
                throw new RuntimeException("Requisition Group Code Does Not Exist");
            }
            if (requisitionGroupProgramSchedule.getProgram().getId() == null){
                throw  new RuntimeException("Program Code Does Not Exist");
            }
            if (requisitionGroupProgramSchedule.getSchedule().getId() == null){
                throw  new RuntimeException("Schedule Code Does Not Exist");
            }

            requisitionGroupProgramScheduleMapper.insert(requisitionGroupProgramSchedule);
        }catch (DuplicateKeyException e){
            throw new RuntimeException("Duplicate Requisition Group Code And Program Code Combination found");
        }
    }
}
