/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openlmis.web.controller.vaccine;

import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.core.service.UserService;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.vaccine.domain.reports.VaccineReport;
import org.openlmis.vaccine.dto.OrderRequisitionDTO;
import org.openlmis.vaccine.service.reports.VaccineReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping(value = "/vaccine/report/")
public class VaccineReportController extends BaseController {

  public static final String PERIODS = "periods";
  public static final String REPORT = "report";
  @Autowired
  VaccineReportService service;

  @Autowired
  ProgramService programService;

  @Autowired
  UserService userService;

  @Autowired
  FacilityService facilityService;


  @RequestMapping(value = "periods/{facilityId}/{programId}", method = RequestMethod.GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_IVD')")
  public ResponseEntity<OpenLmisResponse> getPeriods(@PathVariable Long facilityId, @PathVariable Long programId) {
    return OpenLmisResponse.response(PERIODS, service.getPeriodsFor(facilityId, programId, new Date()));
  }

  @RequestMapping(value = "view-periods/{facilityId}/{programId}", method = RequestMethod.GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_IVD')")
  public ResponseEntity<OpenLmisResponse> getViewPeriods(@PathVariable Long facilityId, @PathVariable Long programId) {
    return OpenLmisResponse.response(PERIODS, service.getReportedPeriodsFor(facilityId, programId));
  }

  @RequestMapping(value = "initialize/{facilityId}/{programId}/{periodId}")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_IVD')")
  public ResponseEntity<OpenLmisResponse> initialize(
          @PathVariable Long facilityId,
          @PathVariable Long programId,
          @PathVariable Long periodId,
          HttpServletRequest request
  ) {
    return OpenLmisResponse.response(REPORT, service.initialize(facilityId, programId, periodId, loggedInUserId(request)));
  }

  @RequestMapping(value = "get/{id}.json", method = RequestMethod.GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_IVD, VIEW_IVD')")
  public ResponseEntity<OpenLmisResponse> getReport(@PathVariable Long id) {
    return OpenLmisResponse.response(REPORT, service.getById(id));
  }

  @RequestMapping(value = "save")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_IVD')")
  public ResponseEntity<OpenLmisResponse> save(@RequestBody VaccineReport report, HttpServletRequest request) {
    service.save(report, loggedInUserId(request));
    return OpenLmisResponse.response(REPORT, report);
  }

  @RequestMapping(value = "submit")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_IVD')")
  public ResponseEntity<OpenLmisResponse> submit(@RequestBody VaccineReport report, HttpServletRequest request) {
    service.submit(report, loggedInUserId(request));
    return OpenLmisResponse.response(REPORT, report);
  }

  @RequestMapping(value = "vaccine-monthly-report")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_IVD')")
  public ResponseEntity<OpenLmisResponse> getVaccineMonthlyReport(@RequestParam("facility") Long facilityId, @RequestParam("period") Long periodId, @RequestParam("zone") Long zoneId) {

    if (periodId == null || periodId == 0) return null;

    return OpenLmisResponse.response("vaccineData", service.getMonthlyVaccineReport(facilityId, periodId, zoneId));

  }

  @RequestMapping(value = "vaccine-usage-trend")
  public ResponseEntity<OpenLmisResponse> vaccineUsageTrend(@RequestParam("facilityCode") String facilityCode, @RequestParam("productCode") String productCode, @RequestParam("period") Long periodId, @RequestParam("zone") Long zoneId) {

    return OpenLmisResponse.response("vaccineUsageTrend", service.vaccineUsageTrend(facilityCode, productCode, periodId, zoneId));
  }

  @RequestMapping(value = "/orderRequisition/downloadPDF", method = RequestMethod.GET)
  public ModelAndView downloadPDF() {

    List<OrderRequisitionDTO> listOrders = new ArrayList<OrderRequisitionDTO>();

    return new ModelAndView("orderRequisitionPDF", "listOrders", listOrders);
  }


}
