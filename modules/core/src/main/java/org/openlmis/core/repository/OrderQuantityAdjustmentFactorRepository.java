/*
 *This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.

  * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.

  * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.

  * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.core.repository;

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
