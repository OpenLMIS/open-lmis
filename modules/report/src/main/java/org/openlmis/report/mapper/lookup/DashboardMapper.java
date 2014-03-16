package org.openlmis.report.mapper.lookup;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.report.model.dto.ItemFillRate;
import org.openlmis.report.model.dto.OrderFillRate;
import org.openlmis.report.model.dto.ShipmentLeadTime;
import org.openlmis.report.model.dto.StockingInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * User: Issa
 * Date: 2/18/14
 * Time: 5:32 PM
 */
@Repository
public interface DashboardMapper {

    @Select("select order_fill_rate as fillRate, primaryname as product from dw_product_fill_rate_vw where programid = #{programId} and periodid = #{periodId} and geographicZoneid = #{geographicZoneid} and facilityid = #{facilityId} and productid = ANY(#{products}::int[])")
    List<ItemFillRate> getItemFillRate(@Param("geographicZoneid") Long geographicZoneid, @Param("periodId")  Long periodId, @Param("facilityId")  Long facilityId,@Param("programId") Long programId , @Param("products") String productIds);

    @Select("select order_fill_rate as fillRate from dw_order_fill_rate_vw where programid = #{programId} and periodid = #{periodId} and geographicZoneid = #{geographicZoneid} and facilityid = #{facilityId}")
    OrderFillRate getOrderFillRate(@Param("geographicZoneid") Long geographicZoneid, @Param("periodId")  Long periodId, @Param("facilityId")  Long facilityId, @Param("programId") Long programId);

    @Select("select code,name,subToAuth,authToInApproval,inApprovalToApproved,approvedToReleased from dw_product_lead_time_vw where programid = #{programId} and periodid = #{periodId} and geographicZoneid = #{geographicZoneid}")
    List<ShipmentLeadTime> getShipmentLeadTime(@Param("geographicZoneid") Long geographicZoneid, @Param("periodId")  Long periodId, @Param("programId") Long programId);

   @Select("select geographicZoneid,programid,periodid,productid, primaryname as product,adequatelyStocked,overStocked,stockedOut,understocked \n"+
           "from dw_product_stock_efficiency_statics_vw where programid = #{programId} and periodid = #{periodId} and geographicZoneid = #{geographicZoneid}  and productid = ANY(#{products}::int[])")

   List<StockingInfo> getStockEfficiencyData(@Param("geographicZoneid") Long geographicZoneid, @Param("periodId")  Long periodId, @Param("programId") Long programId , @Param("products") String productIds);

    @Select("select geographicZoneid,programid,periodid,productid, primaryname as product,facilityId, facilityname as facility,amc,soh,mos,stocking \n"+
            "from dw_product_facility_stock_info_vw where programid = #{programId} and periodid = #{periodId} and geographicZoneid = #{geographicZoneid}  and productid = ANY(#{products}::int[])")

    List<StockingInfo> getStockEfficiencyDetailData(@Param("geographicZoneid") Long geographicZoneid, @Param("periodId")  Long periodId, @Param("programId") Long programId , @Param("products") String productIds);

}

