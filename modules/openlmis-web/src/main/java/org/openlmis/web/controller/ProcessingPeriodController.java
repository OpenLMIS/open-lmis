/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.ProcessingScheduleService;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.openlmis.core.web.OpenLmisResponse.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * This controller handles endpoint related to list, create, update, get details for a period for given schedule
 */

@Controller
@NoArgsConstructor
public class ProcessingPeriodController extends BaseController {

  public static final String PERIODS = "periods";
  public static final String NEXT_START_DATE = "nextStartDate";

  @Autowired
  private ProcessingScheduleService processingScheduleService;

  @RequestMapping(value = "/schedules/{scheduleId}/periods", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SCHEDULE')")
  public ResponseEntity<OpenLmisResponse> getAll(@PathVariable("scheduleId") Long scheduleId) {
    List<ProcessingPeriod> periodList = processingScheduleService.getAllPeriods(scheduleId);
    ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.response(PERIODS, periodList);
    if (!periodList.isEmpty())
      response.getBody().addData(NEXT_START_DATE, periodList.get(0).getNextStartDate());
    return response;
  }

  @RequestMapping(value = "/schedules/{scheduleId}/periods", method = POST, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SCHEDULE')")
  public ResponseEntity<OpenLmisResponse> save(@PathVariable("scheduleId") Long scheduleId, @RequestBody ProcessingPeriod processingPeriod, HttpServletRequest request) {
    processingPeriod.setScheduleId(scheduleId);

    processingPeriod.setModifiedBy(loggedInUserId(request));

    processingPeriod.setCreatedBy(loggedInUserId(request));


    try {
      processingScheduleService.savePeriod(processingPeriod);
    } catch (Exception e) {
      return error(new DataException(e.getMessage()), HttpStatus.BAD_REQUEST);
    }
    ResponseEntity<OpenLmisResponse> successResponse = success(messageService.message("message.period.added.success"));
    successResponse.getBody().addData("id", processingPeriod.getId());
    return successResponse;
  }

  @RequestMapping(value = "/periods/{id}", method = DELETE, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SCHEDULE')")
  public ResponseEntity<OpenLmisResponse> delete(@PathVariable("id") Long id) {
    try {
      processingScheduleService.deletePeriod(id);
    } catch (DataException e) {
      return error(e, HttpStatus.BAD_REQUEST);
    }
    return success(messageService.message("message.period.deleted.success"));
  }

  @RequestMapping(value = "/periods/{id}", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_DISTRIBUTION')")
  public ResponseEntity<OpenLmisResponse> get(@PathVariable("id") Long id) {
    ProcessingPeriod processingPeriod = processingScheduleService.getPeriodById(id);
    return response("period", processingPeriod);
  }
}
