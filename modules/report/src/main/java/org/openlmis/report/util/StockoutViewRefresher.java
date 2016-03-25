package org.openlmis.report.util;

import org.openlmis.core.repository.mapper.StockoutViewMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StockoutViewRefresher {

    @Autowired
    private StockoutViewMapper stockoutViewMapper;

    public void refreshStockoutView() {
        stockoutViewMapper.refresh();
    }
}
