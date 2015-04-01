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
import org.openlmis.core.domain.OrderQuantityAdjustmentProduct;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.OrderQuantityAdjustmentFactorMapper;
import org.openlmis.core.repository.mapper.OrderQuantityAdjustmentProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderQuantityAdjustmentProductRepository {
    @Autowired
    private OrderQuantityAdjustmentProductMapper orderQuantityAdjustmentProductMapper;

    public List<OrderQuantityAdjustmentProduct> getAll() {
       return this.orderQuantityAdjustmentProductMapper.getAll();
    }

    public void insert(OrderQuantityAdjustmentProduct adjustmentProduct) {
        try {
            orderQuantityAdjustmentProductMapper.insert(adjustmentProduct);
        } catch (DuplicateKeyException duplicateKeyException) {
            throw new DataException("error.duplicate.product.code");
        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
            String errorMessage = dataIntegrityViolationException.getMessage().toLowerCase();
            if (errorMessage.contains("foreign key") || errorMessage.contains("violates not-null constraint")) {
                throw new DataException("error.reference.data.missing");
            } else {
                throw new DataException("error.incorrect.length");
            }
        }
    }

    public OrderQuantityAdjustmentProduct getByProductAndFacility(Long productId, Long facilityId){
        return this.orderQuantityAdjustmentProductMapper.getByProductAndFacility(productId, facilityId);
    }

}
