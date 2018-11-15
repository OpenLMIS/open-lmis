package org.openlmis.report.generator.impl;

import org.openlmis.report.generator.AbstractExpiryProductsReportGenerator;
import org.openlmis.report.model.params.StockReportParam;
import org.springframework.stereotype.Component;

@Component(value = "expiredProductsReport")
public class ExpiredProductsReportGenerator extends AbstractExpiryProductsReportGenerator {

    @Override
    protected void setFilterCondition(StockReportParam filterCriteria) {
        filterCriteria.setFilterCondition(new StockReportParam.FilterCondition() {
            @Override
            public String getCondition() {
                return "lots.expirationdate <= #{filterCriteria.endTime} and v.valuecolumn is not null and  v.valuecolumn != '0'";
            }
        });
    }
}