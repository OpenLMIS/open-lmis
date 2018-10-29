package org.openlmis.report.generator.impl;

import org.openlmis.report.generator.AbstractReportModelGenerator;
import org.openlmis.report.model.dto.StockProductDto;
import org.openlmis.report.model.params.StockReportParam;
import org.openlmis.report.service.SimpleTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(value = "expiredProductsReport")
public class ExpiredProductsReportGenerator extends AbstractReportModelGenerator {

    private final static String KEY_QUERY_RESULT = "KEY_QUERY_RESULT";

    @Autowired
    private SimpleTableService simpleTableService;

    @Override
    protected Object getReportHeaders(Map<Object, Object> paraMap, Map<String, Object> queryResult) {
        return null;
    }

    @Override
    protected Object getReportContent(Map<Object, Object> paraMap, Map<String, Object> queryResult) {
        return null;
    }

    @Override
    protected List<Map<String, String>> getReportMergedRegions(Map<Object, Object> paraMap, Map<String, Object> queryResult) {
        return null;
    }

    @Override
    protected Map<String, Object> getQueryResult(Map<Object, Object> paraMap) {
        StockReportParam filterCriteria = new StockReportParam();
        filterCriteria.setValue(paraMap);
        filterCriteria.setFilterCondition(new StockReportParam.FilterCondition() {
            @Override
            public String getCondition() {
                return "lots.expirationdate <= #{filterCriteria.endTime}";
            }
        });
        List<StockProductDto> stockProductDtoList = simpleTableService.getStockProductData(filterCriteria);

        Map<String, Object> result = new HashMap<>();
        result.put(KEY_QUERY_RESULT, stockProductDtoList);
        return result;
    }

    @Override
    protected Object reportDataForFrontEnd(Map<Object, Object> paraMap) {
        return getQueryResult(paraMap).get(KEY_QUERY_RESULT);
    }
}