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
import org.openlmis.vaccine.domain.inventory.*;
import org.openlmis.vaccine.repository.inventory.VaccineInventoryConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Exposes the services for handling stock cards.
 */

@Service
@NoArgsConstructor
public class VaccineInventoryConfigurationService {

    @Autowired
    VaccineInventoryConfigurationRepository repository;

    public List<VaccineInventoryProductConfiguration> getAll() {
        return repository.getAll();
    }

    public VaccineInventoryProductConfiguration getById(Long id) {
        return repository.getById(id);
    }

    public void save(List<VaccineInventoryProductConfiguration> configurations) {

        for (VaccineInventoryProductConfiguration configuration : configurations) {
            Long productId = (configuration.getProductId() == null) ? configuration.getProduct().getId() : configuration.getProductId();
            configuration.setProductId(productId);
            if (configuration.getId() == null) {

                repository.insert(configuration);
            } else {
                repository.update(configuration);
            }
        }
    }

}
