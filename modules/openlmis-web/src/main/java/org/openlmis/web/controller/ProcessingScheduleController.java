package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.ProcessingSchedule;
import org.openlmis.core.exception.DataException;
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

import javax.servlet.http.HttpServletRequest;

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
  public ResponseEntity<OpenLmisResponse> create(@RequestBody ProcessingSchedule processingSchedule, HttpServletRequest request) {
    processingSchedule.setModifiedBy(loggedInUserId(request));
    return saveSchedule(processingSchedule, true);
  }

  @RequestMapping(value = "/schedules/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
  @PreAuthorize("hasPermission('','MANAGE_SCHEDULE')")
  public ResponseEntity<OpenLmisResponse> update(@RequestBody ProcessingSchedule processingSchedule, @PathVariable("id") Integer id, HttpServletRequest request) {
    processingSchedule.setId(id);
    processingSchedule.setModifiedBy(loggedInUserId(request));
    return saveSchedule(processingSchedule, false);
  }

  private ResponseEntity<OpenLmisResponse> saveSchedule(ProcessingSchedule processingSchedule, boolean createOperation) {
    try {
      ProcessingSchedule savedSchedule = processingScheduleService.save(processingSchedule);
      ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.success("'" + savedSchedule.getName() + "' "+ (createOperation?"created":"updated") +" successfully");
      response.getBody().setData(SCHEDULE, savedSchedule);
      return response;
    } catch (DataException e) {
      return OpenLmisResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  public ResponseEntity<OpenLmisResponse> get(Integer id) {
    return OpenLmisResponse.response(SCHEDULE, processingScheduleService.get(id));
  }
}