package org.openlmis.vaccine.repository.Inventory;


import lombok.NoArgsConstructor;
import org.openlmis.vaccine.domain.inventory.StockMovementLineItemExt;
import org.openlmis.vaccine.repository.mapper.inventory.StockMovementLineItemExtMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class StockMovementLineItemExtRepository {
   @Autowired
    StockMovementLineItemExtMapper extMapper;
    public Integer insert(StockMovementLineItemExt itemExt) {
       return extMapper.insert(itemExt);
   }


}
