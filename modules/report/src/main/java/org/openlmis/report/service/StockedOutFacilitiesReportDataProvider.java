package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.mapper.lookup.RequisitionGroupReportMapper;
import org.openlmis.report.mapper.StockedOutReportMapper;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.filter.StockedOutReportFilter;
import org.openlmis.report.model.report.MasterReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 */
@Component
@NoArgsConstructor
public class StockedOutFacilitiesReportDataProvider extends ReportDataProvider {


    private StockedOutReportMapper reportMapper;
    private RequisitionGroupReportMapper requisitionGroupMapper;

    @Autowired
    public StockedOutFacilitiesReportDataProvider(StockedOutReportMapper mapper, RequisitionGroupReportMapper rgMapper) {
        this.reportMapper = mapper;
        this.requisitionGroupMapper = rgMapper;
    }

    @Override
    protected List<? extends ReportData> getBeanCollectionReportData(Map<String, String[]> filterCriteria) {
        RowBounds rowBounds = new RowBounds(RowBounds.NO_ROW_OFFSET,RowBounds.NO_ROW_LIMIT);

        return reportMapper.getReport(getReportFilterData(filterCriteria),rowBounds);
    }

    @Override
    protected List<? extends ReportData> getResultSetReportData(Map<String, String[]> filterCriteria) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.0
    }

    @Override
    public List<? extends ReportData> getReportDataByFilterCriteriaAndPagingAndSorting(Map<String, String[]> filterCriteria, Map<String, String[]> SortCriteria, int page, int pageSize) {
        RowBounds rowBounds = new RowBounds((page-1) * pageSize,pageSize);

        List<MasterReport> reportList = new ArrayList<MasterReport>();
        MasterReport report = new MasterReport();
        report.details =  reportMapper.getReport(getReportFilterData(filterCriteria),rowBounds);
        report.summary = reportMapper.getReportSummary(getReportFilterData(filterCriteria));
        reportList.add( report );

        List<? extends ReportData> list;
        list = reportList;
        return list;
    }

    @Override
    public ReportData getReportFilterData(Map<String, String[]> params) {
        String period           = params.get("period")[0];
        String reportingGroup   = params.get("rgroup")[0] ;
        String facilityType     = params.get("ftype")[0] ;
        String program          = params.get("program")[0];
        String schedule         = params.get("schedule")[0];
        String productCategory   = params.get("productCategory")[0];

        String periodType = params.get("periodType")[0];
        String fromYear =  params.get("fromYear")[0];
        String toYear = params.get("toYear")[0];
        String fromMonth = params.get("fromMonth")[0];
        String toMonth = params.get("toMonth")[0];

        Date startDate = null;
        Date endDate = null;
        Integer startYear = null;
        Integer startMonth = null;
        Integer endYear = null;
        Integer endMonth = null;

        if(fromYear != null   && !fromYear.equals("undefined")){
            startYear =  Integer.valueOf(fromYear);
        }

        if(toYear != null   && !toYear.equals("undefined")){
            endYear =  Integer.valueOf (toYear);
        }
        if(fromMonth != null   && !fromMonth.equals("undefined")){
            startMonth =  Integer.valueOf(fromMonth);
        }
        if(toMonth != null   && !toMonth.equals("undefined")){
            endMonth =  Integer.valueOf(toMonth);
        }

        if(periodType !=null && !periodType.equals("predefined")){
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, startYear);
            calendar.set(Calendar.MONTH, startMonth);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            startDate = calendar.getTime();

            calendar.set(Calendar.YEAR, endYear);
            calendar.set(Calendar.MONTH, endMonth);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            endDate =calendar.getTime();
        }
        return new StockedOutReportFilter(startDate,endDate,period,reportingGroup,facilityType,program,schedule,productCategory,periodType);
    }

    @Override
    public int getReportDataCountByFilterCriteria(Map<String, String[]> filterCriteria) {
        return reportMapper.getStockedOutTotalFacilities(getReportFilterData(filterCriteria)).get(0);
    }




    @Override
    public HashMap<String, String> getAdditionalReportData(Map params){
        HashMap<String, String> result = new HashMap<String, String>() ;

        // spit out the summary section on the report.
        String totalFacilities = reportMapper.getTotalFacilities( params ).get(0).toString();
        String nonReporting = reportMapper.getStockedOutTotalFacilities( getReportFilterData(params) ).get(0).toString();
        result.put("TOTAL_FACILITIES", totalFacilities);
        result.put("TOTAL_NON_REPORTING", nonReporting);

        // Assume by default that the 100% of facilities didn't report
        Long percent = Long.parseLong("100");
        if(totalFacilities != "0"){
            percent = Math.round((Double.parseDouble(nonReporting) /  Double.parseDouble(totalFacilities)) * 100);

        }
        result.put("PERCENTAGE_NON_REPORTING",percent.toString());

        // Interprate the different reporting parameters that were selected on the UI
        String period           = ((String[])params.get("period"))[0];
        String reportingGroup   = ((String[])params.get("rgroup"))[0] ;
        String facilityType     = ((String[])params.get("ftype"))[0] ;
        String program          = ((String[])params.get("program"))[0];

        // compose the filter text as would be presented on the pdf reports.
        String header = "";
        if(program != "" && !program.endsWith("undefined")){
            header += "Program : " + this.reportMapper.getProgram(Integer.parseInt(program)).get(0).getName();
        }
        if(reportingGroup != "" && !reportingGroup.endsWith( "undefined")){
            header = "\nRequisition Group : " + this.requisitionGroupMapper.getById(Integer.parseInt(reportingGroup)).get(0).getName();
        }
        if(facilityType != "" && !facilityType.endsWith( "undefined")){
            header += "\nFacility Type : " + this.reportMapper.getFacilityType(Integer.parseInt(facilityType)).get(0).getName();
        }
        if(period != "" && !period.endsWith("undefined")){
            header += "\nPeriod : " + this.reportMapper.getPeriodId(Integer.parseInt(period)).get(0).getName();
        }
        result.put("REPORT_FILTER_PARAM_VALUES", header);

        return    result;
    }


}
