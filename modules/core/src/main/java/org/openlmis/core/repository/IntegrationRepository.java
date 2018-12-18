package org.openlmis.core.repository;

import org.openlmis.core.domain.Soh;
import org.openlmis.core.domain.StockMovement;
import org.openlmis.core.repository.mapper.IntegrationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class IntegrationRepository {

    private IntegrationMapper integrationMapper;

    @Autowired
    public IntegrationRepository(IntegrationMapper integrationMapper) {
        this.integrationMapper = integrationMapper;
    }

    public Integer getPageInfo(String tableName, Date fromStartDate) {
        return integrationMapper.getPageInfo(tableName, fromStartDate);
    }

    public List<Soh> getSohByDate(Date fromStartDate, int everyPageCount, int startPosition) {
        return integrationMapper.getSohByDate(fromStartDate, everyPageCount, startPosition);
    }

    public List<StockMovement> getStockMovementsByDate(Date fromStartDate, int count, int startPosition) {
        return integrationMapper.getStockMovementsByDate(fromStartDate, count, startPosition);
    }
}
