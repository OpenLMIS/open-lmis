package org.openlmis.report.builder;

import org.openlmis.report.model.params.OverStockReportParam;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;

public class ProductLotInfoQueryBuilder {

    public static String getProductLotInfo(Map params) {
        OverStockReportParam filter = (OverStockReportParam) params.get("filterCriteria");
        BEGIN();
        SELECT("parent_zone.id as provinceId,parent_zone.name as provinceName");
        SELECT("zone.id as districtId,zone.name as districtName");
        SELECT("facilities.id as facilityId,facilities.name facilityName");
        SELECT("products.id as productId,products.code productCode");
        SELECT("products.primaryname productName");
        SELECT("lots.lotnumber as lotNumber");
        SELECT("lots.expirationdate as expiryDate");
        SELECT("lots_on_hand.quantityonhand as lotsOnHand");
        FROM("facilities");
        LEFT_OUTER_JOIN("geographic_zones zone on facilities.geographiczoneid = zone.id");
        LEFT_OUTER_JOIN("geographic_zones parent_zone on zone.parentid = parent_zone.id");
        LEFT_OUTER_JOIN("stock_cards on stock_cards.facilityid = facilities.id");
        LEFT_OUTER_JOIN("products on stock_cards.productid = products.id");
        LEFT_OUTER_JOIN("lots_on_hand on lots_on_hand.stockcardid = stock_cards.id");
        LEFT_OUTER_JOIN("lots on lots.id = lots_on_hand.lotid");
        writePredicates(filter);

        return SQL();
    }

    private static void writePredicates(OverStockReportParam filter) {
        if(null != filter) {
            WHERE("lots_on_hand.createddate <= #{filterCriteria.endTime}");

            if(null != filter.getProvinceId()) {
                WHERE("parent_zone.id = #{filterCriteria.provinceId}");
            }
            if(null != filter.getDistrictId()) {
                WHERE("zone.id = #{filterCriteria.districtId}");
            }
            if(null != filter.getFacilityId()) {
                WHERE("facilities.id = #{filterCriteria.facilityId}");
            }
        }
    }
}