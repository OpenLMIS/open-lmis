/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.GeographicLevel;
import org.openlmis.core.domain.ProductGroup;
import org.openlmis.core.domain.ProductForm;
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.report.mapper.lookup.AdjustmentTypeReportMapper;
import org.openlmis.report.mapper.lookup.*;
import org.openlmis.report.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.openlmis.report.model.dto.*;

import java.util.*;

@Service
@NoArgsConstructor
public class ReportLookupService {

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
    private ProductGroupReportMapper productGroupReportMapper;

    @Autowired
    private ProductFormReportMapper productFormReportMapper;


    public List<Product> getAllProducts(){
        return productMapper.getAll();
    }

    public List<Product> getProductListByCategory(Integer categoryId){
        if(categoryId == null || categoryId == -1 || categoryId == 0){
            return productMapper.getAll();
        }
        return productMapper.getProductListByCategory(categoryId);
    }

    public List<ProductList> getFullProductList(){
        return productMapper.getFullProductList();
    }

    public List<FacilityType> getFacilityTypes(){
        return facilityTypeMapper.getAll();
    }

    public List<RequisitionGroup> getAllRequisitionGroups(){
        return this.rgMapper.getAll();
    }

    public List<RequisitionGroup> getRequisitionGroupsByProgramAndSchedule(int program, int schedule){
        return this.rgMapper.getByProgramAndSchedule(program, schedule);
    }


    public List<ProductCategory> getAllProductCategories(){
        return this.productCategoryMapper.getAll();
    }

    public List<AdjustmentType> getAllAdjustmentTypes(){
        return adjustmentTypeReportMapper.getAll();
    }

    public List<Integer> getOperationYears(){


        int startYear = configurationService.getConfigurationIntValue(Constants.START_YEAR);

        Calendar calendar = Calendar.getInstance();

        int now = calendar.get(Calendar.YEAR);

        List<Integer> years = new ArrayList<>();

        if(startYear == 0 || startYear > now)  {
            years.add(now);
            return years;
        }

        for (int year = startYear; year <= now; year++){

            years.add(year);
        }

         return years;

    }

    public List<Object> getAllMonths(){

        return configurationService.getConfigurationListValue(Constants.MONTHS,",");
    }

    public List<Program> getAllPrograms(){
        return programMapper.getAll();
    }

    public List<Schedule> getAllSchedules(){
        return scheduleMapper.getAll();
    }

    //TODO: implement this method
    public List<org.openlmis.report.model.dto.GeographicZone> getAllZones() {
        return geographicZoneMapper.getAll();
    }

    public List<GeographicLevel> getAllGeographicLevels() {
        return geographicLevelMapper.getAll();
    }

    public List<DosageUnit> getDosageUnits(){
        return dosageUnitMapper.getAll();
    }

    public List<Facility> getAllFacilities(){
        return facilityReportMapper.getAll();
    }

    public List<ProcessingPeriod> getAllProcessingPeriods(){
        return processingPeriodMapper.getAll();
    }

    public List<ProcessingPeriod> getFilteredPeriods(Date startDate, Date endDate){
        if(startDate == null && endDate == null){
            return processingPeriodMapper.getAll();
        }
        return processingPeriodMapper.getFilteredPeriods(startDate, endDate);
    }

    public List<ProductGroup> getAllProductGroups(){
        return productGroupReportMapper.getAll();
    }

    public List<ProductForm> getAllProductForm(){
        return productFormReportMapper.getAll();
    }
}
