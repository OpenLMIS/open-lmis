package org.openlmis.vaccine.service.Inventory;

import org.openlmis.vaccine.repository.Inventory.StockMovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StockMovementService {

    @Autowired
    StockMovementRepository repository;

}
