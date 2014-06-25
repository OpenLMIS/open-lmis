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

    @Select("SELECT dw_orders.programid, dw_orders.periodid,\n" +
            "  facilities.name,facilities.code,dw_orders.facilityid,\n" +
            "  sum(date_part('day',age(authorizeddate,submitteddate))) AS subToAuth,\n" +
            "  sum(date_part('day',age(inapprovaldate,authorizeddate))) AS authToInApproval,\n" +
            "  sum(date_part('day',age(approveddate,inapprovaldate))) AS inApprovalToApproved,\n" +
            "  sum(date_part('day',age(releaseddate,approveddate))) AS approvedToReleased\n" +
            "   FROM dw_orders\n" +
            "   JOIN facilities ON facilities.id = dw_orders.facilityid\n" +
            "   INNER JOIN requisition_group_program_schedules rgp ON rgp.programid = dw_orders.programId and rgp.requisitionGroupId = dw_orders.requisitionGroupId\n" +
            "   INNER JOIN requisition_group_members rgm ON dw_orders.facilityid = rgm.facilityid AND dw_orders.requisitiongroupid = rgm.requisitiongroupid\n" +
            "   WHERE dw_orders.status::text = 'RELEASED'::character varying::text\n" +
            "   AND dw_orders.programid = #{programId} \n" +
            "   AND dw_orders.periodid = #{periodId}\n" +
            "   AND CASE WHEN #{rgroupId}='{}' THEN dw_orders.requisitionGroupId = dw_orders.requisitionGroupId ELSE dw_orders.requisitionGroupId = ANY(#{rgroupId}::int[]) END\n" +
            "  GROUP BY dw_orders.programid,  dw_orders.periodid, facilities.name, facilities.code, dw_orders.facilityid\n" +
            " ORDER BY dw_orders.programid,  dw_orders.periodid, facilities.name, facilities.code, dw_orders.facilityid;\n")
    List<ShipmentLeadTime> getShipmentLeadTime(@Param("periodId")  Long periodId, @Param("programId") Long programId, @Param("rgroupId") String requisitionGroupId);

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

   @Select("select d.requisitionGroupId,d.programId,d.periodId,d.productId,d.requisitionGroupName as location,count(*) totalStockOut\n" +
           "from dw_orders d \n" +
           "INNER JOIN requisition_group_program_schedules rgs ON rgs.requisitionGroupId = d.requisitionGroupId and rgs.programId = d.programId \n"+
           "where d.stockedOutInPast=true \n" +
           "and d.programId = #{programId} \n" +
           "and d.periodId = #{periodId} \n" +
           "and d.productId = #{productId}\n" +
           "and CASE WHEN #{rgroupId} = '{}' THEN d.requisitionGroupId = d.requisitionGroupId ELSE d.requisitionGroupId = ANY(#{rgroupId}::int[]) END \n" +
           "group by d.requisitionGroupId,d.programId,d.periodId,d.productId,d.requisitionGroupName \n" +
           "order by d.requisitionGroupId,d.programId,d.periodId,d.productId,d.requisitionGroupName ")

   List<StockOut> getStockOutFacilities(@Param("periodId")  Long periodId, @Param("programId") Long programId , @Param("productId") Long productId, @Param("rgroupId") String requisitionGroupId);

   @Select("select d.facilityId,d.facilityCode ,d.facilityName,d.requisitionGroupId,d.programId,d.periodId,d.productId,d.productFullName as product, d.suppliedInPast,d.requisitionGroupName as location,d.mosSuppliedInPast \n" +
           "from dw_orders d \n" +
           "INNER JOIN requisition_group_program_schedules rgs ON rgs.requisitionGroupId = d.requisitionGroupId and rgs.programId = d.programId \n"+
           "where d.stockedOutInPast=true \n" +
           "and  d.programId = #{programId} \n" +
           "and d.periodId = #{periodId} \n" +
           "and d.productId = #{productId}\n" +
           "and d.requisitionGroupId = #{rgroupId} ")

    List<StockOut> getStockOutFacilitiesForRequisitionGroup(@Param("periodId")  Long periodId, @Param("programId") Long programId , @Param("productId") Long productId, @Param("rgroupId") Long requisitionGroupId);
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
            "left outer join configuration_settings scs on scs.key = a.sms_msg_template_key ")
    public List<AlertSummary> getNotificationAlerts();

    @Select("select * from fn_populate_dw_orders(1)")
    void startDashboardDataBatchUpdate();

    @Insert("insert into email_notifications(receiver,content,subject,sent) values(#{receiver},#{content},NULL,false);")
    void saveEmailNotification(@Param("receiver")String receiver, @Param("content") String content);


    @Insert("insert into sms(message,phonenumber,direction,sent) values(#{message},#{phonenumber},#{direction},false);")
    void saveSmsNotification(@Param("message")String message, @Param("phonenumber") String phonenumber, @Param("direction")String direction);

    @Select("select date_Part('year',startdate) from processing_periods where id = #{id}")
    public String getYearOfPeriodById(@Param("id")Long id);

    @Select("WITH reporting as (select fn_get_reporting_status_by_facilityid_programid_and_periodid(f.id,s.programid,pp.id) status, f.*\n" +
            "from facilities f\n" +
            "join geographic_zones gz on gz.id = f.geographiczoneid\n" +
            "join requisition_group_members m on f.id = m.facilityId\n" +
            "join requisition_group_program_schedules s on s.requisitionGroupId = m.requisitionGroupId and s.programId = #{programId}\n" +
            "join processing_periods pp on pp.scheduleId = s.scheduleId and pp.id = #{periodId}\n" +
            "where gz.id in  (select geographiczoneid from fn_get_user_geographiczone_children(#{userId}::int,#{zoneId}::int))\n" +
            "AND f.enabled = true\n" +
            "order by status)\n" +
            "select status, count(*) total\n" +
            "from reporting\n" +
            "group by status")
    List<HashMap> getReportingPerformance(@Param("userId") Long userId,@Param("periodId")  Long periodId, @Param("programId") Long programId, @Param("zoneId") Long zoneId);

    @Select("with t as (select fn_get_reporting_status_by_facilityid_programid_and_periodid(f.id,s.programid,pp.id) as status, gz.name as district, (select count(*) > 0 from users where users.active = true and users.facilityId = f.id) as hasContacts, f.*\n" +
            "from facilities f\n" +
            "join geographic_zones gz on gz.id = f.geographicZoneId\n" +
            "join requisition_group_members m on f.id = m.facilityId\n" +
            "join requisition_group_program_schedules s on s.requisitionGroupId = m.requisitionGroupId and s.programId = #{programId}\n" +
            "join processing_periods pp on pp.scheduleId = s.scheduleId and pp.id = #{periodId}\n" +
            "where \n" +
            " gz.id in  (select geographiczoneid from fn_get_user_geographiczone_children(#{userId}::int,#{zoneId}::int))\n" +
            "  AND f.enabled = true\n" +
            " order by status)\n" +
            " select * \n" +
            " from t\n" +
            " where status = #{status}")
    List<ReportingPerformance> getReportingPerformanceDetail(@Param("userId") Long userId, @Param("periodId") Long periodId, @Param("programId") Long programId, @Param("zoneId") Long zoneId, @Param("status") String status);

}

