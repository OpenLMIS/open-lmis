package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.openlmis.report.builder.ProductLotInfoQueryBuilder;
import org.openlmis.report.model.dto.ProductLotInfo;
import org.openlmis.report.model.params.StockReportParam;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductLotInfoMapper {

    @SelectProvider(type = ProductLotInfoQueryBuilder.class, method = "get")
    List<ProductLotInfo> getProductLotInfoList(@Param("filterCriteria") StockReportParam filterCriteria);
}