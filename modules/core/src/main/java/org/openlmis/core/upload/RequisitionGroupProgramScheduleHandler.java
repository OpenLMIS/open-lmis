package org.openlmis.core.upload;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.RequisitionGroupProgramSchedule;
import org.openlmis.core.service.RequisitionGroupProgramScheduleService;
import org.openlmis.upload.Importable;
import org.openlmis.upload.handler.AbstractModelPersistenceHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;


@NoArgsConstructor
@Component("requisitionGroupProgramScheduleHandler")
public class RequisitionGroupProgramScheduleHandler extends AbstractModelPersistenceHandler {


    private RequisitionGroupProgramScheduleService requisitionGroupProgramScheduleService;

    @Autowired
    public RequisitionGroupProgramScheduleHandler(RequisitionGroupProgramScheduleService requisitionGroupProgramScheduleService) {
        this.requisitionGroupProgramScheduleService = requisitionGroupProgramScheduleService;
    }

    @Override
    protected void save(Importable modelClass, String modifiedBy) {
        RequisitionGroupProgramSchedule requisitionGroupProgramSchedule = (RequisitionGroupProgramSchedule) modelClass;
        requisitionGroupProgramSchedule.setModifiedBy(modifiedBy);
        requisitionGroupProgramSchedule.setModifiedDate(new Date());
        requisitionGroupProgramScheduleService.save(requisitionGroupProgramSchedule);
    }
}
