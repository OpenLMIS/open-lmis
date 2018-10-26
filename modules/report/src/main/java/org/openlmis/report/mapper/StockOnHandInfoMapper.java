package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.openlmis.report.builder.DailyFullStockOnHandQueryBuilder;
import org.openlmis.report.model.dto.StockOnHandDto;
import org.openlmis.report.model.params.StockReportParam;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockOnHandInfoMapper {

    @SelectProvider(type = DailyFullStockOnHandQueryBuilder.class, method = "get")
    List<StockOnHandDto> getStockOnHandInfoList(@Param("filterCriteria") StockReportParam filterCriteria);
}
