package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.ProcessingPeriod;
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
import java.util.List;

import static org.openlmis.web.response.OpenLmisResponse.error;

@Controller
@NoArgsConstructor
public class ProcessingPeriodController extends BaseController {

  public static final String PERIODS = "periods";
  private ProcessingScheduleService processingScheduleService;

  @Autowired
  public ProcessingPeriodController(ProcessingScheduleService processingScheduleService) {
    this.processingScheduleService = processingScheduleService;
  }

  @RequestMapping(value = "/schedules/{scheduleId}/periods", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SCHEDULE')")
  public ResponseEntity<OpenLmisResponse> getAll(@PathVariable("scheduleId") Integer scheduleId) {
    List<ProcessingPeriod> periodList = processingScheduleService.getAllPeriods(scheduleId);
    return OpenLmisResponse.response(PERIODS, periodList);
  }

  @RequestMapping(value = "/schedules/{scheduleId}/periods", method = RequestMethod.POST, headers = "Accept=application/json")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SCHEDULE')")
  public ResponseEntity<OpenLmisResponse> save(@PathVariable("scheduleId") Integer scheduleId, @RequestBody ProcessingPeriod processingPeriod, HttpServletRequest request) {
    processingPeriod.setScheduleId(scheduleId);
    processingPeriod.setModifiedBy(loggedInUserId(request));
    try {
      processingScheduleService.savePeriod(processingPeriod);
    } catch (DataException e) {
      return error(e, HttpStatus.BAD_REQUEST);
    }
    ResponseEntity<OpenLmisResponse> successResponse = OpenLmisResponse.success("Period added successfully");
    successResponse.getBody().setData("id", processingPeriod.getId());
    return successResponse;
  }

  @RequestMapping(value = "/periods/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SCHEDULE')")
  public ResponseEntity<OpenLmisResponse> delete(@PathVariable("id") Integer id) {
    try {
      processingScheduleService.deletePeriod(id);
    } catch (DataException e) {
      return error(e, HttpStatus.BAD_REQUEST);
    }
    return OpenLmisResponse.success("Period deleted successfully");
  }
}
