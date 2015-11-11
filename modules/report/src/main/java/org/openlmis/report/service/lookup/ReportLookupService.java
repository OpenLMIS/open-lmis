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

package org.openlmis.report.service.lookup;

import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.core.domain.*;
import org.openlmis.core.domain.GeographicLevel;
import org.openlmis.core.repository.RegimenRepository;
import org.openlmis.core.repository.helper.CommaSeparator;
import org.openlmis.core.repository.mapper.FacilityApprovedProductMapper;
import org.openlmis.core.repository.mapper.ProcessingScheduleMapper;
import org.openlmis.core.repository.mapper.ProgramProductMapper;
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.RequisitionGroupService;
import org.openlmis.core.service.SupervisoryNodeService;
import org.openlmis.equipment.domain.Donor;
import org.openlmis.equipment.domain.Equipment;
import org.openlmis.equipment.repository.DonorRepository;
import org.openlmis.report.mapper.ReportRequisitionMapper;
import org.openlmis.report.mapper.lookup.*;
import org.openlmis.report.model.dto.*;
import org.openlmis.report.model.dto.DosageUnit;
import org.openlmis.report.model.dto.Facility;
import org.openlmis.report.model.dto.FacilityType;
import org.openlmis.report.model.dto.GeographicZone;
import org.openlmis.report.model.dto.ProcessingPeriod;
import org.openlmis.report.model.dto.Product;
import org.openlmis.report.model.dto.ProductCategory;
import org.openlmis.report.model.dto.Program;
import org.openlmis.report.model.dto.Regimen;
import org.openlmis.report.model.dto.RegimenCategory;
import org.openlmis.report.model.dto.RequisitionGroup;
import org.openlmis.report.model.params.UserSummaryParams;
import org.openlmis.report.model.report.OrderFillRateSummaryReport;
import org.openlmis.report.model.report.TimelinessReport;
import org.openlmis.report.util.Constants;
import org.openlmis.report.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.openlmis.core.domain.RightName.MANAGE_EQUIPMENT_INVENTORY;

@Service
@NoArgsConstructor
public class ReportLookupService {
    @Autowired
    private TimelinessStatusReportMapper timelinessStatusReportMapper;
    @Autowired
    private OrderFillRateSummaryListMapper orderFillRateSummaryListMapper;
    @Autowired
    private UserSummaryExReportMapper userSummaryExReportMapper;

    @Autowired
    private RegimenReportMapper regimenReportMapper;

    @Autowired
    private ProductReportMapper productMapper;

    @Autowired
    private RequisitionGroupReportMapper rgMapper;

    @Autowired
    private ProductCategoryReportMapper productCategoryMapper;

    @Autowired
    private AdjustmentTypeReportMapper adjustmentTypeReportMapper;

    @Autowired
    private ConfigurationSettingService configurationService;

    @Autowired
    private ScheduleReportMapper scheduleMapper;

    @Autowired
    private ProgramReportMapper programMapper;

    @Autowired
    private FacilityTypeReportMapper facilityTypeMapper;

    @Autowired
    private GeographicZoneReportMapper geographicZoneMapper;

    @Autowired
    private GeographicLevelReportMapper geographicLevelMapper;

    @Autowired
    private DosageUnitReportMapper dosageUnitMapper;

    @Autowired
    private FacilityLookupReportMapper facilityReportMapper;

    @Autowired
    private ProcessingPeriodReportMapper processingPeriodMapper;

    @Autowired
    private ProcessingScheduleMapper processingScheduleMapper;

    @Autowired
    private ProductGroupReportMapper productGroupReportMapper;

    @Autowired
    private ProductFormReportMapper productFormReportMapper;

    @Autowired
    private RegimenCategoryReportMapper regimenCategoryReportMapper;

    @Autowired
    private ReportRequisitionMapper requisitionMapper;

    @Autowired
    private SupervisoryNodeReportMapper supervisoryNodeReportMapper;

