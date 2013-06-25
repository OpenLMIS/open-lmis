package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.openlmis.report.mapper.lookup.SupplyLineListMapper;
import org.openlmis.report.model.dto.SupplyLineList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mahmed
 * Date: 6/19/13
 * Time: 3:58 PM
 * To change this template use File | Settings | File Templates.
 */
@NoArgsConstructor
@Service
public class SupplyLineListDataProvider {

    @Autowired
   private SupplyLineListMapper supplyLineListMapper;

    public List<SupplyLineList> getAll(){
        return supplyLineListMapper.getAll();
    }
}
