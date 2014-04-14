package org.openlmis.report.service;

import org.openlmis.report.mapper.lookup.DashboardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * User: Issa
 * Date: 4/11/14
 * Time: 3:53 PM
 */
@Service
public class DashboardBatchDataUpdateExecutorService {

    @Autowired
    private DashboardMapper mapper;

    @Scheduled(cron = "${batch.job.dashboard.data.update.schedule}")
    public void startNightlyDashboardDataUpdate(){
        mapper.startDashboardDataBatchUpdate();
    }

}