    @Autowired
    private ProgramProductMapper programProductMapper;

    @Autowired
    private FacilityApprovedProductMapper facilityApprovedProductMapper;

    @Autowired
    private EquipmentTypeReportMapper equipmentTypeReportMapper;

    @Autowired
    private EquipmentReportMapper equipmentReportMapper;

    @Autowired
    private DonorRepository donorRepository;

    @Autowired
    private RegimenRepository regimenRepository;
    private UserSummaryParams userSummaryParam = null;

    @Autowired
    private FacilityService facilityService;

    @Autowired
    private SupervisoryNodeService supervisoryNodeService;

    @Autowired
    private RequisitionGroupService requisitionGroupService;

    @Autowired
    private FacilityLevelMapper levelMapper;

    @Autowired
    private CommaSeparator commaSeparator;

    public List<Product> getAllProducts() {
        return productMapper.getAll();
    }

    public List<RegimenCategory> getAllRegimenCategories() {
        return regimenCategoryReportMapper.getAll();
    }

    //left because referenced but identical to getAllRegimenCategories()
    public List<RegimenCategory> getAllRegimenCategory() {
        return regimenCategoryReportMapper.getAll();
    }

    //For created for future
    public List<RegimenCategory> getRegimenCategoryById(Long id) {
        return regimenCategoryReportMapper.getById(id);
    }

    public List<Regimen> getRegimenByProgram() {
        return regimenReportMapper.getByProgram();
    }

    public List<Regimen> getAllRegimens() {
        return regimenReportMapper.getAll();
    }

    public List<GeographicZone> getGeographicLevelById(Long geographicLevelId) {
        return geographicZoneMapper.getGeographicZoneByLevel(geographicLevelId);
    }

    public List<FlatGeographicZone> getFlatGeographicZoneList() {
        return geographicZoneMapper.getFlatGeographicZoneList();
    }

    public List<Regimen> getRegimenByCategory(Long regimenCategoryId) {
        return regimenReportMapper.getRegimenByCategory(regimenCategoryId);
    }

    public List<Product> getProductsActiveUnderProgram(Long programId) {
        if (configurationService.getBoolValue("ALLOW_PRODUCT_CATEGORY_PER_PROGRAM")) {
            return productMapper.getProductsForProgramPickCategoryFromProgramProduct(programId);
        }
        return productMapper.getProductsForProgram(programId);
    }

    public List<Product> getProductListByCategory(Integer programId, Integer categoryId) {
        if (categoryId == null || categoryId <= 0) {
            return productMapper.getAll();
        }
        return productMapper.getProductListByCategory(programId, categoryId);
    }

    public List<Product> getPushProgramProducts() {
        return productMapper.getPushProgramProducts();
    }

    public List<org.openlmis.core.domain.Product> getFullProductList(RowBounds rowBounds) {
        return productMapper.getFullProductList(rowBounds);
    }

    public Product getProductByCode(String code) {
        return productMapper.getProductByCode(code);
    }

    public List<FacilityType> getFacilityTypes() {
        return facilityTypeMapper.getAll();
    }

    public List<FacilityType> getAllFacilityTypes() {
        return facilityTypeMapper.getAllFacilityTypes();
    }

    public List<FacilityType> getFacilityTypesForProgram(Long programId) {
        return facilityTypeMapper.getForProgram(programId);
    }

    public List<FacilityType> getFacilityLevels(Long programId, Long userId) {
        List<org.openlmis.core.domain.Facility> facilities = facilityService.getUserSupervisedFacilities(userId, programId, MANAGE_EQUIPMENT_INVENTORY);
        facilities.add(facilityService.getHomeFacility(userId));

        String facilityIdString = StringHelper.getStringFromListIds(facilities);

        return facilityTypeMapper.getLevels(programId, facilityIdString);
    }


    public List<RequisitionGroup> getAllRequisitionGroups() {
        return this.rgMapper.getAll();
    }

