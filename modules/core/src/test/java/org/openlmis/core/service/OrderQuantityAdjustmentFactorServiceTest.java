
/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */
package org.openlmis.core.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.OrderQuantityAdjustmentFactor;
import org.openlmis.core.domain.OrderQuantityAdjustmentType;
import org.openlmis.core.repository.OrderQuantityAdjustmentFactorRepository;
import org.openlmis.core.repository.OrderQuantityAdjustmentTypeRepository;
import org.openlmis.db.categories.UnitTests;

import java.util.Date;

import static org.mockito.Mockito.verify;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class OrderQuantityAdjustmentFactorServiceTest {
    @Mock
    private OrderQuantityAdjustmentFactorRepository factorRepository;
    @InjectMocks
    private OrderQuantityAdjustmentFactorService factorService;
    @Test
    public void shouldAddOrderQuantityAdjustmentFactor()throws  Exception{
        OrderQuantityAdjustmentFactor adjustmentFactor = new OrderQuantityAdjustmentFactor();

        adjustmentFactor.setName("Test Adjustment inserted");
        adjustmentFactor.setDisplayOrder(2);
        adjustmentFactor.setDescription("Test Adjustment inserted Description");
        adjustmentFactor.setCreatedDate(new Date());
        factorService.addOrderQuantityAdjustmentFactor(adjustmentFactor);
        verify(factorRepository).addOrderQuantityAdjustmentFactor(adjustmentFactor);


    }
    @Test
    public void shouldUpdate() throws Exception{
        OrderQuantityAdjustmentFactor adjustmentFactor = new OrderQuantityAdjustmentFactor();

        adjustmentFactor.setName("Test Adjustment inserted");
        adjustmentFactor.setDisplayOrder(2);
        adjustmentFactor.setDescription("Test Adjustment inserted Description");
        adjustmentFactor.setCreatedDate(new Date());
        factorService.updateOrderQuantityAdjustmentFactor(adjustmentFactor);
        verify(factorRepository).updateOrderQuantityAdjustmentFactor(adjustmentFactor);

    }
    @Test
    public void shouldGetById() throws Exception{
        factorService.loadOrderQuantityAdjustmentFactorDetail(1l);
        verify(factorRepository).loadOrderQuantityAdjustmentFactorDetail(1l);

    }
    @Test
    public void shouldSearch() throws Exception{
        factorService.searchAdjustmentFactor("");
        verify(factorRepository).searchAdjustmentFactor("");

    }
    @Test
    public void shouldDelete() throws Exception{
        OrderQuantityAdjustmentFactor adjustmentFactor = new OrderQuantityAdjustmentFactor();
        factorService.deleteOrderQuantityAdjustmentFactor(adjustmentFactor);
        verify(factorRepository).deleteOrderQuantityAdjustmentFactor(adjustmentFactor);

    }

}
