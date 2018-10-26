package org.openlmis.report.builder;

import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.jdbc.SqlBuilder;
import org.openlmis.report.model.params.StockReportParam;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.BEGIN;
import static org.apache.ibatis.jdbc.SqlBuilder.FROM;
import static org.apache.ibatis.jdbc.SqlBuilder.SELECT;
import static org.apache.ibatis.jdbc.SqlBuilder.WHERE;

public class DailyFullStockOnHandQueryBuilder {

    public static String get(Map params) {
        BEGIN();
        return new SQL()
                .SELECT("facilityId, productCode, soh, occurred")
                .FROM("(" + getStockOnHandInfo(params) + ") as tmp")
                .WHERE("tmp.rank = 1").toString();
    }

    public static String getStockOnHandInfo(Map params) {

        StockReportParam filter = (StockReportParam) params.get("filterCriteria");

        SELECT("facility_id as facilityId, drug_code as productCode, soh, occurred");
        SELECT("row_number() over (partition by facility_id, drug_code order by occurred desc) rank");
        FROM("vw_daily_full_soh");
        writePredicates(filter);
        return SqlBuilder.SQL();
    }

    private static void writePredicates(StockReportParam filter) {
        if(null != filter) {
            WHERE("vw_daily_full_soh.occurred <= #{filterCriteria.endTime}");

            if(null != filter.getProvinceCode()) {
                WHERE("vw_daily_full_soh.province_code = #{filterCriteria.provinceCode}");
            }
            if(null != filter.getDistrictCode()) {
                WHERE("vw_daily_full_soh.district_code = #{filterCriteria.districtCode}");
            }
            if(null != filter.getFacilityId()) {
                WHERE("vw_daily_full_soh.facility_id = #{filterCriteria.facilityId}");
            }
            if (null != filter.getProductCode()) {
                WHERE("vw_daily_full_soh.drug_code = #{filterCriteria.productCode}");
            }
        }
    }
}
