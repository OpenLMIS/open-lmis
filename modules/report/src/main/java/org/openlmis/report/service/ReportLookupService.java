package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.service.ConfigurationService;
import org.openlmis.report.mapper.AdjustmentTypeReportMapper;
import org.openlmis.report.mapper.lookup.*;
import org.openlmis.report.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.openlmis.report.model.dto.*;

import java.util.*;

/**
 * e-lmis
 * Created by: Elias Muluneh
 * Date: 4/12/13
 * Time: 2:47 AM
 */
@Service
@NoArgsConstructor
public class ReportLookupService {

    private ProductReportMapper productMapper;
    private RequisitionGroupReportMapper rgMapper;
    private ProductCategoryReportMapper productCategoryMapper;
    private AdjustmentTypeReportMapper adjustmentTypeReportMapper;
    private ConfigurationService configurationService;
    private ScheduleReportMapper scheduleMapper;
    private ProgramReportMapper programMapper;

    @Autowired
    public ReportLookupService(
                                ProductReportMapper productMapper
                                , RequisitionGroupReportMapper rgMapper
                                , ProductCategoryReportMapper productCategoryMapper
                                , AdjustmentTypeReportMapper adjustmentTypeReportMapper
                                , ConfigurationService configurationService
                                , ScheduleReportMapper scheduleMapper
                                , ProgramReportMapper programMapper
                             ){
        this.productMapper = productMapper;
        this.rgMapper = rgMapper;
        this.productCategoryMapper = productCategoryMapper;
        this.adjustmentTypeReportMapper = adjustmentTypeReportMapper;
        this.configurationService = configurationService;
        this.programMapper = programMapper;
        this.scheduleMapper = scheduleMapper;
    }

    public List<Product> getAllProducts(){
        return productMapper.getAll();
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

}
