package org.openlmis.report.builder;

import org.apache.ibatis.jdbc.SQL;
import org.openlmis.report.model.params.StockReportParam;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;

public class ProductLotInfoQueryBuilder {

    public static String get(Map params) {
        BEGIN();
        return new SQL()
                .SELECT("*")
                .FROM("(" + subQuery(params) + ") as tmp")
                .WHERE("stockcardFacilityId is not null and rn = 1 and stockOnHandOfLot !=0").toString();
    }

    private static String getProductLotInfo(Map params) {
        StockReportParam filter = (StockReportParam) params.get("filterCriteria");
        SELECT("parent_zone.id as provinceId,parent_zone.name as provinceName");
        SELECT("zone.id as districtId,zone.name as districtName");
        SELECT("facilities.id as facilityId,facilities.name facilityName");
        SELECT("facilities.code as facilityCode");
        SELECT("products.id as productId,products.code productCode");
        SELECT("products.primaryname productName");
        SELECT("sce.occurred as occurred");
        SELECT("lots.lotnumber as lotNumber");
        SELECT("lots.expirationdate as expiryDate");
        SELECT("cast(v.valuecolumn as integer)as stockOnHandOfLot");
        SELECT("products.ishiv as isHiv");
        SELECT("stock_cards.modifieddate AS lastSyncDate");
        SELECT("stock_cards.facilityid as stockcardFacilityId");
        SELECT("NULL as price");
        SELECT("sceli.id as lotItemsId");
        FROM("facilities");
        LEFT_OUTER_JOIN("geographic_zones zone on facilities.geographiczoneid = zone.id");
        LEFT_OUTER_JOIN("geographic_zones parent_zone on zone.parentid = parent_zone.id");
        LEFT_OUTER_JOIN("stock_cards on stock_cards.facilityid = facilities.id");
        LEFT_OUTER_JOIN("products on stock_cards.productid = products.id");
        LEFT_OUTER_JOIN("stock_card_entries sce on stock_cards.id = sce.stockcardid");
        LEFT_OUTER_JOIN("stock_card_entry_lot_items sceli on sce.id = sceli.stockcardentryid");
        LEFT_OUTER_JOIN("lots on lots.id = sceli.lotid");
        LEFT_OUTER_JOIN("stock_card_entry_lot_items_key_values v on sceli.id = v.stockcardentrylotitemid");
        writePredicates(filter);

        return SQL();
    }

    private static String subQuery(Map params) {
        StockReportParam filter = (StockReportParam) params.get("filterCriteria");
        SQL sql = new SQL();
        sql.SELECT("*, row_number() over " +
                "  (partition by facilityId, productId, lotNumber order by lotItemsId desc) as rn")
                .FROM("(" + getProductLotInfo(params) + ") tmp9");
        if (null != filter) {
            sql.WHERE("occurred <= #{filterCriteria.endTime}");
        }
        return sql.toString();
    }

    private static void writePredicates(StockReportParam filter) {
        if(null != filter) {

            if(null != filter.getProvinceId()) {
                WHERE("parent_zone.id = #{filterCriteria.provinceId}");
            }
            if(null != filter.getDistrictId()) {
                WHERE("zone.id = #{filterCriteria.districtId}");
            }
            if(null != filter.getFacilityId()) {
                WHERE("facilities.id = #{filterCriteria.facilityId}");
            }
            if (null != filter.getProductCode()) {
                WHERE("products.code = #{filterCriteria.productCode}");
            }
            if (null != filter.getFilterCondition()) {
                WHERE(filter.getFilterCondition().getCondition());
            }
        }
    }
}