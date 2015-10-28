/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.report.mapper.lookup;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.openlmis.report.builder.DashboardNotificationQueryBuilder;
import org.openlmis.report.model.dto.*;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public interface DashboardMapper {

    @Select("select order_fill_rate as fillRate, primaryname as product from dw_product_fill_rate_vw where programid = #{programId} and periodid = #{periodId}  and facilityid = #{facilityId} and productid = ANY(#{products}::int[]) and quantityapproved > 0")
    List<ItemFillRate> getItemFillRate(@Param("periodId")  Long periodId, @Param("facilityId")  Long facilityId,@Param("programId") Long programId ,@Param("products") String productIds);

    @Select("select order_fill_rate as fillRate from dw_order_fill_rate_vw where programid = #{programId} and periodid = #{periodId} and facilityid = #{facilityId}")
    OrderFillRate getOrderFillRate(@Param("periodId")  Long periodId, @Param("facilityId")  Long facilityId, @Param("programId") Long programId);

    @Select("WITH t as (\n" +
            "SELECT distinct dw_orders.programid, dw_orders.periodid,\n" +
            " facilities.name,facilities.code,dw_orders.facilityid,submitteddate,authorizeddate,inapprovaldate,approveddate,releaseddate\n" +
            "FROM dw_orders\n" +
            "JOIN facilities ON facilities.id = dw_orders.facilityid\n" +
            "WHERE dw_orders.status::text = 'RELEASED'::character varying::text\n" +
            "AND dw_orders.programid = #{programId} \n" +
            "AND dw_orders.periodid = #{periodId}\n" +
            "AND dw_orders.geographiczoneid in (select geographiczoneid from fn_get_user_geographiczone_children(#{userId}::int,#{zoneId}::int))\n" +
            ")\n" +
            "SELECT programid,periodid,name,code,sum(date_part('day',age(authorizeddate,submitteddate))) AS subToAuth,\n" +
            "sum(date_part('day',age(inapprovaldate,authorizeddate))) AS authToInApproval,\n" +
            "sum(date_part('day',age(approveddate,inapprovaldate))) AS inApprovalToApproved,\n" +
            "sum(date_part('day',age(releaseddate,approveddate))) AS approvedToReleased\n" +
            "FROM t \n" +
            "GROUP BY programid,periodid, name, code")
    List<ShipmentLeadTime> getShipmentLeadTime(@Param("userId") Long userId, @Param("periodId")  Long periodId, @Param("programId") Long programId, @Param("zoneId") Long zoneId);

   @Select("SELECT s.programid, s.periodid, s.productid, s.product,\n" +
           "COALESCE(MAX(CASE WHEN s.stocking = 'A' THEN s.stockingStat END),0) AS adequatelyStocked,\n" +
           "COALESCE(MAX(CASE WHEN s.stocking = 'O' THEN s.stockingStat END),0) AS overStocked,\n" +
           "COALESCE(MAX(CASE WHEN s.stocking = 'S' THEN s.stockingStat END),0) AS stockedOut,\n" +
           "COALESCE(MAX(CASE WHEN s.stocking = 'U' THEN s.stockingStat END),0) AS understocked\n" +
           "FROM(SELECT  programid, periodid, productid, primaryname as product, stocking, count(stocking) stockingStat\n" +
           "  FROM dw_product_facility_stock_info_vw\n" +
           "  where programid = #{programId} and periodid = #{periodId} \n" +
           "  AND geographiczoneId in (select geographiczoneid from fn_get_user_geographiczone_children(#{userId}::int,#{zoneId}::int))\n" +
           "  AND productid = ANY(#{products}::int[])\n" +
           "  GROUP BY programid,periodid, productid, primaryname,stocking) s\n" +
           "GROUP BY programid,periodid, productid, product")

   List<StockingInfo> getStockEfficiencyData(@Param("userId") Long userId, @Param("periodId")  Long periodId, @Param("programId") Long programId ,@Param("zoneId") Long zoneId, @Param("products") String productIds);

   @Select("SELECT requisitionGroupId,programid,periodid,productid, primaryname as product,facilityId, facilityname as facility,amc,soh,mos,stocking\n" +
           "FROM dw_product_facility_stock_info_vw \n" +
           "WHERE programid = #{programId} and periodid = #{periodId}\n" +
           "AND geographiczoneId in (select geographiczoneid from fn_get_user_geographiczone_children(#{userId}::int,#{zoneId}::int))\n" +
           "AND productid = ANY(#{products}::int[])")

   List<StockingInfo> getStockEfficiencyDetailData(@Param("userId") Long userId, @Param("periodId")  Long periodId, @Param("programId") Long programId ,@Param("zoneId") Long zoneId, @Param("products") String productIds);

   @Select("select d.programId,d.periodId,d.productId, d.geographicZoneId, d.geographiczonename as location, count(*) totalStockOut, (select count(f.*) from facilities f where f.geographiczoneid = d.geographicZoneId and f.active = TRUE and f.virtualFacility = FALSE) totalGeoFacility\n" +
           "from dw_orders d \n" +
           "where d.stockedOutInPast=true \n" +
           "and d.programId = #{programId} \n" +
           "and d.periodId = #{periodId} \n" +
           "and d.productId = #{productId}\n" +
           "AND d.geographiczoneId in (select geographiczoneid from fn_get_user_geographiczone_children(#{userId}::int,#{zoneId}::int))\n" +
           "group by d.programId,d.periodId,d.productId,d.geographicZoneId,d.geographiczonename \n" +
           "order by d.programId,d.periodId,d.productId,d.geographicZoneId, d.geographiczonename")

   List<StockOut> getStockOutFacilities(@Param("userId") Long userId, @Param("periodId")  Long periodId, @Param("programId") Long programId , @Param("productId") Long productId, @Param("zoneId") Long zoneId);

   @Select("select d.rnrid, d.facilityId,d.facilityCode ,d.facilityName,d.programId,d.periodId,d.productId,d.productFullName as product, d.suppliedInPast,d.geographiczonename as location,d.mosSuppliedInPast \n" +
           "from dw_orders d\n" +
           "where d.stockedOutInPast=true\n" +
           "and  d.programId = #{programId}\n" +
           "and d.periodId = #{periodId}\n" +
           "and d.productId = #{productId}\n" +
           "and d.geographiczoneid = #{zoneId} and d.geographiczoneid in (select geographiczoneid from vw_user_geographic_zones where userid = #{userId} ) ")

    List<StockOut> getStockOutFacilitiesForGeographicZone(@Param("userId") Long userId, @Param("periodId") Long periodId, @Param("programId") Long programId, @Param("productId") Long productId, @Param("zoneId") Long zoneId);
    @Select("SELECT  s.programId,s.periodId, sum (statics_value) staticsValue ,max(s.description) description,max(alerttype) alerttype,max(display_section) displaySection, max(detail_table) detailTable, max(sms_msg_template_key) smsMessageTemplateKey, max(email_msg_template_key) emailMessageTemplateKey\n" +
            "FROM alert_summary s\n" +
            "JOIN alerts a on s.alertTypeId = a.alertType\n" +
            "WHERE s.programId = #{programId}\n" +
            "AND s.periodId = #{periodId}\n" +
            "AND s.geographiczoneid in (select geographiczoneid from fn_get_user_geographiczone_children(#{userId}::int, #{zoneId}::int))\n" +
            "and a.alerttype <> 'FACILITY_STOCKED_OUT_OF_TRACER_PRODUCT'\n"+
            "group by s.programId,s.periodId,a.alerttype")

    List<AlertSummary> getAlerts(@Param("userId") Long userId, @Param("programId")Long programId,
                                 @Param("periodId") Long periodId, @Param("zoneId") Long zoneId);

    @Select("SELECT  s.programId,s.periodId,s.productId, sum (statics_value) staticsValue ,max(s.description) description,max(alerttype) alerttype,max(display_section) displaySection, max(detail_table) detailTable, max(sms_msg_template_key) smsMessageTemplateKey, max(email_msg_template_key) emailMessageTemplateKey\n" +
            "FROM alert_summary s\n" +
            "JOIN alerts a on s.alertTypeId = a.alertType\n" +
            "WHERE s.programId = #{programId}\n" +
            "AND s.periodId = #{periodId}\n" +
            "AND s.geographiczoneid in (select geographiczoneid from fn_get_user_geographiczone_children(#{userId}::int, #{zoneId}::int))\n" +
            "AND a.alerttype = 'FACILITY_STOCKED_OUT_OF_TRACER_PRODUCT'\n" +
            "group by s.programId,s.periodId,s.productId\n")

    List<AlertSummary> getStockedOutAlerts(@Param("userId") Long userId, @Param("programId")Long programId,
                                 @Param("periodId") Long periodId, @Param("zoneId") Long zoneId);


    @SelectProvider(type = DashboardNotificationQueryBuilder.class, method = "getNotificationDetails")
    public List<HashMap> getNotificationDetails(@Param("userId") Long userId, @Param("programId")Long programId,
                                                @Param("periodId") Long periodId, @Param("zoneId") Long zoneId,@Param("tableName") String tableName);

    @SelectProvider(type = DashboardNotificationQueryBuilder.class, method = "getStockedOutNotificationDetails")
    public List<HashMap> getStockedOutNotificationDetails(@Param("userId") Long userId, @Param("programId")Long programId,
                                                          @Param("periodId") Long periodId, @Param("zoneId") Long zoneId, @Param("productId") Long productId,@Param("tableName") String tableName);

    @Select("select a.*, ecs.value emailMessageTemplate,scs.value smsMessageTemplate\n" +
            "from alerts a\n" +
            "left outer join configuration_settings ecs on ecs.key = a.email_msg_template_key\n" +
            "left outer join configuration_settings scs on scs.key = a.sms_msg_template_key \n" +
            "where sms = true or email = true ")
    public List<AlertSummary> getNotificationAlerts();

    @Select("select * from fn_populate_dw_orders(1)")
    void startDashboardDataBatchUpdate();

    @Insert("insert into email_notifications(receiver,content,subject,sent) values(#{receiver},#{content},NULL,false);")
    void saveEmailNotification(@Param("receiver")String receiver, @Param("content") String content);


    @Insert("insert into sms(message,phonenumber,direction,sent) values(#{message},#{phonenumber},#{direction},false);")
    void saveSmsNotification(@Param("message")String message, @Param("phonenumber") String phonenumber, @Param("direction")String direction);

    @Select("select date_Part('year',startdate) from processing_periods where id = #{id}")
    public String getYearOfPeriodById(@Param("id")Long id);

    @Select("SELECT count(*) total, \n" +
            "(SELECT count(*) from requisitions where programid = #{programId} and periodid = #{periodId} \n" +
            "and facilityid in (select facilityid FROM vw_expected_facilities WHERE  programid = #{programId} AND periodid = #{periodId}) \n" +
            "and facilityid in (select facility_id from vw_user_facilities where user_id = #{userId} and program_id = #{programId} and district_id in (SELECT geographiczoneid FROM fn_get_user_geographiczone_children (#{userId}::int,#{zoneId}::int)))\n" +
            "and status not in ('INITIATED', 'SUBMITTED', 'SKIPPED') and emergency = false) as reporting\n" +
            "\n" +
            "FROM vw_expected_facilities WHERE  programid = #{programId} AND periodid = #{periodId} \n" +
            "and geographiczoneid in (SELECT geographiczoneid FROM fn_get_user_geographiczone_children (#{userId}::int,#{zoneId}::int));" )
    ReportingStatus getReportingPerformance(@Param("userId") Long userId,@Param("periodId")  Long periodId, @Param("programId") Long programId, @Param("zoneId") Long zoneId);

   @Select("WITH reportingFac as (SELECT facilityid from requisitions where programid = #{programId} and periodid = #{periodId} \n" +
           "and facilityid in (select facilityid FROM vw_expected_facilities WHERE  programid = #{programId} AND periodid = #{periodId}) \n" +
           "and facilityid in (select facility_id from vw_user_facilities where user_id = #{userId} and program_id = #{programId} and district_id in (SELECT geographiczoneid FROM fn_get_user_geographiczone_children (#{userId}::int,#{zoneId}::int)))\n" +
           "and status not in ('INITIATED', 'SUBMITTED', 'SKIPPED') and emergency = false)\n" +
           "SELECT facilityname as name, geographiczonename as district,\n" +
           "           (select count(*) > 0 from users where users.active = true and users.facilityId = f.id) as hasContacts ,\n" +
           "           #{status} as status\n" +
           "FROM vw_expected_facilities \n" +
           "JOIN facilities f on f.id = facilityid\n" +
           "WHERE  programid = #{programId} AND periodid = #{periodId} \n" +
           "and vw_expected_facilities.geographiczoneid in (SELECT geographiczoneid FROM fn_get_user_geographiczone_children (#{userId}::int,#{zoneId}::int))\n" +
           "and CASE WHEN #{status} = 'reporting' THEN facilityid  in (select facilityId from reportingFac) \n" +
           "         WHEN #{status} = 'nonReporting' THEN facilityid not in (select facilityId from reportingFac) END")
    List<ReportingPerformance> getReportingPerformanceDetail(@Param("userId") Long userId, @Param("periodId") Long periodId, @Param("programId") Long programId, @Param("zoneId") Long zoneId, @Param("status") String status);
    @Select("select fullname from products where id = #{id}")
    String getProductNameById(Long id);

    @Select("select name from processing_periods where id = #{id}")
    public String getPeriodName(@Param("id")Long id);


    /*@Select("SELECT p.id, (p.primaryname || ' ' || form.code || ' ' || p.strength || ' ' || du.code) as name, p.code\n" +
            "      FROM \n" +
            "          products as p \n" +
            "                join product_forms as form on form.id = p.formid \n" +
            "                join dosage_units as du on du.id = p.dosageunitid\n" +
            "            join program_products pp on p.id = pp.productId \n" +
            "        where pp.programId = #{programId} and pp.active = true  and p.tracer = true \n" +
            "    order by name \n" +
            "    limit #{limit}\n"
    )
    List<Product> getTracerProductsForProgram(@Param("programId")Long programId, @Param("limit")Long limit);*/

    @Select("select r as order, product_code, short_name as name,\n" +
            "beginning_balance,\n" +
            "quantity_received,\n" +
            "quantity_dispensed,\n" +
            "total_losses_and_adjustments,\n" +
            "stock_in_hand_facility,\n" +
            "stock_in_hand_upper,\n" +
            "COALESCE(stock_in_hand_facility, 0)+ COALESCE(stock_in_hand_upper, 0) as stock_in_hand_total,\n" +
            "amc,\n" +
            "quantity_requested,\n" +
            "calculated_order_quantity,\n" +
            "quantity_approved,\n" +
            "quantity_expired_facility,\n" +
            "quantity_expired_upper,\n" +
            "startdate,\n" +
            "period_short_name as period_name,\n" +
            "number_of_facilities_understocked,\n" +
            "number_of_facilities_adquatelystocked,\n" +
            "number_of_facilities_overstocked,\n" +
            "COALESCE(quantity_expired_facility,0)+COALESCE(quantity_expired_upper,0) as quantity_expired_total,\n" +
            "number_of_facilities_stocked_out_facility,\n" +
            "number_of_facilities_stocked_out_upper,\n" +
            "COALESCE(number_of_facilities_stocked_out_facility, 0) + COALESCE(number_of_facilities_stocked_out_upper, 0) as total_facilities_stocked_out,\n" +
            "COALESCE(quantity_lost_facility, 0) + COALESCE(quantity_lost_upper, 0) as total_quantity_lost,\n" +
            "COALESCE(quantity_damaged_facility, 0) + COALESCE(quantity_damaged_upper, 0) as total_quantity_damaged,\n" +
            "COALESCE(quantity_expired_facility, 0) + COALESCE(quantity_expired_upper, 0) as total_quantity_expired," +
            "price \n" +
            "from fn_get_dashboard_summary_data(#{programId}::integer, #{periodId}::integer, #{userId}::integer)")
    List<HashMap<String, Object>> getProgramPeriodTracerProductTrend(@Param("programId") Long programId, @Param("periodId") Long periodId, @Param("userId") Long userId);

    @Select("Select * from fn_get_dashboard_reporting_summary_data(#{programId}::integer, #{periodId}::integer, #{userId}::integer)")
    HashMap<String, Object> getDashboardReportingPerformance(@Param("programId") Long programId, @Param("periodId") Long periodId, @Param("userId") Long userId);

    @Select("SELECT\n" +
            "facilities.id facility_id,\n" +
            "facilities.name facility_name,\n" +
            "facility_types.id facility_type_id,\n" +
            "facility_types.name facility_type_name,\n" +
            "geographic_zones.name geographiczone_name,\n" +
            "geographic_zones.levelid, \n" +
            "productcode product_code, \n" +
            "processing_periods.startdate::date start_date,\n" +
            "requisition_line_items.stockinhand stock_in_hand,\n" +
            "COALESCE (requisition_line_items.previousstockinhand,0) previous_stock_in_hand,\n" +
            "requisition_line_items.stockoutdays stock_out_days,\n" +
            "requisition_line_items.quantitydispensed quantity_dispensed,\n" +
            "requisition_line_items.amc,\n" +
            "requisitions.id rnrid\n" +
            "from requisition_line_items\n" +
            "INNER JOIN requisitions ON requisition_line_items.rnrid = requisitions.id\n" +
            "INNER JOIN processing_periods ON processing_periods.id = requisitions.periodid\n" +
            "INNER JOIN facilities ON facilities.id = requisitions.facilityid\n" +
            "INNER JOIN facility_types ON facilities.typeid = facility_types.id\n" +
            "INNER JOIN geographic_zones ON facilities.geographiczoneid = geographic_zones.id\n" +
            "INNER JOIN products ON requisition_line_items.productcode= products.code \n" +
            "where requisitions.programid = #{programId} \n" +
            "and processing_periods.id = #{periodId}\n" +
            "and products.code = #{code} and requisition_line_items.stockinhand = 0 \n" +
            "order by geographic_zones.levelid")
    List<HashMap<String, Object>> getFacilityStockedOut(@Param("programId") Long programId, @Param("periodId") Long periodId, @Param("code") String code);



    @Select("select 'DISPENSED' indicator, a.r, a.productcode, a.shortname, a.geographiczonename, a.quantitydispensed indicator_value, a.price from fn_get_stock_summary_data_by_geozone(#{programId}::integer, #{periodId}::integer, 'DISPENSED', #{userId}::integer) a\n" +
            "union\n" +
            "select 'AMC' indicator, a.r, a.productcode, a.shortname, a.geographiczonename, a.amc indicator_value, a.price from fn_get_stock_summary_data_by_geozone(#{programId}::integer, #{periodId}::integer, 'AMC', #{userId}::integer) a\n" +
            "union\n" +
            "select 'EXPIRED' indicator, a.r, a.productcode, a.shortname, a.geographiczonename,  a.quantityexpired  indicator_value, a.price from fn_get_stock_summary_data_by_geozone(#{programId}::integer, #{periodId}::integer, 'EXPIRED', #{userId}::integer) a\n" +
            "union\n" +
            "select 'DAMAGED' indicator, a.r, a.productcode, a.shortname, a.geographiczonename,  a.quantitydamaged indicator_value, a.price from fn_get_stock_summary_data_by_geozone(#{programId}::integer, #{periodId}::integer, 'DAMAGED', #{userId}::integer) a\n" +
            "union\n" +
            "select 'LOST' indicator, a.r, a.productcode, a.shortname, a.geographiczonename,     a.quantitylost  indicator_value, a.price from fn_get_stock_summary_data_by_geozone(#{programId}::integer, #{periodId}::integer, 'LOST', #{userId}::integer) a\n" +
            "union\n" +
            "select 'STOCKEDOUT' indicator, a.r, a.productcode, a.shortname, a.geographiczonename, a.stockedout indicator_value, a.price from fn_get_stock_summary_data_by_geozone(#{programId}::integer, #{periodId}::integer, 'STOCKEDOUT', #{userId}::integer) a\n" +
            "union\n" +
            "select 'OVERSTOCKED' indicator, a.r, a.productcode, a.shortname, a.geographiczonename, a.overstocked indicator_value, a.price from fn_get_stock_summary_data_by_geozone(#{programId}::integer, #{periodId}::integer, 'OVERSTOCKED', #{userId}::integer) a\n" +
            "union\n" +
            "select 'UNDERSTOCKED' indicator, a.r, a.productcode, a.shortname, a.geographiczonename, a.understocked indicator_value, a.price from fn_get_stock_summary_data_by_geozone(#{programId}::integer, #{periodId}::integer, 'UNDERSTOCKED', #{userId}::integer) a\n" +
            "union\n" +
            "select 'ADEQUATELYSTOCKED' indicator, a.r, a.productcode, a.shortname, a.geographiczonename, a.adequatelystocked indicator_value, a.price from fn_get_stock_summary_data_by_geozone(#{programId}::integer, #{periodId}::integer, 'ADEQUATELYSTOCKED', #{userId}::integer) a\n" +
            "order by 1,3,2" )
    List<HashMap<String, Object>> getDistrictStockSummary(@Param("programId") Long programId, @Param("periodId") Long periodId, @Param("userId") Long userId);

    @Select("select * from (\n" +
            "select 'ONHAND' indicator, a.r, a.productcode, a.shortname, a.facilityname, a.stockinhand indicator_value, a.price from fn_get_stock_summary_data_by_facility(#{programId}::integer, #{periodId}::integer, 'ONHAND',  #{userId}::integer) a\n" +
            "union\n" +
            "select 'DISPENSED' indicator, a.r, a.productcode, a.shortname, a.facilityname, a.quantitydispensed indicator_value, a.price from fn_get_stock_summary_data_by_facility(#{programId}::integer, #{periodId}::integer, 'DISPENSED',  #{userId}::integer) a\n" +
            "union\n" +
            "select 'AMC' indicator, a.r, a.productcode, a.shortname, a.facilityname, a.amc indicator_value, a.price from fn_get_stock_summary_data_by_facility(#{programId}::integer, #{periodId}::integer, 'AMC',  #{userId}::integer) a\n" +
            "union\n" +
            "select 'EXPIRED' indicator, a.r, a.productcode, a.shortname, a.facilityname,   a.quantityexpired  indicator_value, a.price from fn_get_stock_summary_data_by_facility(#{programId}::integer, #{periodId}::integer, 'EXPIRED',  #{userId}::integer) a\n" +
            "union\n" +
            "select 'DAMAGED' indicator, a.r, a.productcode, a.shortname, a.facilityname,     quantitydamaged indicator_value, a.price from fn_get_stock_summary_data_by_facility(#{programId}::integer, #{periodId}::integer, 'DAMAGED',  #{userId}::integer) a\n" +
            "union\n" +
            "select 'LOST' indicator, a.r, a.productcode, a.shortname, a.facilityname,        quantitylost indicator_value, a.price from fn_get_stock_summary_data_by_facility(#{programId}::integer, #{periodId}::integer, 'LOST',  #{userId}::integer) a\n" +
            "union\n" +
            "select 'FILLRATE' indicator, a.r, a.productcode, a.shortname, a.facilityname, case when a.fillrate < 0 then 100 else round(a.fillrate::integer,2) end indicator_value, a.price from fn_get_stock_summary_data_by_facility(#{programId}::integer, #{periodId}::integer, 'FILLRATE',  #{userId}::integer) a\n" +
            ") a\n" +
            "order by 1,3,2")
    List<HashMap<String, Object>> getFacilityStockSummary(@Param("programId") Long programId, @Param("periodId") Long periodId, @Param("userId") Long userId);


}

