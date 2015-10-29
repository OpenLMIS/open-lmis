package org.openlmis.vaccine.repository.inventory;

import lombok.NoArgsConstructor;
import org.openlmis.vaccine.domain.inventory.StockMovement;
import org.openlmis.vaccine.repository.mapper.inventory.StockMovementMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class StockMovementRepository {
    @Autowired
    StockMovementMapper mapper;
    public Integer insert(StockMovement stockMovement){
            return mapper.Insert(stockMovement);
    }

    public Integer update(StockMovement stockMovement){
        return mapper.update(stockMovement);
    }

    public StockMovement getLastStock(){
        return mapper.getLastInsertedStock();
    }
}
