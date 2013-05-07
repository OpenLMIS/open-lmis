package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.core.repository.FacilityRepository;
import org.openlmis.core.repository.ProcessingPeriodRepository;
import org.openlmis.core.repository.ProgramRepository;
import org.openlmis.core.repository.RequisitionGroupRepository;
import org.openlmis.core.service.RequisitionGroupService;
import org.openlmis.report.mapper.NonReportingFacilityReportMapper;
import org.openlmis.report.mapper.RequisitionGroupReportMapper;
import org.openlmis.report.mapper.SummaryReportMapper;
import org.openlmis.report.model.ReportData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.openlmis.report.model.report.NonReportingFacilityReport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
@Component
@NoArgsConstructor
public class NonReportingFacilityReportDataProvider extends ReportDataProvider {


    private NonReportingFacilityReportMapper reportMapper;
    private RequisitionGroupReportMapper requisitionGroupMapper;

    @Autowired
    public NonReportingFacilityReportDataProvider(NonReportingFacilityReportMapper mapper, RequisitionGroupReportMapper rgMapper) {
        this.reportMapper = mapper;
        this.requisitionGroupMapper = rgMapper;
    }

    @Override
    protected List<? extends ReportData> getBeanCollectionReportData(Map<String, String[]> filterCriteria) {
        RowBounds rowBounds = new RowBounds(RowBounds.NO_ROW_OFFSET,RowBounds.NO_ROW_LIMIT);

        List<NonReportingFacilityReport> reportList = new ArrayList<NonReportingFacilityReport>();
        NonReportingFacilityReport report = new NonReportingFacilityReport();

        return reportMapper.getReport(filterCriteria,rowBounds);
        //report.details =  reportMapper.getReport(filterCriteria,rowBounds);
        //report.summary = reportMapper.getReportSummary(filterCriteria);

        //reportList.add( report );
        // cast the list of reports to
        //List<? extends ReportData> list;
        //list = reportList;
        //return list;
    }

    @Override
    protected List<? extends ReportData> getResultSetReportData(Map<String, String[]> filterCriteria) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.0
    }

    @Override
    public List<? extends ReportData> getReportDataByFilterCriteriaAndPagingAndSorting(Map<String, String[]> filterCriteria, Map<String, String[]> SortCriteria, int page, int pageSize) {
        RowBounds rowBounds = new RowBounds((page-1) * pageSize,pageSize);

        List<NonReportingFacilityReport> reportList = new ArrayList<NonReportingFacilityReport>();
        NonReportingFacilityReport report = new NonReportingFacilityReport();
        report.details =  reportMapper.getReport(filterCriteria,rowBounds);
        report.summary = reportMapper.getReportSummary(filterCriteria);
        reportList.add( report );

        List<? extends ReportData> list;
        list = reportList;
        return list;
    }

    @Override
    public int getReportDataCountByFilterCriteria(Map<String, String[]> filterCriteria) {
        return reportMapper.getNonReportingTotalFacilities(filterCriteria).get(0);
    }




    @Override
    public HashMap<String, String> getAdditionalReportData(Map params){
        HashMap<String, String> result = new HashMap<String, String>() ;

        // spit out the summary section on the report.
        result.put("TOTAL_FACILITIES", reportMapper.getTotalFacilities( params ).get(0).toString());

        result.put("TOTAL_NON_REPORTING", reportMapper.getNonReportingTotalFacilities( params ).get(0).toString());

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
            header += "Facility Type : " + this.reportMapper.getFacilityType(Integer.parseInt(facilityType)).get(0).getName();
        }
        if(period != "" && !period.endsWith("undefined")){
            header += "\nPeriod : " + this.reportMapper.getPeriodId(Integer.parseInt(period)).get(0).getName();
        }
        result.put("REPORT_FILTER_PARAM_VALUES", header);

        return    result;
    }


}
