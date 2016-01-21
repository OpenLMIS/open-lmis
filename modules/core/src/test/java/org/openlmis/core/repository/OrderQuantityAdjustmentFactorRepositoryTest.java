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
package org.openlmis.core.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.OrderQuantityAdjustmentFactor;
import org.openlmis.core.repository.mapper.OrderQuantityAdjustmentFactorMapper;
import org.openlmis.core.service.OrderQuantityAdjustmentFactorService;
import org.openlmis.db.categories.UnitTests;

import java.util.Date;

import static org.mockito.Mockito.verify;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class OrderQuantityAdjustmentFactorRepositoryTest {
    @Mock
    private OrderQuantityAdjustmentFactorMapper factorMapper;
    @InjectMocks
    private OrderQuantityAdjustmentFactorRepository factorRepository;

    @Test
    public void shouldAddOrderQuantityAdjustmentFactor() throws Exception {
        OrderQuantityAdjustmentFactor adjustmentFactor = new OrderQuantityAdjustmentFactor();

        adjustmentFactor.setName("Test Adjustment inserted");
        adjustmentFactor.setDisplayOrder(2);
        adjustmentFactor.setDescription("Test Adjustment inserted Description");
        adjustmentFactor.setCreatedDate(new Date());
        factorRepository.addOrderQuantityAdjustmentFactor(adjustmentFactor);
        verify(factorMapper).insert(adjustmentFactor);


    }

    @Test
    public void shouldUpdate() throws Exception {
        OrderQuantityAdjustmentFactor adjustmentFactor = new OrderQuantityAdjustmentFactor();

        adjustmentFactor.setName("Test Adjustment inserted");
        adjustmentFactor.setDisplayOrder(2);
        adjustmentFactor.setDescription("Test Adjustment inserted Description");
        adjustmentFactor.setCreatedDate(new Date());
        factorRepository.updateOrderQuantityAdjustmentFactor(adjustmentFactor);
        verify(factorMapper).update(adjustmentFactor);

    }

    @Test
    public void shouldGetById() throws Exception {
        factorRepository.loadOrderQuantityAdjustmentFactorDetail(1l);
        verify(factorMapper).getById(1l);

    }

    @Test
    public void shouldSearch() throws Exception {
        factorRepository.searchAdjustmentFactor("");
        verify(factorMapper).searchAdjustmentFactor("");

    }

    @Test
    public void shouldDelete() throws Exception {
        OrderQuantityAdjustmentFactor adjustmentFactor = new OrderQuantityAdjustmentFactor();
        factorRepository.deleteOrderQuantityAdjustmentFactor(adjustmentFactor);
        verify(factorMapper).delete(adjustmentFactor);

    }


}

