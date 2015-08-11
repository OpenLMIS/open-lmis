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

import org.openlmis.core.domain.RightName;
import org.openlmis.core.domain.User;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.core.service.UserService;
import org.openlmis.vaccine.domain.reports.VaccineReport;
import org.openlmis.vaccine.service.reports.VaccineReportService;
import org.openlmis.web.controller.BaseController;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/vaccine/report/")
public class VaccineReportController extends BaseController {

  @Autowired
  VaccineReportService service;

  @Autowired
  ProgramService programService;

  @Autowired
  UserService userService;

  @Autowired
  FacilityService facilityService;

  @RequestMapping(value = "programs")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_IVD_SETTINGS')")
  public ResponseEntity<OpenLmisResponse> getProgramsForConfiguration(){
    return OpenLmisResponse.response("programs", programService.getAllIvdPrograms() );
  }

  @RequestMapping(value = "ivd-form/programs")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_REQUISITION, AUTHORIZE_REQUISITION')")
  public ResponseEntity<OpenLmisResponse> getProgramForIvdFormHomeFacility(HttpServletRequest request){
    Long userId = loggedInUserId(request);
    User user = userService.getById(userId);
    return OpenLmisResponse.response("programs", programService.getIvdProgramsSupportedByUserHomeFacilityWithRights(user.getFacilityId(), userId, "CREATE_REQUISITION", "AUTHORIZE_REQUISITION") );
  }

  @RequestMapping(value = "ivd-form/supervised-programs")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_REQUISITION, AUTHORIZE_REQUISITION')")
  public ResponseEntity<OpenLmisResponse> getProgramForIvdFormSupervisedFacilities(HttpServletRequest request){
    return OpenLmisResponse.response("programs", programService.getIvdProgramForSupervisedFacilities(loggedInUserId(request), "CREATE_REQUISITION", "AUTHORIZE_REQUISITION") );
  }

  @RequestMapping(value = "ivd-form/facilities/{programId}.json", method = RequestMethod.GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_REQUISITION, AUTHORIZE_REQUISITION')")
  public ResponseEntity<OpenLmisResponse> getFacilities(@PathVariable Long programId, HttpServletRequest request){
    Long userId = loggedInUserId(request);
    //TODO: make sure this method also supports home facility.
    return OpenLmisResponse.response("facilities", facilityService.getUserSupervisedFacilities(userId, programId, RightName.CREATE_REQUISITION));
  }

  @RequestMapping(value = "periods/{facilityId}/{programId}", method = RequestMethod.GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_REQUISITION, AUTHORIZE_REQUISITION')")
  public ResponseEntity<OpenLmisResponse> getPeriods(@PathVariable Long facilityId, @PathVariable Long programId, HttpServletRequest request){
    return OpenLmisResponse.response("periods", service.getPeriodsFor(facilityId, programId, new Date()));
  }

  @RequestMapping(value = "view-periods/{facilityId}/{programId}", method = RequestMethod.GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_REQUISITION, AUTHORIZE_REQUISITION')")
  public ResponseEntity<OpenLmisResponse> getViewPeriods(@PathVariable Long facilityId, @PathVariable Long programId, HttpServletRequest request){
    return OpenLmisResponse.response("periods", service.getReportedPeriodsFor(facilityId, programId));
  }

  @RequestMapping(value = "initialize/{facilityId}/{programId}/{periodId}")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_REQUISITION, AUTHORIZE_REQUISITION')")
  public ResponseEntity<OpenLmisResponse> initialize(
    @PathVariable Long facilityId,
    @PathVariable Long programId,
    @PathVariable Long periodId,
    HttpServletRequest request
  ){
    return OpenLmisResponse.response("report", service.initialize(facilityId, programId, periodId));
  }

  @RequestMapping(value = "get/{id}.json", method = RequestMethod.GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_REQUISITION, AUTHORIZE_REQUISITION')")
  public ResponseEntity<OpenLmisResponse> getReport(@PathVariable Long id, HttpServletRequest request){
    return OpenLmisResponse.response("report", service.getById(id));
  }

  @RequestMapping(value = "save")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_REQUISITION, AUTHORIZE_REQUISITION')")
  public ResponseEntity<OpenLmisResponse> save(@RequestBody VaccineReport report, HttpServletRequest request){
    service.save(report);
    return OpenLmisResponse.response("report", report);
  }

  @RequestMapping(value = "submit")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'AUTHORIZE_REQUISITION')")
  public ResponseEntity<OpenLmisResponse> submit(@RequestBody VaccineReport report, HttpServletRequest request){
    service.submit(report);
    return OpenLmisResponse.response("report", report);
  }

  @RequestMapping(value = "vaccine-monthly-report")
  public ResponseEntity<OpenLmisResponse> getVaccineMonthlyReport(@RequestParam("facility") Long facilityId, @RequestParam("period") Long periodId, @RequestParam("zone") Long zoneId){

    if (periodId == null || periodId == 0) return null;

    Map<String, Object> data = new HashMap();

    if (facilityId == null || facilityId == 0 ){ // Return aggregated data for the selected geozone

      data.put("vaccination", service.getVaccineReport(null, facilityId, periodId, zoneId));

    } else {
      Long reportId = service.getReportIdForFacilityAndPeriod(facilityId, periodId);
      data.put("diseaseSurveillance", service.getDiseaseSurveillance(reportId));
      data.put("coldChain", service.getColdChain(reportId));
      data.put("adverseEffect", service.getAdverseEffectReport(reportId));
      data.put("vaccineCoverage", service.getVaccineCoverageReport(reportId));
      data.put("immunizationSession", service.getImmunizationSession(reportId));
      data.put("vaccination", service.getVaccineReport(reportId, facilityId, periodId, zoneId));
      data.put("syringes", service.getSyringeAndSafetyBoxReport(reportId));
      data.put("vitamins", service.getVitaminsReport(reportId));
      data.put("targetPopulation", service.getTargetPopulation(facilityId, periodId));
      data.put("vitaminSupplementation", service.getVitaminSupplementationReport(reportId));
    }

    return OpenLmisResponse.response("vaccineData", data);
  }

  @RequestMapping(value = "vaccine-usage-trend")
  public ResponseEntity<OpenLmisResponse> vaccineUsageTrend(@RequestParam("facilityCode") String facilityCode, @RequestParam("productCode") String productCode, @RequestParam("period") Long periodId, @RequestParam("zone") Long zoneId){
    return OpenLmisResponse.response("vaccineUsageTrend", service.vaccineUsageTrend(facilityCode, productCode, periodId, zoneId));
  }

}
