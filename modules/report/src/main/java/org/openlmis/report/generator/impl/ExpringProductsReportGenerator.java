package org.openlmis.report.generator.impl;

import org.openlmis.report.generator.AbstractExpiryProductsReportGenerator;
import org.openlmis.report.model.dto.StockProductDto;
import org.openlmis.report.model.params.StockReportParam;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(value = "expiringProductsReport")
public class ExpringProductsReportGenerator extends AbstractExpiryProductsReportGenerator {

    @Override
    protected Map<String, Object> getQueryResult(Map<Object, Object> paraMap) {
        StockReportParam filterCriteria = new StockReportParam();
        filterCriteria.setValue(paraMap);
        filterCriteria.setFilterCondition(new StockReportParam.FilterCondition() {
            @Override
            public String getCondition() {
                String timeDifference = "date_part('day', lots.expirationdate::timestamp - #{filterCriteria.endTime})";
                String result = timeDifference + " < 90 and " + timeDifference + " > 0 and v.valuecolumn is not null and  v.valuecolumn != '0'";
                return result;
            }
        });
        List<StockProductDto> stockProductDtoList = simpleTableService.getStockProductData(filterCriteria);

        Map<String, Object> result = new HashMap<>();
        result.put(KEY_QUERY_RESULT, stockProductDtoList);
        return result;
    }

}