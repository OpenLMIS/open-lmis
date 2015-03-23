

/*
 *This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.

  * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.

  * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.

  * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */
package org.openlmis.core.service;

import org.openlmis.core.domain.OrderQuantityAdjustmentType;
import org.openlmis.core.repository.OrderQuantityAdjustmentTypeRepository;
import org.openlmis.core.repository.mapper.OrderQuantityAdjustmentTypeMapper;
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
