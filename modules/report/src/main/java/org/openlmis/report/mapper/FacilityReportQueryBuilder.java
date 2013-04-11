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
        SELECT("F.id, F.code, F.name, F.active as active, FT.name as facilityType, GZ.name as region, FO.code as owner,F.latitude::text ||',' ||  F.longitude::text  ||', ' || F.altitude::text gpsCoordinates,F.mainphone as phoneNumber, F.fax as fax");
        //FROM("facility_types FT");
        FROM("facilities F");
        JOIN("facility_types FT on FT.id = F.typeid");
        LEFT_OUTER_JOIN("geographic_zones GZ on GZ.id = F.geographiczoneid");
        LEFT_OUTER_JOIN("facility_operators FO on FO.id = F.operatedbyid");


        if (filter.getStatusId() != null) {
            WHERE("F.active = #{filterCriteria.statusId}");
        }
        if (filter.getZoneId() != 0) {
             WHERE("F.geographiczoneid = #{filterCriteria.zoneId}");
        }
        if (filter.getFacilityTypeId() != 0) {
            WHERE("F.typeid = #{filterCriteria.facilityTypeId}");
        }

        if(sorter.getFacilityName().equalsIgnoreCase("asc")){
            ORDER_BY("F.name asc");
        }
        if(sorter.getFacilityName().equalsIgnoreCase("desc")){
            ORDER_BY("F.name desc");
        }

        if(sorter.getCode().equalsIgnoreCase("asc")){
            ORDER_BY("F.code asc");
        }
        if(sorter.getCode().equalsIgnoreCase("desc")){
            ORDER_BY("F.code desc");
        }

        if(sorter.getFacilityType().equalsIgnoreCase("asc")){
            ORDER_BY("F.typeid asc");
        }
        if(sorter.getFacilityType().equalsIgnoreCase("desc")){
            ORDER_BY("F.typeid desc");
        }

        return SQL();
    }
    public static String SelectFilteredFacilitiesCountSql(Map params){//,ReportData SortCriteria ,int page,int pageSize) {

        FacilityReportFilter filter  = (FacilityReportFilter)params.get("filterCriteria");
         // filterCriteria
        BEGIN();
        SELECT("COUNT(*)");
        FROM("facilities F");

        if (filter.getStatusId() != null) {
            WHERE("F.active = #{filterCriteria.statusId}");
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
