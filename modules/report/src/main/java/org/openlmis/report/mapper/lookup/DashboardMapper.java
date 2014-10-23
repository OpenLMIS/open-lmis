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

/**
 * User: Issa
 * Date: 2/18/14
 * Time: 5:32 PM
 */
@Repository
public interface DashboardMapper {

    @Select("select order_fill_rate as fillRate, primaryname as product from dw_product_fill_rate_vw where programid = #{programId} and periodid = #{periodId}  and facilityid = #{facilityId} and productid = ANY(#{products}::int[])")
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

   @Select("select d.programId,d.periodId,d.productId, d.geographicZoneId, d.geographiczonename as location, count(*) totalStockOut\n" +
           "from dw_orders d \n" +
           "where d.stockedOutInPast=true \n" +
           "and d.programId = #{programId} \n" +
           "and d.periodId = #{periodId} \n" +
           "and d.productId = #{productId}\n" +
           "AND d.geographiczoneId in (select geographiczoneid from fn_get_user_geographiczone_children(#{userId}::int,#{zoneId}::int))\n" +
           "group by d.programId,d.periodId,d.productId,d.geographicZoneId,d.geographiczonename \n" +
           "order by d.programId,d.periodId,d.productId,d.geographicZoneId, d.geographiczonename")

   List<StockOut> getStockOutFacilities(@Param("userId") Long userId, @Param("periodId")  Long periodId, @Param("programId") Long programId , @Param("productId") Long productId, @Param("zoneId") Long zoneId);

   @Select("select d.facilityId,d.facilityCode ,d.facilityName,d.programId,d.periodId,d.productId,d.productFullName as product, d.suppliedInPast,d.geographiczonename as location,d.mosSuppliedInPast \n" +
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

    @Select("WITH reportingPerf as(\n" +
            "            select distinct dw.reporting AS status,uf.facilityid,uf.facilityname,uf.geographiczonename,\n" +
            "            (select count(*) > 0 from users where users.active = true and users.facilityId = uf.facilityid) as hasContacts\n" +
            "            from vw_user_geo_facilities uf\n" +
            "            join dw_orders dw on dw.facilityid = uf.facilityid\n" +
            "            where uf.geographiczoneid in (select geographiczoneid from fn_get_user_geographiczone_children(#{userId}::int,#{zoneId}::int))\n" +
            "            and dw.programid = #{programId} and dw.periodid= #{periodId}  and uf.userid = #{userId}\n" +
            "            UNION ALL\n" +
            "               SELECT \n" +
            "                'N' AS Status, \n" +
            "                facilityid, \n" +
            "                facilityname, \n" +
            "                geographiczonename, \n" +
            "                 (select count(*) > 0 from users where users.active = true and users.facilityId = vw_expected_facilities.facilityid) as hasContacts \n" +
            "               FROM \n" +
            "                vw_expected_facilities \n" +
            "               WHERE \n" +
            "                programid = #{programId} \n" +
            "               AND periodid =  #{periodId} \n" +
            "               AND facilityid not in (select facilityid from dw_orders where programid = #{programId} and periodid = #{periodId})\n" +
            "               AND geographiczoneid IN ( \n" +
            "                SELECT \n" +
            "                 geographiczoneid \n" +
            "                FROM \n" +
            "                 fn_get_user_geographiczone_children (#{userId}::int,#{zoneId}::int) \n" +
            "               ) )\n" +
            "            SELECT status, count(*) as total\n" +
            "            FROM reportingPerf\n" +
            "            GROUP BY status")
    List<HashMap> getReportingPerformance(@Param("userId") Long userId,@Param("periodId")  Long periodId, @Param("programId") Long programId, @Param("zoneId") Long zoneId);

    @Select("WITH reportingPerf as(\n" +
            "select distinct dw.reporting AS status,uf.facilityid,uf.facilityname as name,uf.geographiczonename as district,\n" +
            "(select count(*) > 0 from users where users.active = true and users.facilityId = uf.facilityid) as hasContacts\n" +
            "from vw_user_geo_facilities uf\n" +
            "join dw_orders dw on dw.facilityid = uf.facilityid\n" +
            "where uf.geographiczoneid in (select geographiczoneid from fn_get_user_geographiczone_children(#{userId}::int,#{zoneId}::int))\n" +
            "and dw.programid = #{programId} and dw.periodid= #{periodId} and uf.userid = #{userId}\n" +
            "UNION ALL\n" +
            "select 'N' AS status, facilityid,facilityname,geographiczonename,\n" +
            "(select count(*) > 0 from users where users.active = true and users.facilityId = vw_user_geo_facilities.facilityid) as hasContacts\n" +
            "from vw_user_geo_facilities \n" +
            "where facilityid not in (select facilityid from dw_orders where programid = #{programId} and periodid = #{periodId} \n" +
            "and geographiczoneid in (select geographiczoneid from fn_get_user_geographiczone_children( #{userId}::int,#{zoneId}::int)))\n" +
            "and userid =  #{userId} and  geographiczoneid in (select geographiczoneid from fn_get_user_geographiczone_children( #{userId}::int,#{zoneId}::int)))\n" +
            "SELECT * \n" +
            "FROM reportingPerf\n" +
            "WHERE status = #{status}")
    List<ReportingPerformance> getReportingPerformanceDetail(@Param("userId") Long userId, @Param("periodId") Long periodId, @Param("programId") Long programId, @Param("zoneId") Long zoneId, @Param("status") String status);

}