    public List<RequisitionGroup> getRequisitionGroupsByProgramAndSchedule(int program, int schedule) {
        return this.rgMapper.getByProgramAndSchedule(program, schedule);
    }

    public List<RequisitionGroup> getRequisitionGroupsByProgram(int program) {
        return this.rgMapper.getByProgram(program);
    }

    public List<ProductCategory> getAllProductCategories() {
        return this.productCategoryMapper.getAll();
    }

    public List<ProductCategory> getCategoriesForProgram(int programId) {
        if (configurationService.getBoolValue("ALLOW_PRODUCT_CATEGORY_PER_PROGRAM")) {
            return this.productCategoryMapper.getForProgramUsingProgramProductCategory(programId);
        }
        return this.productCategoryMapper.getForProgram(programId);
    }

    public List<AdjustmentType> getAllAdjustmentTypes() {
        return adjustmentTypeReportMapper.getAll();
    }

    public List<Integer> getOperationYears() {


        int startYear = configurationService.getConfigurationIntValue(Constants.START_YEAR);

        Calendar calendar = Calendar.getInstance();

        int now = calendar.get(Calendar.YEAR);

        List<Integer> years = new ArrayList<>();

        if (startYear == 0 || startYear > now) {
            years.add(now);
            return years;
        }

        for (int year = startYear; year <= now; year++) {

            years.add(year);
        }

        return years;

    }

    public List<Object> getAllMonths() {

        return configurationService.getConfigurationListValue(Constants.MONTHS, ",");
    }

    public List<Program> getAllPrograms() {
        return programMapper.getAll();
    }

    public List<Program> getAllPrograms(Long userId) {
        return programMapper.getAllForUser(userId);
    }

    public Program getProgramByCode(String code) {
        return programMapper.getProgramByCode(code);
    }

    //It return all programs only with regimen
    public List<Program> getAllRegimenPrograms() {
        return programMapper.getAllRegimenPrograms();
    }

    public List<Program> getAllProgramsWithBudgeting() {
        return programMapper.getAllProgramsWithBudgeting();
    }

    public List<Schedule> getAllSchedules() {
        return scheduleMapper.getAll();
    }

    public List<org.openlmis.report.model.dto.GeographicZone> getAllZones() {
        return geographicZoneMapper.getAll();
    }

    public List<GeographicLevel> getAllGeographicLevels() {
        return geographicLevelMapper.getAll();
    }

    public List<DosageUnit> getDosageUnits() {
        return dosageUnitMapper.getAll();
    }

    public List<Facility> getAllFacilities(RowBounds bounds) {
        return facilityReportMapper.getAll(bounds);
    }

    public Facility getFacilityByCode(String code) {
        return facilityReportMapper.getFacilityByCode(code);
    }

    public List<Facility> getFacilities(Long program, Long schedule, Long type, Long requisitionGroup, Long zone, Long userId) {
        // this method does not work if no program is specified
        if (program == 0) {
            return null;
        }

        if (schedule == 0 && type == 0) {
            return facilityReportMapper.getFacilitiesByProgram(program, zone, userId);
        }

        if (type == 0 && requisitionGroup == 0) {
            return facilityReportMapper.getFacilitiesByProgramSchedule(program, schedule, zone, userId);
        }

        if (type == 0 && requisitionGroup != 0) {
            return facilityReportMapper.getFacilitiesByProgramScheduleAndRG(program, schedule, requisitionGroup, zone, userId);
        }

        if (requisitionGroup == 0 && type != 0) {
            return facilityReportMapper.getFacilitiesByProgramZoneFacilityType(program, zone, userId, type);
        }

        if (requisitionGroup == 0) {
            return facilityReportMapper.getFacilitiesByPrgraomScheduleType(program, schedule, type, zone, userId);
        }

        return facilityReportMapper.getFacilitiesByPrgraomScheduleTypeAndRG(program, schedule, type, requisitionGroup, zone);
    }

