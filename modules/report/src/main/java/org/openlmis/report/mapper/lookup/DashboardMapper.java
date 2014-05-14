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
           "    COALESCE(MAX(CASE WHEN s.stocking = 'A' THEN s.stockingStat END),0) AS adequatelyStocked,\n" +
           "    COALESCE(MAX(CASE WHEN s.stocking = 'O' THEN s.stockingStat END),0) AS overStocked,\n" +
           "    COALESCE(MAX(CASE WHEN s.stocking = 'S' THEN s.stockingStat END),0) AS stockedOut,\n" +
           "    COALESCE(MAX(CASE WHEN s.stocking = 'U' THEN s.stockingStat END),0) AS understocked\n" +
           "    FROM(SELECT  programid, periodid, productid, primaryname as product, stocking, count(stocking) stockingStat\n" +
           "              FROM dw_product_facility_stock_info_vw\n" +
           "             where programid = #{programId} and periodid = #{periodId} \n" +
           "              AND CASE WHEN #{rgroupId} ='{}' THEN requisitionGroupId = requisitionGroupId ELSE requisitionGroupId =  ANY( #{rgroupId}::int[]) END\n" +
           "              AND productid = ANY(#{products}::int[])\n" +
           "              GROUP BY programid,periodid, productid, primaryname,stocking) s\n" +
           "   GROUP BY programid,periodid, productid, product")

   List<StockingInfo> getStockEfficiencyData(@Param("periodId")  Long periodId, @Param("programId") Long programId ,@Param("rgroupId") String requisitionGroupId, @Param("products") String productIds);

   @Select("SELECT requisitionGroupId,programid,periodid,productid, primaryname as product,facilityId, facilityname as facility,amc,soh,mos,stocking \n" +
           "FROM dw_product_facility_stock_info_vw \n" +
           "WHERE programid = #{programId} and periodid = #{periodId}\n" +
           "  AND CASE WHEN #{rgroupId} ='{}' THEN requisitionGroupId = requisitionGroupId ELSE requisitionGroupId =  ANY(#{rgroupId}::int[]) END\n" +
           "  AND productid = ANY(#{products}::int[])")

   List<StockingInfo> getStockEfficiencyDetailData(@Param("periodId")  Long periodId, @Param("programId") Long programId ,@Param("rgroupId") String requisitionGroupId, @Param("products") String productIds);

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
    @Select("SELECT DISTINCT s.id,statics_value staticsValue,s.description,supervisorynodeid,alerttype,display_section displaySection,email,sms,detail_table detailTable, sms_msg_template_key smsMessageTemplateKey, email_msg_template_key emailMessageTemplateKey\n" +
            "FROM alert_summary s\n" +
            "INNER JOIN alerts a on s.alertTypeId= a.alertType\n" +
            "INNER JOIN vw_user_supervisorynodes sn ON sn.id = s.supervisorynodeid\n" +
            "WHERE sn.userid = #{userId}\n" +
            "AND s.programid = #{programId}\n"+
            "AND (sn.id = #{supervisoryNodeId} OR sn.parentId = #{supervisoryNodeId}) \n")

    List<AlertSummary> getAlerts(@Param("userId") Long userId, @Param("supervisoryNodeId") Long supervisoryNodeId, @Param("programId")Long programId);


    @SelectProvider(type = DashboardNotificationQueryBuilder.class, method = "getNotificationDetails")
    public List<HashMap> getNotificationDetails(@Param("tableName") String tableName,@Param("alertId") Long id);

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
    public String getPeriod(@Param("id")Long id);
}

