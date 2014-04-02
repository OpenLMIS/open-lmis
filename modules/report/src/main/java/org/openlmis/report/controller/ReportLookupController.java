/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.report.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProcessingScheduleService;
import org.openlmis.report.model.dto.*;
import org.openlmis.report.response.OpenLmisResponse;
import org.openlmis.report.service.lookup.ReportLookupService;
import org.openlmis.report.util.InteractiveReportPeriodFilterParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@NoArgsConstructor
@RequestMapping(value = "/reports")
public class ReportLookupController extends BaseController {

  public static final String USER_ID = "USER_ID";

  public static final String OPEN_LMIS_OPERATION_YEARS = "years";
  public static final String OPEN_LMIS_OPERATION_MONTHS = "months";

  private String  getCommaSeparatedIds(List<Long> idList){

      return idList == null ? "{}" : idList.toString().replace("[", "{").replace("]", "}");
  }

  @Autowired
  private ReportLookupService reportLookupService;
  @Autowired
  private FacilityService facilityService;

  @Autowired
  private ProcessingScheduleService processingScheduleService;

  @RequestMapping(value="/programs", method = GET, headers = BaseController.ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getPrograms(){
      return OpenLmisResponse.response( "programs", this.reportLookupService.getAllPrograms() );
  }
  //It Get only programs with regimens
  @RequestMapping(value="/regimenPrograms", method = GET, headers = BaseController.ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getRegimenPrograms(){
      return OpenLmisResponse.response( "regimenPrograms", this.reportLookupService.getAllRegimenPrograms());
  }


  @RequestMapping(value="/schedules", method = GET, headers = BaseController.ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getSchedules(){
      return OpenLmisResponse.response( "schedules", this.reportLookupService.getAllSchedules() ) ;
  }

  @RequestMapping(value="/facilityTypes", method = GET, headers = BaseController.ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getFacilityTypes(){
      return OpenLmisResponse.response( "facilityTypes", this.reportLookupService.getFacilityTypes() ) ;
  }

  @RequestMapping(value="/regimenCategories", method = GET, headers = BaseController.ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getAllRegimenCategory(){
      return OpenLmisResponse.response( "regimenCategories", this.reportLookupService.getAllRegimenCategory() ) ;
  }

  @RequestMapping(value="/geographicLevels", method = GET, headers = BaseController.ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getAllGeographicLevels(){
      return OpenLmisResponse.response( "geographicLevels", this.reportLookupService.getAllGeographicLevels() ) ;
  }


  @RequestMapping(value="/products.json", method = GET, headers = BaseController.ACCEPT_JSON)
  public List<Product> getProducts(){
        return this.reportLookupService.getAllProducts();
  }

  @RequestMapping(value = "/regiments", method = GET, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getAllRegimens() {
      return OpenLmisResponse.response("regimens", this.reportLookupService.getAllRegimens());
  }

  @RequestMapping(value = "/regimenCategories/{regimenCategoryId}/regimens", method = GET, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getRegimensByCategory(@PathVariable("regimenCategoryId") Long regimenCategoryId) {

      List<Regimen> regimenList = reportLookupService.getRegimenByCategory(regimenCategoryId);
      return OpenLmisResponse.response("regimens", regimenList);
  }

  @RequestMapping(value = "/geographicLevels/{geographicLevelId}/zones", method = GET, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getGeographicZones(@PathVariable("geographicLevelId") Long geographicLevelId) {
    List<GeographicZone> geographicZoneList =  reportLookupService.getGeographicLevelById(geographicLevelId);

      return OpenLmisResponse.response("zones", geographicZoneList);
  }

  @RequestMapping(value = "/geographic-zones/flat", method = GET, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getFlatGeographicZones() {
    List<FlatGeographicZone> geographicZoneList =  reportLookupService.getFlatGeographicZoneList();

    return OpenLmisResponse.response("zones", geographicZoneList);
  }

  @RequestMapping(value="/program-products/{programId}.json", method = GET, headers = BaseController.ACCEPT_JSON)
  public List<Product> getProgramProducts( @PathVariable("programId") Long programId){
    return this.reportLookupService.getProductsActiveUnderProgram(programId);
  }

  @RequestMapping(value="/products_by_category", method = GET, headers = BaseController.ACCEPT_JSON)
  public List<Product> getProductsByCategory(@RequestParam(value = "category", required = true, defaultValue = "0") int category){
      return this.reportLookupService.getProductListByCategory(category);
  }

  @RequestMapping(value="/rgroups", method = GET, headers = BaseController.ACCEPT_JSON)
  public List<RequisitionGroup> getRequisitionGroups(){
      return this.reportLookupService.getAllRequisitionGroups();
  }

  @RequestMapping(value="/reporting_groups_by_program", method = GET, headers = BaseController.ACCEPT_JSON)
  public List<RequisitionGroup> getRequisitionGroupsByProgram(
      @RequestParam(value = "program", required = true, defaultValue = "1") int program
  ){
    return this.reportLookupService.getRequisitionGroupsByProgram(program);
  }

  @RequestMapping(value="/reporting_groups_by_program_schedule", method = GET, headers = BaseController.ACCEPT_JSON)
  public List<RequisitionGroup> getRequisitionGroupsByProgramSchedule(
          @RequestParam(value = "program", required = true, defaultValue = "1") int program,
          @RequestParam(value = "schedule", required = true, defaultValue = "10") int schedule
  ){
      return this.reportLookupService.getRequisitionGroupsByProgramAndSchedule(program,schedule);
  }

  @RequestMapping(value="/reporting_groups_by_supervisory_node_program_schedule", method = GET, headers = BaseController.ACCEPT_JSON)
  public List<RequisitionGroup> getBySupervisoryNodesAndProgramAndSchedule(
         @RequestParam(value = "programId") Long programId,
         @RequestParam(value = "scheduleId") Long scheduleId,
         @RequestParam(value = "supervisoryNodeId") Long supervisoryNodeId
  ){
      List<SupervisoryNode> supervisoryNodeList = reportLookupService.getAllSupervisoryNodesByParentNodeId(supervisoryNodeId);

      List<Long> supervisoryNodeIds = null;

      if (supervisoryNodeList != null && supervisoryNodeList.size() > 0){
          supervisoryNodeIds = new ArrayList<>(supervisoryNodeList.size());
          for(SupervisoryNode node : supervisoryNodeList){
              supervisoryNodeIds.add(node.getId());
          }
      }
      return this.reportLookupService.getBySupervisoryNodesAndProgramAndSchedule(getCommaSeparatedIds(supervisoryNodeIds),programId,scheduleId);
  }


  @RequestMapping(value="/productCategories", method = GET, headers = BaseController.ACCEPT_JSON)
  public List<ProductCategory> getProductCategories(){
      return this.reportLookupService.getAllProductCategories();
  }

  @RequestMapping(value="/programs/{programId}/productCategories.json", method = GET, headers = BaseController.ACCEPT_JSON)
  public List<ProductCategory> getProductCategoriesForPrograms(@PathVariable(value = "programId") int programId){
    return this.reportLookupService.getCategoriesForProgram(programId);
  }

  @RequestMapping(value="/adjustmentTypes", method = GET, headers = BaseController.ACCEPT_JSON)
  public List<AdjustmentType> getAdjustmentTypes(){
      return this.reportLookupService.getAllAdjustmentTypes();
  }

  @RequestMapping(value = "/operationYears", method = GET, headers = BaseController.ACCEPT_JSON)
  public Map getOperationYears() {
      MultiValueMap operationPeriods = new LinkedMultiValueMap<>();
      operationPeriods.put(OPEN_LMIS_OPERATION_YEARS,reportLookupService.getOperationYears());

      return operationPeriods;
  }

  @RequestMapping(value = "/months", method = GET, headers = BaseController.ACCEPT_JSON)
  public Map getAllMonths() {
      MultiValueMap months = new LinkedMultiValueMap<>();
      months.put(OPEN_LMIS_OPERATION_MONTHS,reportLookupService.getAllMonths());

      return months;
  }

  @RequestMapping(value = "/geographicZones", method = GET, headers = BaseController.ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getAllGeographicZones(HttpServletRequest request) {
      return OpenLmisResponse.response("zones", reportLookupService.getAllZones());
  }

  @RequestMapping(value = "/productForms", method = GET, headers = BaseController.ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getAllProductForms(HttpServletRequest request) {
      return OpenLmisResponse.response("productForms", reportLookupService.getAllProductForm());
  }

  @RequestMapping(value = "/dosageUnits", method = GET, headers = BaseController.ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getDosageUnits(HttpServletRequest request) {
      return OpenLmisResponse.response("dosageUnits", reportLookupService.getDosageUnits());
  }

  @RequestMapping(value = "/productGroups", method = GET, headers = BaseController.ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getAllProductGroups(HttpServletRequest request) {
      return OpenLmisResponse.response("productGroups", reportLookupService.getAllProductGroups());
  }

  @RequestMapping(value = "/allFacilities", method = GET, headers = BaseController.ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getAllFacilities(HttpServletRequest request) {
      return OpenLmisResponse.response("allFacilities", reportLookupService.getAllFacilities());
  }

  @RequestMapping(value = "/facilities", method = GET, headers = BaseController.ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getFacilities(
      @RequestParam("program") Long program,
      @RequestParam("schedule") Long schedule,
      @RequestParam(value = "type", defaultValue = "0" , required = false) Long type,
      @RequestParam(value = "requisitionGroup", defaultValue = "0", required = false) Long requisitionGroup
  ) {
    // set default for optional parameters
    // turns out spring's optional parameter and default config is not cutting it.
    type = (type != null)? type: 0L;
    requisitionGroup = (requisitionGroup != null)?requisitionGroup: 0L;

    return OpenLmisResponse.response("facilities", reportLookupService.getFacilities( program, schedule, type, requisitionGroup ));
  }

/*  @RequestMapping(value = "/facilities/geographicZone/{geographicZoneId}/requisitionGroup/{rgroupId}/program/{programId}/schedule/{scheduleId}", method = GET, headers = BaseController.ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getFacilities(
          @PathVariable("geographicZoneId") Long geographicZoneId,
          @PathVariable("rgroupId") Long requisitionGroupId,
          @PathVariable("programId") Long programId,
          @PathVariable("scheduleId") Long scheduleId,
          HttpServletRequest request
  ) {
      return OpenLmisResponse.response("facilities", reportLookupService.getFacilitiesBy(geographicZoneId,requisitionGroupId,programId,scheduleId));
  }*/
@RequestMapping(value = "/user/facilities", method = GET, headers = BaseController.ACCEPT_JSON)
public ResponseEntity<OpenLmisResponse> getSupervisedFacilities(
        HttpServletRequest request
) {
    return OpenLmisResponse.response("facilities", facilityService.getForUserAndRights(loggedInUserId(request),Right.VIEW_REQUISITION));
}



    @RequestMapping(value = "/facilities/program/{programId}/schedule/{scheduleId}", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getFacilities(
            @PathVariable("programId") Long programId,
            @PathVariable("scheduleId") Long scheduleId,
            @RequestParam("rgroupId") List<Long> requisitionGroupId
    ) {
        return OpenLmisResponse.response("facilities", reportLookupService.getFacilitiesBy(getCommaSeparatedIds(requisitionGroupId),programId,scheduleId));
    }

  @RequestMapping(value = "/schedules/{scheduleId}/periods", method = GET, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getAll(@PathVariable("scheduleId") Long scheduleId) {
    List<ProcessingPeriod> periodList = processingScheduleService.getAllPeriods(scheduleId);
    return OpenLmisResponse.response("periods", periodList);
  }

  @RequestMapping(value = "/schedules/{scheduleId}/year/{year}/periods", method = GET, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getPeriodsByScheduleAndYear(@PathVariable("scheduleId") Long scheduleId, @PathVariable("year") Long year) {
      List<ProcessingPeriod> periodList = processingScheduleService.getAllPeriodsForScheduleAndYear(scheduleId,year);
      return OpenLmisResponse.response("periods", periodList);
  }

  @RequestMapping(value = "/allPeriods", method = GET, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getAllPeriods(HttpServletRequest request) {
      List<org.openlmis.report.model.dto.ProcessingPeriod> periodList = reportLookupService.getAllProcessingPeriods();
      return OpenLmisResponse.response("periods", periodList);
  }

  @RequestMapping(value = "/periods", method = GET, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getFilteredPeriods(HttpServletRequest request) {

      Date startDate = InteractiveReportPeriodFilterParser.getStartDateFilterValue(request.getParameterMap());
      Date endDate = InteractiveReportPeriodFilterParser.getEndDateFilterValue(request.getParameterMap());

      List<org.openlmis.report.model.dto.ProcessingPeriod> periodList = reportLookupService.getFilteredPeriods(startDate,endDate);

      return OpenLmisResponse.response("periods", periodList);
  }

  @RequestMapping(value = "/supervisory-nodes/programs", method = GET, headers = BaseController.ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getProgramsForAllSupervisoryNodes(HttpServletRequest request){

      List<Program> programList = reportLookupService.getAllUserSupervisedActivePrograms(loggedInUserId(request));
      return OpenLmisResponse.response("programs",programList);
  }
  @RequestMapping(value = "/supervisory-node/{supervisoryNodeId}/programs", method = GET, headers = BaseController.ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getProgramsForSupervisoryNode(@PathVariable("supervisoryNodeId") Long supervisoryNodeId, HttpServletRequest request){

      List<Program> programList = reportLookupService.getUserSupervisedActiveProgramsBySupervisoryNode(loggedInUserId(request), supervisoryNodeId);
      return OpenLmisResponse.response("programs",programList);
  }
  /*@RequestMapping(value = "/user/default-supervisory-node", method = GET, headers = BaseController.ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getUserDefaultSupervisoryNode(HttpServletRequest request){
      List<SupervisoryNode> defaultSupervisoryNode = reportLookupService.getAllSupervisoryNodesByUserHavingActiveProgram(loggedInUserId(request));
      if (defaultSupervisoryNode != null && defaultSupervisoryNode.size() > 0){
          return OpenLmisResponse.response("supervisoryNode",defaultSupervisoryNode.get(0));
      }
      return OpenLmisResponse.response("supervisoryNode",null);
  }*/

  @RequestMapping(value = "/user/supervisory-nodes", method = GET, headers = BaseController.ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getUserSupervisoryNodes(HttpServletRequest request){
      List<SupervisoryNode> supervisoryNodes = reportLookupService.getAllSupervisoryNodesByUserHavingActiveProgram(loggedInUserId(request));
      return OpenLmisResponse.response("supervisoryNodes",supervisoryNodes);
  }
}
