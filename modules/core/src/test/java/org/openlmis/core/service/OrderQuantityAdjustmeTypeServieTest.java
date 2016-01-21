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
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.OrderQuantityAdjustmentType;
import org.openlmis.core.repository.OrderQuantityAdjustmentTypeRepository;
import org.openlmis.core.repository.mapper.OrderQuantityAdjustmentTypeMapper;
import org.openlmis.db.categories.UnitTests;

import java.util.Date;

import static org.mockito.Mockito.verify;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)

public class OrderQuantityAdjustmeTypeServieTest {
    @Mock
    private OrderQuantityAdjustmentTypeRepository adjustmentTypeRepository;
    @InjectMocks
    private OrderQuantityAdjustmentTypeService adjustmentTypeService;
    @Test
    public void shouldAddOrderQuantityAdjustmentType()throws  Exception{
        OrderQuantityAdjustmentType adjustmentType = new OrderQuantityAdjustmentType();

        adjustmentType.setName("Test Adjustment inserted");
        adjustmentType.setDisplayOrder(2);
        adjustmentType.setDescription("Test Adjustment inserted Description");
        adjustmentType.setCreatedDate(new Date());
        adjustmentTypeService.addOrderQuantityAdjustmentType(adjustmentType);
        verify(adjustmentTypeRepository).addOrderQuantityAdjustmentType(adjustmentType);


    }
    @Test
    public void shouldUpdate() throws Exception{
        OrderQuantityAdjustmentType adjustmentType = new OrderQuantityAdjustmentType();

        adjustmentType.setName("Test Adjustment inserted");
        adjustmentType.setDisplayOrder(2);
        adjustmentType.setDescription("Test Adjustment inserted Description");
        adjustmentType.setCreatedDate(new Date());
        adjustmentTypeService.updateOrderQuantityAdjustmentType(adjustmentType);
        verify(adjustmentTypeRepository).updateOrderQuantityAdjustmentType(adjustmentType);

    }
    @Test
    public void shouldGetById() throws Exception{
        adjustmentTypeService.loadOrderQuantityAdjustmentType(1l);
        verify(adjustmentTypeRepository).loadOrderQuantityAdjustmentType(1l);

    }
    @Test
    public void shouldSearch() throws Exception{
        adjustmentTypeService.searchForQuantityAdjustmentType("");
        verify(adjustmentTypeRepository).searchForQuantityAdjustmentType("");

    }
    @Test
    public void shouldDelete() throws Exception{
        OrderQuantityAdjustmentType adjustmentType = new OrderQuantityAdjustmentType();
        adjustmentTypeService.deleteOrderQuantityAdjustmentType(adjustmentType);
        verify(adjustmentTypeRepository).deleteOrderQuantityAdjustmentType(adjustmentType);

    }

}
