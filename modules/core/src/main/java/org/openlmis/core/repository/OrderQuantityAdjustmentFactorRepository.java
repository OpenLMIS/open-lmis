package org.openlmis.core.repository;/*
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
import org.openlmis.core.repository.mapper.OrderQuantityAdjustmentFactorMapper;
import org.openlmis.core.repository.mapper.OrderQuantityAdjustmentTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderQuantityAdjustmentFactorRepository {
    @Autowired
    private OrderQuantityAdjustmentFactorMapper quantityAdjustmentFactorMapper;


    public void addOrderQuantityAdjustmentFactor(OrderQuantityAdjustmentFactor quantityAdjustmentFactor) {
        this.quantityAdjustmentFactorMapper.insert(quantityAdjustmentFactor);

    }

    public void updateOrderQuantityAdjustmentFactor(OrderQuantityAdjustmentFactor quantityAdjustmentFactor) {
        this.quantityAdjustmentFactorMapper.update(quantityAdjustmentFactor);
    }

    public void deleteOrderQuantityAdjustmentFactor(OrderQuantityAdjustmentFactor quantityAdjustmentFactor) {
        this.quantityAdjustmentFactorMapper.delete(quantityAdjustmentFactor);
    }

    public List<OrderQuantityAdjustmentFactor> loadOrderQuantityAdjustmentFactorList() {
        return this.quantityAdjustmentFactorMapper.getAll();
    }

    public OrderQuantityAdjustmentFactor loadOrderQuantityAdjustmentFactorDetail(long id) {
        return  this.quantityAdjustmentFactorMapper.getById(id);
    }

    public List<OrderQuantityAdjustmentFactor> searchAdjustmentFactor(String param) {
       return this.quantityAdjustmentFactorMapper.searchAdjustmentFactor(param);
    }
}
