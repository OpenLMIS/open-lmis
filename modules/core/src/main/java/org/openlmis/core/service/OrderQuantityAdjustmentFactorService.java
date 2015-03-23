package org.openlmis.core.service;/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 *   Copyright © 2013 VillageReach
 *
 *   This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *    
 *   This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *   You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

import org.openlmis.core.domain.OrderQuantityAdjustmentFactor;
import org.openlmis.core.domain.OrderQuantityAdjustmentType;
import org.openlmis.core.repository.OrderQuantityAdjustmentFactorRepository;
import org.openlmis.core.repository.OrderQuantityAdjustmentTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderQuantityAdjustmentFactorService {
    @Autowired
    private OrderQuantityAdjustmentFactorRepository quantityAdjustmentFactorRepository;


    public void addOrderQuantityAdjustmentFactor(OrderQuantityAdjustmentFactor orderQuantityAdjustmentFactor) {
        this.quantityAdjustmentFactorRepository.addOrderQuantityAdjustmentFactor(orderQuantityAdjustmentFactor);

    }

    public void updateOrderQuantityAdjustmentFactor(OrderQuantityAdjustmentFactor orderQuantityAdjustmentFactor) {
        this.quantityAdjustmentFactorRepository.updateOrderQuantityAdjustmentFactor(orderQuantityAdjustmentFactor);
    }

    public void deleteOrderQuantityAdjustmentFactor(OrderQuantityAdjustmentFactor orderQuantityAdjustmentFactor) {
        this.quantityAdjustmentFactorRepository.deleteOrderQuantityAdjustmentFactor(orderQuantityAdjustmentFactor);
    }

    public List<OrderQuantityAdjustmentFactor> loadOrderQuantityAdjustmentFactor() {
        return  this.quantityAdjustmentFactorRepository.loadOrderQuantityAdjustmentFactorList();
    }
}
