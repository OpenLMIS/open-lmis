package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.core.domain.Soh;
import org.openlmis.core.domain.StockMovement;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface IntegrationMapper {

    @Select("SELECT count(1) FROM ${tableName} WHERE modifieddate >= #{fromStartDate}")
    Integer getPageInfo(@Param(value = "tableName") String tableName, @Param(value = "fromStartDate") Date fromStartDate);


    @Select("SELECT * FROM stock_on_hand_product_vw WHERE modifieddate >= #{fromStartDate} " +
            "limit #{everyPageCount} offset #{startPosition}")
    List<Soh> getSohByDate(@Param(value = "fromStartDate") Date fromStartDate,
                           @Param(value = "everyPageCount") int everyPageCount,
                           @Param(value = "startPosition") int startPosition);

    @Select("SELECT * from vw_stock_movements_integration WHERE modifieddate >= #{fromStartDate} limit #{everyPageCount} offset #{startPosition}")
    List<StockMovement> getStockMovementsByDate(
            @Param(value = "fromStartDate") Date fromStartDate,
            @Param(value = "everyPageCount") int everyPageCount,
            @Param(value = "startPosition") int startPosition);
}
