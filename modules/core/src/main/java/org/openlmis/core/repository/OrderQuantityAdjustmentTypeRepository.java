package org.openlmis.core.repository;/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 *   Copyright © 2013 VillageReach
 *
 *   This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *    
 *   This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *   You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

import org.openlmis.core.domain.OrderQuantityAdjustmentType;
import org.openlmis.core.repository.mapper.OrderQuantityAdjustmentTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderQuantityAdjustmentTypeRepository {
    @Autowired
    private OrderQuantityAdjustmentTypeMapper quantityAdjustmentTypeMapper;


    public void addOrderQuantityAdjustmentType(OrderQuantityAdjustmentType quantityAdjustmentType) {
        this.quantityAdjustmentTypeMapper.insert(quantityAdjustmentType);

    }

    public void updateOrderQuantityAdjustmentType(OrderQuantityAdjustmentType quantityAdjustmentType) {
        this.quantityAdjustmentTypeMapper.update(quantityAdjustmentType);
    }

    public void deleteOrderQuantityAdjustmentType(OrderQuantityAdjustmentType quantityAdjustmentType) {
        this.quantityAdjustmentTypeMapper.delete(quantityAdjustmentType);
    }

    public List<OrderQuantityAdjustmentType> loadOrderQuantityAdjustmentTypeList() {
      return  this.quantityAdjustmentTypeMapper.getAll();
    }

    public OrderQuantityAdjustmentType loadOrderQuantityAdjustmentType(Long id) {
        return  this.quantityAdjustmentTypeMapper.getById(id);
    }

    public List<OrderQuantityAdjustmentType> searchForQuantityAdjustmentType(String param) {
        return  this.quantityAdjustmentTypeMapper.search(param);
    }
}
