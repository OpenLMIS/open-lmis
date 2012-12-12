package org.openlmis.rnr.service;

import lombok.NoArgsConstructor;
import org.openlmis.rnr.domain.RequisitionGroupProgramSchedule;
import org.openlmis.rnr.repository.RequisitionGroupProgramScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class RequisitionGroupProgramScheduleService {
    private RequisitionGroupProgramScheduleRepository requisitionGroupProgramScheduleRepository;

    @Autowired
    public RequisitionGroupProgramScheduleService(RequisitionGroupProgramScheduleRepository requisitionGroupProgramScheduleRepository) {
        this.requisitionGroupProgramScheduleRepository = requisitionGroupProgramScheduleRepository;
    }


    public void save(RequisitionGroupProgramSchedule requisitionGroupProgramSchedule) {
        requisitionGroupProgramScheduleRepository.insert(requisitionGroupProgramSchedule);
    }
}