    public List<Facility> getFacilitiesBy(Long userId, Long supervisoryNodeId, String requisitionGroup, Long program, Long schedule) {

        return facilityReportMapper.getFacilitiesBy(userId, supervisoryNodeId, requisitionGroup, program, schedule);

    }

    public List<Facility> getFacilityByGeographicZoneTree(Long userId, Long zoneId, Long programId) {
        return facilityReportMapper.getFacilitiesByGeographicZoneTree(userId, zoneId, programId);
    }
    public List<Facility> getFacilityByGeographicZone(Long userId, Long zoneId) {
        return facilityReportMapper.getFacilitiesByGeographicZone(userId, zoneId);
    }

    public List<HashMap> getFacilitiesForNotifications(Long userId, Long zoneId) {

        return facilityReportMapper.getFacilitiesForNotifications(userId, zoneId);

    }

    public List<ProcessingPeriod> getAllProcessingPeriods() {
        return processingPeriodMapper.getAll();
    }

    public List<ProcessingSchedule> getAllProcessingSchedules() {
        return processingScheduleMapper.getAll();
    }


    public List<ProcessingPeriod> getFilteredPeriods(Date startDate, Date endDate) {
        if (startDate == null && endDate == null) {
            return processingPeriodMapper.getAll();
        }
        return processingPeriodMapper.getFilteredPeriods(startDate, endDate);
    }

    public List<ProductGroup> getAllProductGroups() {
        return productGroupReportMapper.getAll();
    }

    public List<ProductForm> getAllProductForm() {
        return productFormReportMapper.getAll();
    }

    public List<Product> getListOfProducts(String productIds) {
        return productMapper.getSelectedProducts(productIds);
    }

    public String getFacilityNameForRnrId(Long rnrId) {
        return requisitionMapper.getFacilityNameForRnrId(rnrId);
    }

    public org.openlmis.core.domain.Facility getFacilityForRnrId(Long rnrId) {
        return requisitionMapper.getFacilityForRnrId(rnrId);
    }

    public String getPeriodTextForRnrId(Long rnrId) {
        return requisitionMapper.getPeriodTextForRnrId(rnrId);
    }

    public String getProgramNameForRnrId(Long rnrId) {
        return requisitionMapper.getProgramNameForRnrId(rnrId);
    }

    public List<Program> getAllUserSupervisedActivePrograms(Long userId) {
        return programMapper.getUserSupervisedActivePrograms(userId);
    }

    public List<Program> getUserSupervisedActiveProgramsBySupervisoryNode(Long userId, Long supervisoryNodeId) {
        return programMapper.getUserSupervisedActiveProgramsBySupervisoryNode(userId, supervisoryNodeId);
    }

    public List<SupervisoryNode> getAllUserSupervisoryNode(Long userId) {
        return supervisoryNodeReportMapper.getAllSupervisoryNodesInHierarchyByUser(userId);
    }

    public List<SupervisoryNode> getAllSupervisoryNodesByUserHavingActiveProgram(Long userId) {
        return supervisoryNodeReportMapper.getAllSupervisoryNodesByUserHavingActiveProgram(userId);
    }

    public List<SupervisoryNode> getAllSupervisoryNodesByParentNodeId(Long supervisoryNodeId) {
        return supervisoryNodeReportMapper.getAllSupervisoryNodesByParentNodeId(supervisoryNodeId);
    }

    public List<ProgramProduct> getAllProgramProducts() {
        return programProductMapper.getAll();
    }

    public List<FacilityTypeApprovedProduct> getAllFacilityTypeApprovedProducts() {
        return facilityApprovedProductMapper.getAll();
    }

    public List<UserRoleAssignmentsReport> getAllRolesBySupervisoryNodeHavingProgram(Long roleId, Long programId, Long supervisoryNodeId) {
        return userSummaryExReportMapper.getUserRoleAssignments(roleId, programId, supervisoryNodeId);
    }

