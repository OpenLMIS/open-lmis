/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.web.controller.vaccine;

import org.openlmis.core.domain.Right;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.vaccine.domain.reports.VaccineReport;
import org.openlmis.vaccine.service.reports.VaccineReportService;
import org.openlmis.web.controller.BaseController;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/vaccine/report/")
public class VaccineReportController extends BaseController {

  @Autowired
  VaccineReportService service;

  @Autowired
  ProgramService programService;

  @Autowired
  FacilityService facilityService;

  @RequestMapping(value = "programs")
  public ResponseEntity<OpenLmisResponse> getPrograms(){
    // TODO: make this pay attention to the programs that are allowed for this user.
    return OpenLmisResponse.response("programs", programService.getAllPushPrograms() );
  }

  @RequestMapping(value = "facilities/{programId}.json", method = RequestMethod.GET)
  public ResponseEntity<OpenLmisResponse> getFacilities(@PathVariable Long programId, HttpServletRequest request){
    Long userId = loggedInUserId(request);

    //TODO: make sure this method also supports home facility.
    return OpenLmisResponse.response("facilities", facilityService.getUserSupervisedFacilities(userId, programId, Right.CREATE_REQUISITION));
  }

  @RequestMapping(value = "periods/{facilityId}/{programId}", method = RequestMethod.GET)
  public ResponseEntity<OpenLmisResponse> getPeriods(@PathVariable Long facilityId, @PathVariable Long programId, HttpServletRequest request){
    Long userId = loggedInUserId(request);

    return OpenLmisResponse.response("periods", service.getPeriodsFor(facilityId, programId));
  }

  @RequestMapping(value = "initialize/{facilityId}/{programId}/{periodId}")
  public ResponseEntity<OpenLmisResponse> initialize(
    @PathVariable Long facilityId,
    @PathVariable Long programId,
    @PathVariable Long periodId,
    HttpServletRequest request
  ){
    return OpenLmisResponse.response("report", service.initialize(facilityId, programId, periodId));
  }


  @RequestMapping(value = "save")
  public ResponseEntity<OpenLmisResponse> save(@RequestBody VaccineReport report, HttpServletRequest request){
    service.save(report);
    return OpenLmisResponse.response("report", report);
  }

  @RequestMapping(value = "submit")
  public ResponseEntity<OpenLmisResponse> submit(@RequestBody VaccineReport report, HttpServletRequest request){
    //service.submit(report);
    return OpenLmisResponse.response("report", report);
  }

}
