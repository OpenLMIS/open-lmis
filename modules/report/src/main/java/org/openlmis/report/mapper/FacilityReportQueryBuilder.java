package org.openlmis.report.mapper;

import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.model.FacilityReportFilter;
import org.openlmis.report.model.FacilityReportSorter;
import org.openlmis.report.model.ReportData;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;
//import static org.apache.ibatis.jdbc.SelectBuilder.*;
/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 3/29/13
 * Time: 6:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class FacilityReportQueryBuilder {

    public static String SelectFilteredSortedPagedFacilitiesSql(Map params){//ReportData filterCriteria,ReportData SortCriteria ,int page,int pageSize) {

        FacilityReportFilter filter  = (FacilityReportFilter)params.get("filterCriteria");
        FacilityReportSorter sorter = (FacilityReportSorter)params.get("SortCriteria");
        BEGIN();
        SELECT("id, code, name");
        FROM("facilities ");

        if (filter.getStatusId() != null) {
           WHERE("active = #{filterCriteria.statusId}");
        }
        if (filter.getZoneId() != 0) {
             WHERE("geographiczoneid = #{filterCriteria.zoneId}");
        }
        if (filter.getFacilityTypeId() != 0) {
            WHERE("typeid = #{filterCriteria.facilityTypeId}");
        }

        if(sorter.getFacilityName().equalsIgnoreCase("asc")){
            ORDER_BY("name asc");
        }
        if(sorter.getFacilityName().equalsIgnoreCase("desc")){
            ORDER_BY("name desc");
        }

        if(sorter.getCode().equalsIgnoreCase("asc")){
            ORDER_BY("code asc");
        }
        if(sorter.getCode().equalsIgnoreCase("desc")){
            ORDER_BY("code desc");
        }

        if(sorter.getFacilityType().equalsIgnoreCase("asc")){
            ORDER_BY("typeid asc");
        }
        if(sorter.getFacilityType().equalsIgnoreCase("desc")){
            ORDER_BY("typeid desc");
        }

        return SQL();
    }
    public static String SelectFilteredFacilitiesCountSql(Map params){//,ReportData SortCriteria ,int page,int pageSize) {

        FacilityReportFilter filter  = (FacilityReportFilter)params.get("filterCriteria");
         // filterCriteria
        BEGIN();
        SELECT("COUNT(*)");
        FROM("facilities ");

        if (filter.getStatusId() != null) {
            WHERE("active = #{filterCriteria.statusId}");
        }
        if (filter.getZoneId() != 0) {
            WHERE("geographiczoneid = #{filterCriteria.zoneId}");
        }
        if (filter.getFacilityTypeId() != 0) {
            WHERE("typeid = #{filterCriteria.facilityTypeId}");
        }
        return SQL();
    }
}
