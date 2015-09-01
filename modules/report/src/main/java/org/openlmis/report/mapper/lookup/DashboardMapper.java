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
}

