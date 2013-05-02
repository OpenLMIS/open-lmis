package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.mapper.AverageConsumptionReportMapper;
import org.openlmis.report.mapper.NonReportingFacilityReportMapper;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.filter.AverageConsumptionReportFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 */
@Component
@NoArgsConstructor
public class AverageConsumptionReportDataProvider extends ReportDataProvider {


    private AverageConsumptionReportMapper reportMapper;


    @Autowired
    public AverageConsumptionReportDataProvider(AverageConsumptionReportMapper mapper) {
        this.reportMapper = mapper;
    }

    @Override
    protected List<? extends ReportData> getBeanCollectionReportData(Map<String, String[]> filterCriteria) {

        return reportMapper.getReportData(filterCriteria);
    }

    @Override
    protected List<? extends ReportData> getResultSetReportData(Map<String, String[]> filterCriteria) {
        return null;
    }

    @Override
    public List<? extends ReportData> getReportDataByFilterCriteriaAndPagingAndSorting(Map<String, String[]> filterCriteria, Map<String, String[]> SortCriteria, int page, int pageSize) {
        RowBounds rowBounds = new RowBounds((page-1)*pageSize,pageSize);
        AverageConsumptionReportFilter averageConsumptionReportFilter = null;

        if(filterCriteria != null){
            averageConsumptionReportFilter = new AverageConsumptionReportFilter();
            Date originalStart =  new Date();
            Date originalEnd =  new Date();

            averageConsumptionReportFilter.setZoneId(filterCriteria.get("zoneId") == null ? 0 : Integer.parseInt(filterCriteria.get("zoneId")[0]));  //defaults to 0
            averageConsumptionReportFilter.setFacilityTypeId(filterCriteria.get("facilityTypeId") == null ? 0 : Integer.parseInt(filterCriteria.get("facilityTypeId")[0])); //defaults to 0
            //ConsumptionReportFilter.setStatusId(filterCriteria.get("statusId") == null || filterCriteria.get("statusId")[0].isEmpty() ? null : Boolean.valueOf(filterCriteria.get("statusId")[0]));
            averageConsumptionReportFilter.setYearFrom(filterCriteria.get("fromYear") == null ? originalStart.getYear() : Integer.parseInt(filterCriteria.get("fromYear")[0])); //defaults to 0
            averageConsumptionReportFilter.setYearTo(filterCriteria.get("toYear") == null ? originalEnd.getYear() : Integer.parseInt(filterCriteria.get("toYear")[0])); //defaults to 0
            averageConsumptionReportFilter.setMonthFrom(filterCriteria.get("fromMonth") == null ? originalStart.getMonth() : Integer.parseInt(filterCriteria.get("fromMonth")[0])); //defaults to 0
            averageConsumptionReportFilter.setMonthTo(filterCriteria.get("toMonth") == null ? originalEnd.getMonth() : Integer.parseInt(filterCriteria.get("toMonth")[0])); //defaults to 0


            //first day of the selected/default month
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(originalStart);

            calendar1.set(Calendar.YEAR, averageConsumptionReportFilter.getYearFrom()); //originalStart.setYear(consumptionReportFilter.getYearFrom());
            calendar1.set(Calendar.MONTH, averageConsumptionReportFilter.getMonthFrom());//originalStart.setMonth(consumptionReportFilter.getMonthFrom());
            calendar1.set(Calendar.DAY_OF_MONTH, 1);//originalStart.setDate(1);
            originalStart = calendar1.getTime();
            averageConsumptionReportFilter.setStartDate(originalStart);

            //last day of the selected/default month
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(originalEnd);

            calendar.set(Calendar.YEAR, averageConsumptionReportFilter.getYearTo());//originalEnd.setYear(consumptionReportFilter.getYearTo());
            calendar.set(Calendar.MONTH, averageConsumptionReportFilter.getMonthTo());//originalEnd.setMonth(consumptionReportFilter.getMonthTo());

            calendar.add(Calendar.MONTH, 1);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.DATE, -1);
            originalEnd = calendar.getTime();

            averageConsumptionReportFilter.setEndDate(originalEnd);

        }
        return reportMapper.getFilteredSortedPagedAverageConsumptionReport(averageConsumptionReportFilter, null, rowBounds);
    }

    @Override
    public int getReportDataCountByFilterCriteria(Map<String, String[]> filterCriteria) {
        AverageConsumptionReportFilter averageConsumptionReportFilter = null;

        if(filterCriteria != null){
            averageConsumptionReportFilter = new AverageConsumptionReportFilter();
            Date originalStart =  new Date();
            Date originalEnd =  new Date();

            averageConsumptionReportFilter.setZoneId(filterCriteria.get("zoneId") == null ? 0 : Integer.parseInt(filterCriteria.get("zoneId")[0]));  //defaults to 0
            averageConsumptionReportFilter.setFacilityTypeId(filterCriteria.get("facilityTypeId") == null ? 0 : Integer.parseInt(filterCriteria.get("facilityTypeId")[0])); //defaults to 0
            //ConsumptionReportFilter.setStatusId(filterCriteria.get("statusId") == null || filterCriteria.get("statusId")[0].isEmpty() ? null : Boolean.valueOf(filterCriteria.get("statusId")[0]));
            averageConsumptionReportFilter.setYearFrom(filterCriteria.get("fromYear") == null ? originalStart.getYear() : Integer.parseInt(filterCriteria.get("fromYear")[0])); //defaults to 0
            averageConsumptionReportFilter.setYearTo(filterCriteria.get("toYear") == null ? originalEnd.getYear() : Integer.parseInt(filterCriteria.get("toYear")[0])); //defaults to 0
            averageConsumptionReportFilter.setMonthFrom(filterCriteria.get("fromMonth") == null ? originalStart.getMonth() : Integer.parseInt(filterCriteria.get("fromMonth")[0])); //defaults to 0
            averageConsumptionReportFilter.setMonthTo(filterCriteria.get("toMonth") == null ? originalEnd.getMonth() : Integer.parseInt(filterCriteria.get("toMonth")[0])); //defaults to 0


            //first day of the selected/default month
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(originalStart);

            calendar1.set(Calendar.YEAR, averageConsumptionReportFilter.getYearFrom()); //originalStart.setYear(consumptionReportFilter.getYearFrom());
            calendar1.set(Calendar.MONTH, averageConsumptionReportFilter.getMonthFrom());//originalStart.setMonth(consumptionReportFilter.getMonthFrom());
            calendar1.set(Calendar.DAY_OF_MONTH, 1);//originalStart.setDate(1);
            originalStart = calendar1.getTime();
            averageConsumptionReportFilter.setStartDate(originalStart);

            //last day of the selected/default month
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(originalEnd);

            calendar.set(Calendar.YEAR, averageConsumptionReportFilter.getYearTo());//originalEnd.setYear(consumptionReportFilter.getYearTo());
            calendar.set(Calendar.MONTH, averageConsumptionReportFilter.getMonthTo());//originalEnd.setMonth(consumptionReportFilter.getMonthTo());

            calendar.add(Calendar.MONTH, 1);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.DATE, -1);
            originalEnd = calendar.getTime();

            averageConsumptionReportFilter.setEndDate(originalEnd);

        }
        return (int) reportMapper.getFilteredSortedPagedAverageConsumptionReportCount(averageConsumptionReportFilter);
    }

    @Override
    public ReportData getReportFilterData(Map<String, String[]> params) {
       return new ReportData() {
                        @Override
                        public String toString() {
                            return "The Period: " ;
                        }
                    };
     }
}
