/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openlmis.core.service;

import org.openlmis.core.domain.OrderQuantityAdjustmentType;
import org.openlmis.core.repository.OrderQuantityAdjustmentTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderQuantityAdjustmentTypeService {
    @Autowired
    private OrderQuantityAdjustmentTypeRepository quantityAdjustmentTypeRepository;


    public void addOrderQuantityAdjustmentType(OrderQuantityAdjustmentType quantityAdjustmentType) {
        this.quantityAdjustmentTypeRepository.addOrderQuantityAdjustmentType(quantityAdjustmentType);

    }

    public void updateOrderQuantityAdjustmentType(OrderQuantityAdjustmentType quantityAdjustmentType) {
        this.quantityAdjustmentTypeRepository.updateOrderQuantityAdjustmentType(quantityAdjustmentType);
    }

    public void deleteOrderQuantityAdjustmentType(OrderQuantityAdjustmentType quantityAdjustmentType) {
        this.quantityAdjustmentTypeRepository.deleteOrderQuantityAdjustmentType(quantityAdjustmentType);

    }

    public List<OrderQuantityAdjustmentType> loadOrderQuantityAdjustmentTypeList() {
        return  this.quantityAdjustmentTypeRepository.loadOrderQuantityAdjustmentTypeList();
    }

    public OrderQuantityAdjustmentType loadOrderQuantityAdjustmentType(Long id) {
        return  this.quantityAdjustmentTypeRepository.loadOrderQuantityAdjustmentType(id);
    }

    public List<OrderQuantityAdjustmentType> searchForQuantityAdjustmentType(String param) {
        List<OrderQuantityAdjustmentType> quantityAdjustmentTypeList=null;
        quantityAdjustmentTypeList=this.quantityAdjustmentTypeRepository.searchForQuantityAdjustmentType(param);
        return quantityAdjustmentTypeList;
    }
}
