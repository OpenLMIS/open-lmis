package org.openlmis.report.util;

import org.openlmis.core.repository.mapper.MaterializedViewMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MaterializedViewRefresher {

    @Autowired
    private MaterializedViewMapper materializedViewMapper;

    public void refreshDailyMaterializedViews() {
        materializedViewMapper.refreshPeriodMovements();
    }

    public void refreshHourlyMaterializedViews() {
        materializedViewMapper.refreshLotExpiryDate();
        materializedViewMapper.refreshDailyFullSOH();
        materializedViewMapper.refreshCMMEntries();
        materializedViewMapper.refreshStockouts();
        materializedViewMapper.refreshCarryStartDates();
        materializedViewMapper.refreshWeeklyTracerSOH();
        materializedViewMapper.refreshWeeklyNOSSOH();
    }
}
