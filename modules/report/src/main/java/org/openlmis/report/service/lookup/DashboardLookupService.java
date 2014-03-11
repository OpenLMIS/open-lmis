package org.openlmis.report.service.lookup;

import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.report.mapper.lookup.DashboardMapper;
import org.openlmis.report.model.dto.ItemFillRate;
import org.openlmis.report.model.dto.OrderFillRate;
import org.openlmis.report.model.dto.ShipmentLeadTime;
import org.openlmis.report.model.dto.StockingInfo;
import org.openlmis.report.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * User: Issa
 * Date: 2/18/14
 * Time: 5:32 PM
 */
@Service
public class DashboardLookupService {

    @Autowired
    DashboardMapper dashboardMapper;

    private String  getCommaSeparatedIds(List<Long> idList){

        return idList == null ? "{}" : idList.toString().replace("[", "{").replace("]", "}");
    }

    public List<ItemFillRate> getItemFillRate(Long geographicZoneId, Long periodId, Long facilityId, Long programId, List<Long> productListId){

        return dashboardMapper.getItemFillRate(geographicZoneId, periodId, facilityId, programId, getCommaSeparatedIds(productListId));
    }

    public OrderFillRate getOrderFillRate(Long geographicZoneId, Long periodId, Long facilityId, Long programId){

        return dashboardMapper.getOrderFillRate(geographicZoneId, periodId, facilityId, programId);
    }

    public List<ShipmentLeadTime> getShipmentLeadTime(Long geographicZoneId, Long periodId, Long programId){
        return dashboardMapper.getShipmentLeadTime(geographicZoneId,periodId,programId);

    }

    public List<StockingInfo> getStockEfficiencyData(Long geographicZoneId, Long periodId, Long programId, List<Long> productListId){
        return dashboardMapper.getStockEfficiencyData(geographicZoneId, periodId, programId, getCommaSeparatedIds(productListId));

    }
    public List<StockingInfo> getStockEfficiencyDetailData(Long geographicZoneId, Long periodId, Long programId, List<Long> productListId){
        return dashboardMapper.getStockEfficiencyDetailData(geographicZoneId, periodId, programId, getCommaSeparatedIds(productListId));

    }

}
