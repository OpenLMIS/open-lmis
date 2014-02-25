package org.openlmis.report.service.lookup;

import org.openlmis.report.mapper.lookup.DashboardMapper;
import org.openlmis.report.model.dto.ItemFillRate;
import org.openlmis.report.model.dto.OrderFillRate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
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

    public List<ItemFillRate> getItemFillRate(Long geographicZoneId, Long periodId, Long facilityId, List<Long> productListId){

        return dashboardMapper.getItemFillRate(geographicZoneId, periodId, facilityId, getCommaSeparatedIds(productListId));
    }

    public OrderFillRate getOrderFillRate(Long geographicZoneId, Long periodId, Long facilityId, List<Long> productListId){

        return dashboardMapper.getOrderFillRate(geographicZoneId, periodId, facilityId, getCommaSeparatedIds(productListId));
    }
}
