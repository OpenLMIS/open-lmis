package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.openlmis.report.builder.ProductLotInfoQueryBuilder;
import org.openlmis.report.model.dto.ProductLotInfo;
import org.openlmis.report.model.params.OverStockReportParam;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductLotInfoMapper {

    @SelectProvider(type = ProductLotInfoQueryBuilder.class, method = "getProductLotInfo")
    List<ProductLotInfo> getProductLotInfoList(@Param("filterCriteria") OverStockReportParam filterCriteria);
}