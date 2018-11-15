package org.openlmis.report.generator.impl;

import org.openlmis.report.generator.AbstractExpiryProductsReportGenerator;
import org.openlmis.report.model.params.StockReportParam;
import org.springframework.stereotype.Component;

@Component(value = "expiringProductsReport")
public class ExpringProductsReportGenerator extends AbstractExpiryProductsReportGenerator {

    @Override
    protected void setFilterCondition(StockReportParam filterCriteria) {
        filterCriteria.setFilterCondition(new StockReportParam.FilterCondition() {
            @Override
            public String getCondition() {
                String timeDifference = "date_part('day', lots.expirationdate::timestamp - #{filterCriteria.endTime})";
                String result = timeDifference + " < 90 and " + timeDifference + " > 0 and v.valuecolumn is not null and  v.valuecolumn != '0'";
                return result;
            }
        });
    }

}