    public List<UserRoleAssignmentsReport> getUserRoleAssignments(Map<String, String[]> filterCriteria) {
        return userSummaryExReportMapper.getUserRoleAssignment(getReportFilterData(filterCriteria));
    }

    public UserSummaryParams getReportFilterData(Map<String, String[]> filterCriteria) {
        if (filterCriteria != null) {
            userSummaryParam = new UserSummaryParams();
            userSummaryParam.setRoleId(StringUtils.isBlank(filterCriteria.get("roleId")[0]) ? 0 : Long.parseLong(filterCriteria.get("roleId")[0])); //defaults to 0
            userSummaryParam.setProgramId(StringUtils.isBlank(filterCriteria.get("programId")[0]) ? 0 : Long.parseLong(filterCriteria.get("programId")[0]));
            userSummaryParam.setSupervisoryNodeId(StringUtils.isBlank(filterCriteria.get("supervisoryNodeId")[0]) ? 0 : Long.parseLong(filterCriteria.get("supervisoryNodeId")[0]));
        }

        return userSummaryParam;
    }

    public List<EquipmentType> getEquipmentTypes() {
        return equipmentTypeReportMapper.getEquipmentTypeList();
    }


    public GeoZoneTree getGeoZoneTree(Long userId) {
        List<GeoZoneTree> zones = geographicZoneMapper.getGeoZonesForUser(userId);
        GeoZoneTree tree = geographicZoneMapper.getParentZoneTree();
        populateChildren(tree, zones);
        return tree;
    }

    public GeoZoneTree getGeoZoneTree(Long userId, Long programId) {
        List<GeoZoneTree> zones = geographicZoneMapper.getGeoZonesForUserByProgram(userId, programId);
        GeoZoneTree tree = geographicZoneMapper.getParentZoneTree();
        populateChildren(tree, zones);
        return tree;
    }

    private void populateChildren(GeoZoneTree tree, List<GeoZoneTree> source) {
        // find children from the source
        List<GeoZoneTree> children = new ArrayList<>();
        for (GeoZoneTree t : source) {
            if (t.getParentId() == tree.getId()) {
                children.add(t);
            }
        }

        tree.setChildren(children);

        for (GeoZoneTree zone : tree.getChildren()) {
            populateChildren(zone, source);
        }
    }

    public GeoZoneTree getUserGeoZoneTree(Long userId, Long programId) {
        GeoZoneTree tree = geographicZoneMapper.getParentZoneTree();
        populateUserGeographicZoneChildren(tree, userId, programId);
        return tree;
    }


    private void populateUserGeographicZoneChildren(GeoZoneTree tree, Long userId, Long programId) {
        tree.setChildren(geographicZoneMapper.getUserGeographicZoneChildren(programId, tree.getId(), userId));

        for (GeoZoneTree zone : tree.getChildren()) {
            populateUserGeographicZoneChildren(zone, userId, programId);
        }
    }

    public List<OrderFillRateSummaryReport> getOrderFillRateSummary(Long programId, Long periodId, Long scheduleId, Long facilityTypeId, Long userId, Long zoneId, String status) {
        return orderFillRateSummaryListMapper.getOrderFillRateSummaryReportData(programId, periodId, scheduleId, facilityTypeId, userId, zoneId, status);
    }

    public List<ProductCategoryProductTree> getProductCategoryProductByProgramId(int programId) {

        List<ProductCategory> productCategory = this.productCategoryMapper.getForProgramUsingProgramProductCategory(programId);

        List<ProductCategoryProductTree> productCategoryProducts = productCategoryMapper.getProductCategoryProductByProgramId(programId);

        List<ProductCategoryProductTree> newTreeList = new ArrayList<ProductCategoryProductTree>();

        for (ProductCategory pc : productCategory) {

            ProductCategoryProductTree object = new ProductCategoryProductTree();
            object.setCategory(pc.getName());
            object.setCategory_id(pc.getId());

            for (ProductCategoryProductTree productCategoryProduct : productCategoryProducts) {

                if (pc.getId() == productCategoryProduct.getCategory_id()) {
                    object.getChildren().add(productCategoryProduct);
                }
            }

            newTreeList.add(object);
        }
        return newTreeList;
    }

