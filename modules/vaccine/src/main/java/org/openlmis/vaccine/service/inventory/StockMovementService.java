package org.openlmis.vaccine.service.inventory;

import org.openlmis.vaccine.repository.inventory.StockMovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StockMovementService {

    @Autowired
    StockMovementRepository repository;

}
