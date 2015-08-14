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
import org.openlmis.core.domain.ProcessingSchedule;
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
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

import static org.openlmis.core.web.OpenLmisResponse.error;
import static org.openlmis.core.web.OpenLmisResponse.success;

/**
 * This controller handles endpoint related to list, create, update, get details for a schedule
 */

@Controller
@NoArgsConstructor
public class ProcessingScheduleController extends BaseController {

  public static final String SCHEDULES = "schedules";
  public static final String SCHEDULE = "schedule";

  @Autowired
  private ProcessingScheduleService processingScheduleService;

  @RequestMapping(value = "/schedules", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SCHEDULE, MANAGE_REQUISITION_GROUP')")
  public ResponseEntity<OpenLmisResponse> getAll() {
    return OpenLmisResponse.response(SCHEDULES, processingScheduleService.getAll());
  }

  @RequestMapping(value = "/schedules", method = RequestMethod.POST, headers = "Accept=application/json")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SCHEDULE')")
  public ResponseEntity<OpenLmisResponse> create(@RequestBody ProcessingSchedule processingSchedule, HttpServletRequest request) {
    processingSchedule.setCreatedBy(loggedInUserId(request));
    processingSchedule.setModifiedBy(loggedInUserId(request));
    return saveSchedule(processingSchedule, true);
  }

  @RequestMapping(value = "/schedules/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SCHEDULE')")
  public ResponseEntity<OpenLmisResponse> update(@RequestBody ProcessingSchedule processingSchedule, @PathVariable("id") Long id, HttpServletRequest request) {
    processingSchedule.setId(id);
    processingSchedule.setModifiedBy(loggedInUserId(request));
    return saveSchedule(processingSchedule, false);
  }

  @RequestMapping(value = "/schedules/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SCHEDULE')")
  public ResponseEntity<OpenLmisResponse> get(@PathVariable("id") Long id) {
    try {
      ProcessingSchedule processingSchedule = processingScheduleService.get(id);
      return OpenLmisResponse.response(SCHEDULE, processingSchedule);
    } catch (DataException e) {
      return error(e, HttpStatus.NOT_FOUND);
    }
  }

  private ResponseEntity<OpenLmisResponse> saveSchedule(ProcessingSchedule processingSchedule, boolean createOperation) {
    try {
      ProcessingSchedule savedSchedule = processingScheduleService.save(processingSchedule);
      ResponseEntity<OpenLmisResponse> response;
      if (createOperation) {
        response = success(messageService.message("message.schedule.created.success", savedSchedule.getName()));
      } else {
        response = success(messageService.message("message.schedule.updated.success", savedSchedule.getName()));
      }
      response.getBody().addData(SCHEDULE, savedSchedule);
      return response;
    } catch (DataException e) {
      return error(e, HttpStatus.BAD_REQUEST);
    }
  }
}