    public List<YearSchedulePeriodTree> getYearSchedulePeriodTree() {

        List<YearSchedulePeriodTree> yearSchedulePeriodTree = processingPeriodMapper.getYearSchedulePeriodTree();
        List<Schedule> schedules = scheduleMapper.getAll();

        List<Integer> years = getOperationYears();

        List<YearSchedulePeriodTree> yearList = new ArrayList<YearSchedulePeriodTree>();

        //add the year layer
        for (Integer year : years) {

            YearSchedulePeriodTree yearObject = new YearSchedulePeriodTree();
            yearObject.setYear(year.toString());

            // Add the schedule layer
            for (Schedule schedule : schedules) {

                YearSchedulePeriodTree scheduleObject = new YearSchedulePeriodTree();
                scheduleObject.setGroupname(schedule.getName());

                for (YearSchedulePeriodTree period : yearSchedulePeriodTree) {

                    if (schedule.getId() == period.getGroupid() && period.getYear().equals(year.toString())) {
                        scheduleObject.getChildren().add(period);
                    }
                }

                yearObject.getChildren().add(scheduleObject);
            }

            yearList.add(yearObject);
        }

        return yearList;
    }

    public List<Equipment> getEquipmentsByType(Long equipmentType) {

        if (equipmentType == 0)
            return equipmentReportMapper.getEquipmentAll();
        else
            return equipmentReportMapper.getEquipmentsByType(equipmentType);

    }

    public List<Donor> getAllDonors() {
        return donorRepository.getAll();
    }

    public List<Schedule> getSchedulesByProgram(long program) {
        return scheduleMapper.getSchedulesForProgram(program);
    }

    public List<Facility> getFacilities(Long type) {
        return facilityReportMapper.getFacilitiesBytype(type);
    }

    public List<Facility> getFacilities(Map<String, String[]> filterCriteria, long userId) {
        List<Facility> facilitiesList = null;
//        (@Param("program") Long program, @Param("zone") Long zone, @Param("userId") Long userId, @Param("type") Long type);
        long program = 0;
        long zone;

        long type;
        program = StringUtils.isBlank(filterCriteria.get("programId")[0]) ? 0 : Long.parseLong(filterCriteria.get("programId")[0]);
        zone = StringUtils.isBlank(filterCriteria.get("zoneId")[0]) ? 0 : Long.parseLong(filterCriteria.get("zoneId")[0]);
        type = StringUtils.isBlank(filterCriteria.get("facilityTypeId")[0]) ? 0 : Long.parseLong(filterCriteria.get("facilityTypeId")[0]);

        facilitiesList = this.facilityReportMapper.getFacilitiesByProgramZoneFacilityType(program, zone, userId, type);

        return facilitiesList;
    }

    public List<DosageFrequency> getAllDosageFrequencies() {
        return regimenRepository.getAllDosageFrequencies();
    }

    public List<RegimenProductCombination> getAllRegimenProductCombinations() {
        return regimenRepository.getAllRegimenProductCombinations();
    }

    public List<RegimenCombinationConstituent> getAllRegimenCombinationConstituents() {
        return regimenRepository.getAllRegimenCombinationConstituents();
    }

    public List<RegimenConstituentDosage> getAllRegimenConstituentDosages() {
        return regimenRepository.getAllRegimenConstituentsDosages();
    }

    public List<TimelinessReport> getTimelinessStatusData(Long programId, Long periodId, Long scheduleId, Long zoneId, String status) {
        return timelinessStatusReportMapper.getTimelinessStatusData(programId, periodId, scheduleId, zoneId, status);
    }

    public List<TimelinessReport> getFacilityRnRStatusData(Long programId, Long periodId, Long scheduleId, Long zoneId, String status, String facilityIds) {
        return timelinessStatusReportMapper.getFacilityRnRStatusData(programId, periodId, scheduleId, zoneId, status, facilityIds);
    }

