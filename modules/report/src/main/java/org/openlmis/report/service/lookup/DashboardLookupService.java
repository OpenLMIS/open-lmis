package org.openlmis.report.service.lookup;

import org.openlmis.report.mapper.lookup.DashboardMapper;
import org.openlmis.report.model.dto.ItemFillRate;
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

    public List<ItemFillRate> getItemFillRate(){
        return dashboardMapper.getItemFillRate();
    }
}
