package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.ProcessingSchedule;
import org.openlmis.core.service.ProcessingScheduleService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@NoArgsConstructor
public class ProcessingScheduleController extends BaseController {

    public static final String SCHEDULES = "schedules";
    public static final String SAVED_SCHEDULE = "savedSchedule";
    private ProcessingScheduleService processingScheduleService;

    @Autowired
    public ProcessingScheduleController(ProcessingScheduleService processingScheduleService) {
        this.processingScheduleService = processingScheduleService;
    }

    @RequestMapping(value = "schedule", method = RequestMethod.GET, headers = "Accept=application/json")
    @PreAuthorize("hasPermission('','MANAGE_SCHEDULE')")
    public ResponseEntity<OpenLmisResponse> getAll() {
        return OpenLmisResponse.response(SCHEDULES, processingScheduleService.getAll());
    }

    @RequestMapping(value = "schedule", method = RequestMethod.POST, headers = "Accept=application/json")
    @PreAuthorize("hasPermission('','MANAGE_SCHEDULE')")
    public ResponseEntity<OpenLmisResponse> save(ProcessingSchedule processingSchedule) {
        processingScheduleService.save(processingSchedule);
        return OpenLmisResponse.response(SAVED_SCHEDULE, processingSchedule);
    }
}
