package org.openlmis.vaccine.repository.inventory;

import lombok.NoArgsConstructor;
import org.openlmis.vaccine.domain.inventory.StockMovementLineItem;
import org.openlmis.vaccine.repository.mapper.inventory.StockMovementLineItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class StockMovementLineItemRepository {

    @Autowired
    StockMovementLineItemMapper mapper;

    public Integer insert(StockMovementLineItem item){
        return mapper.Insert(item);
    }

    public Integer update(StockMovementLineItem item){
        //TODO update
        return null;
    }
}
