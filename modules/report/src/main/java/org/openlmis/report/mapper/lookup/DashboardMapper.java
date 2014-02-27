package org.openlmis.report.mapper.lookup;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.report.model.dto.ItemFillRate;
import org.openlmis.report.model.dto.OrderFillRate;
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
}
