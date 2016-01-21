/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.vaccine.service.inventory;

import lombok.NoArgsConstructor;
import org.openlmis.stockmanagement.domain.Lot;
import org.openlmis.stockmanagement.repository.LotRepository;
import org.openlmis.vaccine.repository.inventory.VaccineInventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@NoArgsConstructor
public class VaccineInventoryService {

    @Autowired
    VaccineInventoryRepository repository;

    @Autowired
    LotRepository lotRepository;

    public List<Lot> getLotsByProductId(Long productId) {
        return repository.getLotsByProductId(productId);
    }

    public Lot insertLot(Lot lot) {
        return lotRepository.getOrCreateLot(lot);
    }
}
