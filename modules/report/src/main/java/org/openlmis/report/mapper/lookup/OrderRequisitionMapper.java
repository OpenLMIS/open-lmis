package org.openlmis.report.mapper.lookup;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.builder.OrderRequisitionBuilder;
import org.openlmis.report.model.ReportParameter;
import org.openlmis.report.model.report.vaccine.OrderRequisition;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface OrderRequisitionMapper {

    @SelectProvider(type=OrderRequisitionBuilder.class, method="getQuery")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=10,timeout=0,useCache=true,flushCache=true)
    public List<OrderRequisition> getReport(@Param("filterCriteria") ReportParameter filterCriteria,
                                            @Param("SortCriteria") Map<String, String[]> SortCriteria,
                                            @Param("RowBounds") RowBounds rowBounds);
}
