package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.RequisitionGroupProgramSchedule;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.core.repository.mapper.RequisitionGroupMapper;
import org.openlmis.core.repository.mapper.RequisitionGroupProgramScheduleMapper;
import org.openlmis.core.repository.mapper.ScheduleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

@Repository
@NoArgsConstructor
public class RequisitionGroupProgramScheduleRepository {

    private RequisitionGroupProgramScheduleMapper requisitionGroupProgramScheduleMapper;
    private RequisitionGroupMapper requisitionGroupMapper;
    private ProgramRepository programRepository;
    private ScheduleMapper scheduleMapper;
    private FacilityMapper facilityMapper;

    @Autowired
    public RequisitionGroupProgramScheduleRepository(RequisitionGroupProgramScheduleMapper requisitionGroupProgramScheduleMapper,
                                                     RequisitionGroupMapper requisitionGroupMapper, ProgramRepository programRepository, ScheduleMapper scheduleMapper, FacilityMapper facilityMapper) {
        this.requisitionGroupProgramScheduleMapper = requisitionGroupProgramScheduleMapper;
        this.requisitionGroupMapper = requisitionGroupMapper;
        this.programRepository = programRepository;
        this.scheduleMapper = scheduleMapper;
        this.facilityMapper = facilityMapper;
    }

    public void insert(RequisitionGroupProgramSchedule requisitionGroupProgramSchedule) {
        try {
            requisitionGroupProgramSchedule.getRequisitionGroup().setId(requisitionGroupMapper.getIdForCode(requisitionGroupProgramSchedule.getRequisitionGroup().getCode()));
            requisitionGroupProgramSchedule.getProgram().setId(programRepository.getIdForCode(requisitionGroupProgramSchedule.getProgram().getCode()));
            requisitionGroupProgramSchedule.getSchedule().setId(scheduleMapper.getIdForCode(requisitionGroupProgramSchedule.getSchedule().getCode()));
            Facility dropOffFacility = requisitionGroupProgramSchedule.getDropOffFacility();
            if (dropOffFacility != null)
                requisitionGroupProgramSchedule.getDropOffFacility().setId(facilityMapper.getIdForCode(dropOffFacility.getCode()));

            if (requisitionGroupProgramSchedule.getRequisitionGroup().getId() == null) {
                throw new DataException("Requisition Group Code Does Not Exist");
            }

            if (requisitionGroupProgramSchedule.getSchedule().getId() == null) {
                throw new DataException("Schedule Code Does Not Exist");
            }

            if (requisitionGroupProgramSchedule.isDirectDelivery() && requisitionGroupProgramSchedule.getDropOffFacility() != null) {
                throw new DataException("Incorrect combination of Direct Delivery and Drop off Facility");
            }

            if (!requisitionGroupProgramSchedule.isDirectDelivery() && requisitionGroupProgramSchedule.getDropOffFacility() == null) {
                throw new DataException("Drop off facility code not defined");
            }

            if (requisitionGroupProgramSchedule.getDropOffFacility() != null && requisitionGroupProgramSchedule.getDropOffFacility().getId() == null) {
                throw new DataException("Drop off facility code is not present");
            }

            requisitionGroupProgramScheduleMapper.insert(requisitionGroupProgramSchedule);
        } catch (DuplicateKeyException e) {
            throw new DataException("Duplicate Requisition Group Code And Program Code Combination found");
        }
    }
}
