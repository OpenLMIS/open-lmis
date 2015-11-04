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

package org.openlmis.report.controller;

import lombok.NoArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProcessingScheduleService;
import org.openlmis.core.service.SupervisoryNodeService;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.equipment.domain.Donor;
import org.openlmis.equipment.domain.Equipment;
import org.openlmis.report.model.dto.*;
import org.openlmis.report.model.report.OrderFillRateSummaryReport;
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
import java.util.Date;
import java.util.HashMap;
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
  public static final String PROGRAMS = "programs";
  public static final String SCHEDULES = "schedules";
  public static final String FACILITY_TYPES = "facilityTypes";
  public static final String FACILITY_LEVELS = "facilityLevels";
  public static final String REGIMEN_CATEGORIES = "regimenCategories";
  public static final String GEOGRAPHIC_LEVELS = "geographicLevels";
  public static final String REGIMENS = "regimens";
  public static final String ZONES = "zones";
  public static final String ZONE = "zone";
  public static final String PRODUCT_FORMS = "productForms";
  public static final String DOSAGE_UNITS = "dosageUnits";
  public static final String PRODUCT_GROUPS = "productGroups";
  public static final String ALL_FACILITIES = "allFacilities";
  public static final String FACILITIES = "facilities";
  public static final String PERIODS = "periods";
  public static final String SUPERVISORY_NODES = "supervisoryNodes";
  public static final String USER_ROLE_ASSIGNMENTS = "userRoleAssignments";
  public static final String USER_ROLE_ASSIGNMENT_SUMMARY = "userRoleAssignmentSummary";
  public static final String EQUIPMENT_TYPES = "equipmentTypes";
  public static final String EQUIPMENTS = "equipments";
  public static final String PRODUCT_CATEGORY_TREE = "productCategoryTree";
  public static final String YEAR_SCHEDULE_PERIOD = "yearSchedulePeriod";
  public static final String VACCINE_PERIODS = "vaccinePeriods";
  public static final String DONORS = "donors";
  public static final String TIMELINESS_DATA = "timelinessData";
  public static final String TIMELINESS_STATUS_DATA = "timelinessStatusData";
  public static final String REPORTING_DATES = "reportingDates";
  public static final String LAST_PERIODS = "lastPeriods";

  @Autowired
  private ReportLookupService reportLookupService;
  @Autowired
  private FacilityService facilityService;

  @Autowired
  private ProcessingScheduleService processingScheduleService;

  @Autowired
  private SupervisoryNodeService supervisoryNodeService;

  @RequestMapping(value="/programs", method = GET, headers = BaseController.ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getPrograms(){
    return OpenLmisResponse.response(PROGRAMS, this.reportLookupService.getAllPrograms() );
  }

  @RequestMapping(value="/user-programs", method = GET, headers = BaseController.ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getPrograms(HttpServletRequest request){
    return OpenLmisResponse.response(PROGRAMS, this.reportLookupService.getAllPrograms(loggedInUserId(request)) );
  }

  //It Get only programs with regimens
  @RequestMapping(value="/programs-supporting-regimen", method = GET, headers = BaseController.ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getRegimenPrograms(){
      return OpenLmisResponse.response(PROGRAMS, this.reportLookupService.getAllRegimenPrograms());
  }
  @RequestMapping(value = "/programs-supporting-budget", method = GET,headers = BaseController.ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse>getProgramsWithBudgetingApplies(){
      return OpenLmisResponse.response(PROGRAMS,this.reportLookupService.getAllProgramsWithBudgeting());
  }
  @RequestMapping(value="/schedules", method = GET, headers = BaseController.ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getSchedules(){
      return OpenLmisResponse.response(SCHEDULES, this.reportLookupService.getAllSchedules() ) ;
  }

  @RequestMapping(value="/schedules-by-program", method = GET, headers = BaseController.ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getSchedulesByProgram(@RequestParam(value = "program", required = true, defaultValue = "0") long program ){
    return OpenLmisResponse.response(SCHEDULES, this.reportLookupService.getSchedulesByProgram(program) ) ;
  }

  @RequestMapping(value="/facilityTypes", method = GET, headers = BaseController.ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getFacilityTypes(){
      return OpenLmisResponse.response(FACILITY_TYPES, this.reportLookupService.getFacilityTypes() ) ;
  }

  @RequestMapping(value="/facilityTypesForProgram", method = GET, headers = BaseController.ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getFacilityTypesForProgram(@RequestParam("program") Long programId){
    return OpenLmisResponse.response(FACILITY_TYPES, this.reportLookupService.getFacilityTypesForProgram(programId)) ;
  }

  @RequestMapping(value="/facility-levels", method = GET, headers = BaseController.ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getFacilityLevels(@RequestParam("program") Long programId,
                                                            HttpServletRequest request){
    return OpenLmisResponse.response(FACILITY_LEVELS, this.reportLookupService.getFacilityLevels(programId,
            loggedInUserId(request))) ;
  }

  @RequestMapping(value="/regimenCategories", method = GET, headers = BaseController.ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getAllRegimenCategory(){
      return OpenLmisResponse.response(REGIMEN_CATEGORIES, this.reportLookupService.getAllRegimenCategory() ) ;
  }

  @RequestMapping(value="/geographicLevels", method = GET, headers = BaseController.ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getAllGeographicLevels(){
      return OpenLmisResponse.response(GEOGRAPHIC_LEVELS, this.reportLookupService.getAllGeographicLevels() ) ;
  }


  @RequestMapping(value="/products.json", method = GET, headers = BaseController.ACCEPT_JSON)
  public List<Product> getProducts(){
        return this.reportLookupService.getAllProducts();
  }

  @RequestMapping(value = "/regiments", method = GET, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getAllRegimens() {
      return OpenLmisResponse.response(REGIMENS, this.reportLookupService.getAllRegimens());
  }

  @RequestMapping(value = "/regimenCategories/{regimenCategoryId}/regimens", method = GET, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getRegimensByCategory(@PathVariable("regimenCategoryId") Long regimenCategoryId) {

      List<Regimen> regimenList = reportLookupService.getRegimenByCategory(regimenCategoryId);
      return OpenLmisResponse.response(REGIMENS, regimenList);
  }

  @RequestMapping(value = "/geographicLevels/{geographicLevelId}/zones", method = GET, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getGeographicZones(@PathVariable("geographicLevelId") Long geographicLevelId) {
    List<GeographicZone> geographicZoneList =  reportLookupService.getGeographicLevelById(geographicLevelId);

      return OpenLmisResponse.response(ZONES, geographicZoneList);
  }

  @RequestMapping(value = "/geographic-zones/flat", method = GET, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getFlatGeographicZones() {
    List<FlatGeographicZone> geographicZoneList =  reportLookupService.getFlatGeographicZoneList();

    return OpenLmisResponse.response(ZONES, geographicZoneList);
  }

  @RequestMapping(value = "/geographic-zones/tree", method = GET, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getGeographicZoneTree(  HttpServletRequest request) {
    GeoZoneTree geoZoneTree =  reportLookupService.getGeoZoneTree(loggedInUserId(request));

    return OpenLmisResponse.response(ZONE, geoZoneTree);
  }

  @RequestMapping(value = "/geographic-zones/tree-program", method = GET, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getGeographicZoneTreeByProgram( @RequestParam(value = "program", required = true, defaultValue = "0") long program,  HttpServletRequest request) {
    GeoZoneTree geoZoneTree =  reportLookupService.getGeoZoneTree(loggedInUserId(request), program);

    return OpenLmisResponse.response(ZONE, geoZoneTree);
  }

  @RequestMapping(value = "/supervisory-node/user-unassigned-node", method = GET, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getTotalUnassignedSupervisoryNode( @RequestParam(value = "program", required = true, defaultValue = "0") long program,  HttpServletRequest request) {
    Long unassignedSupervisoryNodes =  supervisoryNodeService.getTotalUnassignedSupervisoryNodeOfUserBy(loggedInUserId(request), program);

        return OpenLmisResponse.response("supervisory_nodes", unassignedSupervisoryNodes);
    }

  @RequestMapping(value="/program-products/{programId}.json", method = GET, headers = BaseController.ACCEPT_JSON)
  public List<Product> getProgramProducts( @PathVariable("programId") Long programId){
    return this.reportLookupService.getProductsActiveUnderProgram(programId);
  }

  @RequestMapping(value = "/push-program/products", method = GET, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getPushProgramProducts() {
    return OpenLmisResponse.response("products", reportLookupService.getPushProgramProducts());
  }

  @RequestMapping(value="/products_by_category", method = GET, headers = BaseController.ACCEPT_JSON)
  public List<Product> getProductsByCategory(@RequestParam(value = "category", required = true, defaultValue = "0") int category, @RequestParam(value = "program", required = true, defaultValue = "0") int programId){
      return this.reportLookupService.getProductListByCategory(programId, category);
  }

  @RequestMapping(value="/rgroups", method = GET, headers = BaseController.ACCEPT_JSON)
  public List<RequisitionGroup> getRequisitionGroups(){
      return this.reportLookupService.getAllRequisitionGroups();
  }

  @RequestMapping(value="/reporting_groups_by_program", method = GET, headers = BaseController.ACCEPT_JSON)
  public List<RequisitionGroup> getRequisitionGroupsByProgram(
      @RequestParam(value =  "program", required = true, defaultValue = "1") int program
  ){
    return this.reportLookupService.getRequisitionGroupsByProgram(program);
  }

  @RequestMapping(value="/reporting_groups_by_program_schedule", method = GET, headers = BaseController.ACCEPT_JSON)
  public List<RequisitionGroup> getRequisitionGroupsByProgramSchedule(
          @RequestParam(value = "program", required = true, defaultValue = "1") int program,
          @RequestParam(value = "schedule", required = true, defaultValue = "10") int schedule
  ){
      return this.reportLookupService.getRequisitionGroupsByProgramAndSchedule(program, schedule);
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
  public ResponseEntity<OpenLmisResponse> getAllGeographicZones() {
      return OpenLmisResponse.response(ZONES, reportLookupService.getAllZones());
  }

  @RequestMapping(value = "/productForms", method = GET, headers = BaseController.ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getAllProductForms() {
      return OpenLmisResponse.response(PRODUCT_FORMS, reportLookupService.getAllProductForm());
  }

  @RequestMapping(value = "/dosageUnits", method = GET, headers = BaseController.ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getDosageUnits() {
      return OpenLmisResponse.response(DOSAGE_UNITS, reportLookupService.getDosageUnits());
  }

  @RequestMapping(value = "/productGroups", method = GET, headers = BaseController.ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getAllProductGroups() {
      return OpenLmisResponse.response(PRODUCT_GROUPS, reportLookupService.getAllProductGroups());
  }

  @RequestMapping(value = "/allFacilities", method = GET, headers = BaseController.ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getAllFacilities() {
      return OpenLmisResponse.response(ALL_FACILITIES, reportLookupService.getAllFacilities(new RowBounds(RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT)));
  }

  @RequestMapping(value = "/facilities", method = GET, headers = BaseController.ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getFacilities(
      @RequestParam("program") Long program,
      @RequestParam("schedule") Long schedule,
      @RequestParam(value = "type", defaultValue = "0L", required = false) Long type,
      @RequestParam(value = "requisitionGroup", defaultValue = "0L", required = false) Long requisitionGroup,
      @RequestParam(value = ZONE, defaultValue = "0L", required = false) Long zone,
      HttpServletRequest request

  ) {
    // set default for optional parameters
    // turns out spring's optional parameter and default config is not cutting it.
    type = (type != null)? type: 0L;
    requisitionGroup = (requisitionGroup != null)?requisitionGroup: 0L;

    return OpenLmisResponse.response(FACILITIES, reportLookupService.getFacilities(program, schedule, type, requisitionGroup, zone, loggedInUserId(request)));
  }
    @RequestMapping(value = "/facilitiesByType/{facilityTypeId}.json", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getFacilitiesByFacilityType(

            @PathVariable("facilityTypeId") Long type,

            HttpServletRequest request

    ) {
        // set default for optional parameters
        // turns out spring's optional parameter and default config is not cutting it.
        type = (type != null)? type: 0L;


        return OpenLmisResponse.response(FACILITIES, reportLookupService.getFacilities(  type ));
    }
    @RequestMapping(value = "/facilitiesByType", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getProgramFacilitiesList(

                      HttpServletRequest request

    ) {

        long userId=this.loggedInUserId(request);

        return OpenLmisResponse.response(FACILITIES, reportLookupService.getFacilities(  request.getParameterMap(),userId ));
    }
  @RequestMapping(value = "/user/facilities", method = GET, headers = BaseController.ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getSupervisedFacilities(
          HttpServletRequest request
  ) {
            return OpenLmisResponse.response(FACILITIES, facilityService.getForUserAndRights(loggedInUserId(request), "VIEW_REQUISITION"));
  }



    @RequestMapping(value = "/geographic-zone/facilities", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getFacilities(
            @RequestParam("zoneId") Long zoneId,
            @RequestParam("programId") Long programId,
            HttpServletRequest request
    ) {
        return OpenLmisResponse.response(FACILITIES, reportLookupService.getFacilityByGeographicZoneTree(loggedInUserId(request), zoneId, programId));
    }

    @RequestMapping(value = "/geographic-zone/{geoId}/facilities", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getFacilities(
            @PathVariable("geoId") Long zoneId,
            HttpServletRequest request
    ) {
        return OpenLmisResponse.response(FACILITIES, reportLookupService.getFacilityByGeographicZone(loggedInUserId(request), zoneId));
    }

    @RequestMapping(value = "notification/facilities", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getFacilitiesForNotifications(
            @RequestParam("zoneId") Long zoneId,
            HttpServletRequest request
    ) {
        return OpenLmisResponse.response(FACILITIES, reportLookupService.getFacilitiesForNotifications(loggedInUserId(request), zoneId));
    }

  @RequestMapping(value = "/user/geographic-zones/tree", method = GET, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getUserGeographicZoneTree(@RequestParam("programId") Long programId, HttpServletRequest request) {
          GeoZoneTree geoZoneTree =  reportLookupService.getGeoZoneTree(loggedInUserId(request), programId);

      return OpenLmisResponse.response(ZONE, geoZoneTree);
  }

  @RequestMapping(value = "/schedules/{scheduleId}/periods", method = GET, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getAll(@PathVariable("scheduleId") Long scheduleId) {
    List<ProcessingPeriod> periodList = processingScheduleService.getAllPeriods(scheduleId);
    return OpenLmisResponse.response(PERIODS, periodList);
  }

  @RequestMapping(value = "/schedules/{scheduleId}/year/{year}/periods", method = GET, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getPeriodsByScheduleAndYear(@PathVariable("scheduleId") Long scheduleId, @PathVariable("year") Long year) {
      List<ProcessingPeriod> periodList = processingScheduleService.getAllPeriodsForScheduleAndYear(scheduleId, year);
      return OpenLmisResponse.response(PERIODS, periodList);
  }

  @RequestMapping(value = "/allPeriods", method = GET, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getAllPeriods() {
      List<org.openlmis.report.model.dto.ProcessingPeriod> periodList = reportLookupService.getAllProcessingPeriods();
      return OpenLmisResponse.response(PERIODS, periodList);
  }

  @RequestMapping(value = "/periods", method = GET, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getFilteredPeriods(HttpServletRequest request) {

      Date startDate = InteractiveReportPeriodFilterParser.getStartDateFilterValue(request.getParameterMap());
      Date endDate = InteractiveReportPeriodFilterParser.getEndDateFilterValue(request.getParameterMap());

      List<org.openlmis.report.model.dto.ProcessingPeriod> periodList = reportLookupService.getFilteredPeriods(startDate, endDate);

      return OpenLmisResponse.response(PERIODS, periodList);
  }

  @RequestMapping(value = "/user/programs", method = GET, headers = BaseController.ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getAllUserSupervisedActivePrograms(HttpServletRequest request){

      List<Program> programList = reportLookupService.getAllUserSupervisedActivePrograms(loggedInUserId(request));
      return OpenLmisResponse.response(PROGRAMS,programList);
  }

  @RequestMapping(value = "/users/{userId}/programs", method = GET, headers = BaseController.ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getAllSupervisedActiveProgramsForUser(@PathVariable("userId") Long userId){

    List<Program> programList = reportLookupService.getAllUserSupervisedActivePrograms(userId);
    return OpenLmisResponse.response(PROGRAMS,programList);
 }
  @RequestMapping(value = "/supervisory-node/{supervisoryNodeId}/programs", method = GET, headers = BaseController.ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getProgramsForSupervisoryNode(@PathVariable("supervisoryNodeId") Long supervisoryNodeId, HttpServletRequest request){

      List<Program> programList = reportLookupService.getUserSupervisedActiveProgramsBySupervisoryNode(loggedInUserId(request), supervisoryNodeId);
      return OpenLmisResponse.response(PROGRAMS,programList);
  }

  @RequestMapping(value = "/user/supervisory-nodes", method = GET, headers = BaseController.ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getUserSupervisoryNodes(HttpServletRequest request){
      List<SupervisoryNode> supervisoryNodes = reportLookupService.getAllSupervisoryNodesByUserHavingActiveProgram(loggedInUserId(request));
      return OpenLmisResponse.response(SUPERVISORY_NODES,supervisoryNodes);
  }

  @RequestMapping(value = "/roles/{roleId}/program/{programId}/supevisoryNode/{supervisoryNodeId}", method = GET, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse>  getUserRoleAssignments(@PathVariable("roleId") Long roleId,
                                                            @PathVariable("programId") Long programId,
                                                            @PathVariable("supervisoryNodeId") Long supervisoryNodeId){
      List<UserRoleAssignmentsReport> userRoleAssignments = reportLookupService.getAllRolesBySupervisoryNodeHavingProgram(roleId, programId, supervisoryNodeId);

      return OpenLmisResponse.response(USER_ROLE_ASSIGNMENTS, userRoleAssignments);
  }

  @RequestMapping(value = "UserRoleAssignments/getUserRoleAssignments", method = GET, headers = BaseController.ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getUserRoleAssignments(HttpServletRequest request){
      List<UserRoleAssignmentsReport> userSummaryList = reportLookupService.getUserRoleAssignments(request.getParameterMap());
      return OpenLmisResponse.response(USER_ROLE_ASSIGNMENT_SUMMARY,userSummaryList);
  }

    @RequestMapping(value="/equipmentTypes", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getEquipmentType() {
        List<EquipmentType> equipmentTypeList =  reportLookupService.getEquipmentTypes();

        return OpenLmisResponse.response(EQUIPMENT_TYPES, equipmentTypeList);
    }

    @RequestMapping(value = "/equipmentsByType/{equipmentType}", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getEquipmentByType(  @PathVariable("equipmentType") Long equipmentType
                                                                 ) {
        List<Equipment> equipments = reportLookupService.getEquipmentsByType(equipmentType);
        return OpenLmisResponse.response(EQUIPMENTS, equipments);
    }

    @RequestMapping(value="/productProgramCategoryTree/{programId}", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getProductCategoryProductByProgramId(@PathVariable("programId") int programId){

        List<ProductCategoryProductTree> categoryProductTree = reportLookupService.getProductCategoryProductByProgramId(programId);

        return OpenLmisResponse.response(PRODUCT_CATEGORY_TREE, categoryProductTree);
    }

    @RequestMapping(value="/yearSchedulePeriod", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getScheduleYearPeriod(){

        List<YearSchedulePeriodTree> yearSchedulePeriodTree = reportLookupService.getYearSchedulePeriodTree();

        return OpenLmisResponse.response(YEAR_SCHEDULE_PERIOD, yearSchedulePeriodTree);
    }

    @RequestMapping(value = "/vaccineYearSchedulePeriod", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getVaccineScheduleYearPeriod() {

        List<YearSchedulePeriodTree> yearSchedulePeriodTree = reportLookupService.getVaccineYearSchedulePeriodTree();

        Long currentPeriodId = reportLookupService.getCurrentPeriodIdForVaccine();
        Map<String, Object> data = new HashMap<>(2);
        data.put("currentPeriodId", currentPeriodId);
        data.put(PERIODS, yearSchedulePeriodTree);

        return OpenLmisResponse.response(VACCINE_PERIODS, data);
    }

    @RequestMapping(value = "/OrderFillRateSummary/program/{programId}/period/{periodId}/schedule/{scheduleId}/facilityTypeId/{facilityTypeId}/zone/{zoneId}/status/{status}/orderFillRateSummary", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getOrderFillRateSummaryData(@PathVariable("programId") Long programId,
                                                                        @PathVariable("periodId") Long periodId,
                                                                        @PathVariable("scheduleId") Long scheduleId,
                                                                        @PathVariable("facilityTypeId") Long facilityTypeId,
                                                                        @PathVariable("zoneId") Long zoneId,
                                                                        @PathVariable("status") String status,
                                                                        HttpServletRequest request) {
        List<OrderFillRateSummaryReport> orderFillRateReportSummaryList = reportLookupService.getOrderFillRateSummary(programId, periodId, scheduleId, facilityTypeId, loggedInUserId(request), zoneId, status);
        return OpenLmisResponse.response("orderFillRateSummary", orderFillRateReportSummaryList);
    }

    @RequestMapping(value="/donors", method = GET, headers = BaseController.ACCEPT_JSON)

    public ResponseEntity<OpenLmisResponse> getDonorsList(){
        List<Donor> donors = reportLookupService.getAllDonors();
        return OpenLmisResponse.response(DONORS, donors);
    }

    @RequestMapping(value = "/user/supervised/facilities", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getUserSupervisedFacilities(
            HttpServletRequest request
    ) {
       return OpenLmisResponse.response(FACILITIES, facilityService.getUserSupervisedFacilities(loggedInUserId(request)));
    }


    @RequestMapping(value = "/timelinessStatusData/timelinessData", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getTimelinessStatusData(
            @RequestParam("programId") Long programId,
            @RequestParam("periodId") Long periodId,
            @RequestParam("scheduleId") Long scheduleId,
            @RequestParam("zoneId") Long zoneId,
            @RequestParam("status") String status,
            HttpServletRequest request
    ) {
        return OpenLmisResponse.response(TIMELINESS_DATA, reportLookupService.getTimelinessStatusData(programId, periodId, scheduleId, zoneId, status));
    }



    @RequestMapping(value = "/timelinessStatusData/getFacilityRnRStatusData", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getFacilityTimelinessData(
            @RequestParam("programId") Long programId,
            @RequestParam("periodId") Long periodId,
            @RequestParam("scheduleId") Long scheduleId,
            @RequestParam("zoneId") Long zoneId,
            @RequestParam("status") String status,
            @RequestParam("facilityIds") String facilityIds,

            HttpServletRequest request
    ) {
        return OpenLmisResponse.response(TIMELINESS_STATUS_DATA, reportLookupService.getFacilityRnRStatusData(programId, periodId, scheduleId, zoneId, status, facilityIds));
    }

    @RequestMapping(value = "/reportingDates/getTimelinessReportingDates", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getTimelinessReportingDates(
            @RequestParam("periodId") Long periodId,
            HttpServletRequest request
    ) {
        return OpenLmisResponse.response(REPORTING_DATES, reportLookupService.getTimelinessReportingDates(periodId));
    }

    @RequestMapping(value="/rmnch-products.json", method = GET, headers = BaseController.ACCEPT_JSON)
    public List<Product> getRmnchProducts(){
        return this.reportLookupService.getRmnchProducts();
    }

    @RequestMapping(value = "/last-periods.json", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getLastPeriods(@RequestParam("programId") Long programId){
        return OpenLmisResponse.response(LAST_PERIODS, this.reportLookupService.getLastPeriods(programId));
    }

    @RequestMapping(value = "/facility-By-level", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getFacilityByLevel(@RequestParam("program") Long programId,
                                                               HttpServletRequest request) {
        List<FacilityLevelTree> facilityLevelTrees = reportLookupService.getFacilityByLevel(programId, loggedInUserId(request));
        return OpenLmisResponse.response(FACILITY_LEVELS, facilityLevelTrees);

    }

}