    public List<TimelinessReport> getTimelinessReportingDates(Long periodId) {
        return timelinessStatusReportMapper.getTimelinessReportingDates(periodId);
    }

    public List<Product> getRmnchProducts() {
        return productMapper.getRmnchProducts();
    }

    public List<ProcessingPeriod> getLastPeriods(Long programId){
        return processingPeriodMapper.getLastPeriods(programId);
    }


    public List<YearSchedulePeriodTree> getVaccineYearSchedulePeriodTree() {
        List<YearSchedulePeriodTree> yearSchedulePeriodTree = processingPeriodMapper.getVaccineYearSchedulePeriodTree();

        Set<String> years = new HashSet<>();
        Set<Schedule> schedules = new HashSet<>();
        for (YearSchedulePeriodTree periodTree : yearSchedulePeriodTree) {
            years.add(periodTree.getYear());
            schedules.add(new Schedule(periodTree.getGroupid(), periodTree.getGroupname(), null, null));
        }

        List<YearSchedulePeriodTree> yearList = new ArrayList<>();

        //add the year layer
        for (String year : years) {

            YearSchedulePeriodTree yearObject = new YearSchedulePeriodTree();
            yearObject.setYear(year);

            // Add the schedule layer
            for (Schedule schedule : schedules) {

                YearSchedulePeriodTree scheduleObject = new YearSchedulePeriodTree();
                scheduleObject.setGroupname(schedule.getName());

                for (YearSchedulePeriodTree period : yearSchedulePeriodTree) {

                    if (schedule.getId() == period.getGroupid() && period.getYear().equals(year)) {
                        scheduleObject.getChildren().add(period);
                    }
                }
                if (scheduleObject.getChildren().size() > 0) {

                    yearObject.getChildren().add(scheduleObject);
                }

            }

            yearList.add(yearObject);
        }

        return yearList;
    }

    public Long getCurrentPeriodIdForVaccine(){
        return processingPeriodMapper.getCurrentPeriodIdForVaccine();
    }

    //New
    public List<FacilityLevelTree> getFacilityByLevel(Long programId, Long userId) {


        org.openlmis.core.domain.Facility homeFacility = facilityService.getHomeFacility(userId);

        List<SupervisoryNode> supervisoryNodes = supervisoryNodeService.getAllSupervisoryNodesInHierarchyBy(userId, programId, MANAGE_EQUIPMENT_INVENTORY);
        List<org.openlmis.core.domain.RequisitionGroup> requisitionGroups = requisitionGroupService.getRequisitionGroupsBy(supervisoryNodes);

        List<FacilityLevelTree> facilityLevels = levelMapper.getFacilitiesByLevel(programId, commaSeparator.commaSeparateIds(requisitionGroups));
        List<FacilityLevelTree> parentTree = levelMapper.getParentTree(programId, commaSeparator.commaSeparateIds(requisitionGroups));

        List<FacilityLevelTree> treeList = new ArrayList<FacilityLevelTree>();

        for (FacilityLevelTree fa : facilityLevels) {

            FacilityLevelTree facilityObject = new FacilityLevelTree();
            facilityObject.setSuperVisedFacility(fa.getSuperVisedFacility());
            facilityObject.setSuperVisedFacilityId(fa.getSuperVisedFacilityId());
            facilityObject.setParentId(fa.getParentId());
            facilityObject.setHomeFacilityName(homeFacility.getName());
            facilityObject.setFacilityId(homeFacility.getId());

            for (FacilityLevelTree tree : parentTree) {

                if ((tree.getParentId() == facilityObject.getParentId()) && (tree.getSuperVisedFacilityId() == facilityObject.getSuperVisedFacilityId())) {

                    facilityObject.getChildren().add(tree);

                }

            }

            treeList.add(facilityObject);


        }


        return treeList;

    }

//End new

}
