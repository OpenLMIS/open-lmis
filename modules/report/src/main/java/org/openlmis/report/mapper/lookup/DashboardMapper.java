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

    @Select("select 45 as fillRate, 'Product 1' as product\n" +
            "UNION ALL\n" +
            "select 65 , 'Product 2' \n" +
            "UNION ALL\n" +
            "select -75 , 'Product 3' \n" +
            "UNION ALL\n" +
            "select 85 , 'Product 4';")
    List<ItemFillRate> getItemFillRate();

    @Select("select order_fill_rate as fillRate from dw_order_fill_rate_vw where periodid = #{periodId} and geographiczoneid = #{geographiczoneid} and facilityid = #{facilityId}")
    OrderFillRate getOrderFillRate(@Param("geographiczoneid") Long geographiczoneid, @Param("periodId")  Long periodId, @Param("facilityId")  Long facilityId);
}
