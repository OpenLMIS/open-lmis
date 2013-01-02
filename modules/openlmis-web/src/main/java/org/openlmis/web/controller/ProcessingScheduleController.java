package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.ProcessingSchedule;
import org.openlmis.core.service.ProcessingScheduleService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@NoArgsConstructor
public class ProcessingScheduleController extends BaseController {

  public static final String SCHEDULES = "schedules";
  public static final String SCHEDULE = "schedule";
  private ProcessingScheduleService processingScheduleService;

  @Autowired
  public ProcessingScheduleController(ProcessingScheduleService processingScheduleService) {
    this.processingScheduleService = processingScheduleService;
  }

  @RequestMapping(value = "/schedules", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("hasPermission('','MANAGE_SCHEDULE')")
  public ResponseEntity<OpenLmisResponse> getAll() {
    return OpenLmisResponse.response(SCHEDULES, processingScheduleService.getAll());
  }

  @RequestMapping(value = "/schedules", method = RequestMethod.POST, headers = "Accept=application/json")
  @PreAuthorize("hasPermission('','MANAGE_SCHEDULE')")
  public ResponseEntity<OpenLmisResponse> create(@RequestBody ProcessingSchedule processingSchedule) {
    return saveSchedule(processingSchedule);
  }

  @RequestMapping(value = "/schedules/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
  @PreAuthorize("hasPermission('','MANAGE_SCHEDULE')")
  public ResponseEntity<OpenLmisResponse> update(@RequestBody ProcessingSchedule processingSchedule, @PathVariable("id") Integer id) {
    processingSchedule.setId(id);
    return saveSchedule(processingSchedule);
  }

  private ResponseEntity<OpenLmisResponse> saveSchedule(ProcessingSchedule processingSchedule) {
    try {
      final ProcessingSchedule savedSchedule = processingScheduleService.save(processingSchedule);
      return OpenLmisResponse.response(SCHEDULE, savedSchedule);
    } catch (Exception e) {
      return OpenLmisResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }
}