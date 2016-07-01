package org.openlmis.report.util;

import org.openlmis.core.repository.mapper.MaterializedViewMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MaterializedViewRefresher {

    @Autowired
    private MaterializedViewMapper materializedViewMapper;

    public void refreshViews() {
        materializedViewMapper.refreshStockouts();
        materializedViewMapper.refreshCarryStartDates();
        materializedViewMapper.refreshWeeklyTracerSOH();
        materializedViewMapper.refreshPeriodMovements();
        materializedViewMapper.refreshDailyFullSoh();
    }
}
