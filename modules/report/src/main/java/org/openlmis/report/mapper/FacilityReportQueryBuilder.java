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
       //  if (filterCriteria.getFacilityTypeId() != 0) {
       //     WHERE("F.FIRST_NAME like ${firstName}");
       // }

        //ORDER_BY("F.LAST_NAME");
        //LIMIT 10 OFFSET 10  // LIMIT pageSize OFFSET pageSize * (page-1)
       // BEGIN(); // Clears ThreadLocal variable
       // SELECT("P.ID, P.USERNAME, P.PASSWORD, P.FULL_NAME");
       //SELECT("P.LAST_NAME, P.CREATED_ON, P.UPDATED_ON");
       // FROM("PERSON P");
       // FROM("ACCOUNT A");
       // INNER_JOIN("DEPARTMENT D on D.ID = P.DEPARTMENT_ID");
       // INNER_JOIN("COMPANY C on D.COMPANY_ID = C.ID");
       // WHERE("P.ID = A.ID");
       // WHERE("P.FIRST_NAME like ?");
       // OR();
       // WHERE("P.LAST_NAME like ?");
       // GROUP_BY("P.ID");
       // HAVING("P.LAST_NAME like ?");
       // OR();
       // HAVING("P.FIRST_NAME like ?");
        if(sorter.getFacilityName().equalsIgnoreCase("asc") || sorter.getFacilityName().equalsIgnoreCase("desc")){
            ORDER_BY("name");
        }
        //GROUP_BY("id");
       // return SQL();

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

        //GROUP_BY("id");

        return SQL();
    }
}
