package org.openlmis.report.util;

import org.openlmis.core.repository.mapper.MaterializedViewMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MaterializedViewRefresher {
    private static final Logger log = LoggerFactory.getLogger(MaterializedViewRefresher.class);

    @Autowired
    private MaterializedViewMapper materializedViewMapper;

    public void refreshDailyMaterializedViews() {
        log.info("start to refresh daily materialized view");
        materializedViewMapper.refreshPeriodMovements();
    }

    public void refreshHourlyMaterializedViews() {
        log.info("start to refresh hourly materialized view");
        materializedViewMapper.refreshLotExpiryDate();
        materializedViewMapper.refreshDailyFullSOH();
        materializedViewMapper.refreshCMMEntries();
        materializedViewMapper.refreshStockouts();
        materializedViewMapper.refreshCarryStartDates();
        materializedViewMapper.refreshWeeklyTracerSOH();
        materializedViewMapper.refreshWeeklyNOSSOH();
    }
}
