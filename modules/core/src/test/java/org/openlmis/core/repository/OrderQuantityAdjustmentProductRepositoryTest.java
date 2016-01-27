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

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.*;
import org.openlmis.core.repository.mapper.OrderQuantityAdjustmentProductMapper;
import org.openlmis.db.categories.UnitTests;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class OrderQuantityAdjustmentProductRepositoryTest {

    @Mock
    private OrderQuantityAdjustmentProductMapper adjustmentProductMapper;

    @InjectMocks
    private OrderQuantityAdjustmentProductRepository adjustmentProductRepository;

    @Test
    public void shouldGetAll() throws Exception {
        Product product = new Product();
        Facility facility = new Facility(10L);
        product.setId(20L);
        OrderQuantityAdjustmentProduct adjustmentProduct = new OrderQuantityAdjustmentProduct();
        adjustmentProduct.setAdjustmentFactor(new OrderQuantityAdjustmentFactor("adj-factor","adj-factor",1,true));
        adjustmentProduct.setAdjustmentType(new OrderQuantityAdjustmentType("adj-type","adj-type",1));
        adjustmentProduct.setFacility(facility);
        adjustmentProduct.setProduct(product);
        adjustmentProduct.setMinMOS(5L);
        adjustmentProduct.setMaxMOS(15L);
        List<OrderQuantityAdjustmentProduct> adjustmentProductList = new ArrayList<>(1);
        adjustmentProductList.add(adjustmentProduct);
        when(adjustmentProductMapper.getAll()).thenReturn(adjustmentProductList);

        List<OrderQuantityAdjustmentProduct> fetchedAdjustment = adjustmentProductRepository.getAll();

        verify(adjustmentProductMapper).getAll();
        assertThat(fetchedAdjustment, is(adjustmentProductList));

    }

    @Test
    public void shouldInsert() throws Exception {
        Product product = new Product();
        product.setId(20L);
        OrderQuantityAdjustmentProduct adjustmentProduct = new OrderQuantityAdjustmentProduct();
        adjustmentProduct.setAdjustmentFactor(new OrderQuantityAdjustmentFactor("adj-factor","adj-factor",1,true));
        adjustmentProduct.setAdjustmentType(new OrderQuantityAdjustmentType("adj-type","adj-type",1));
        adjustmentProduct.setFacility(new Facility(10L));
        adjustmentProduct.setProduct(product);
        adjustmentProduct.setMinMOS(5L);
        adjustmentProduct.setMaxMOS(15L);
        adjustmentProductRepository.insert(adjustmentProduct);
        verify(adjustmentProductMapper).insert(adjustmentProduct);
    }

    @Test
    public void shouldGetByProductAndFacility() throws Exception {
        Product product = new Product();
        Facility facility = new Facility(10L);
        product.setId(20L);
        OrderQuantityAdjustmentProduct adjustmentProduct = new OrderQuantityAdjustmentProduct();
        adjustmentProduct.setAdjustmentFactor(new OrderQuantityAdjustmentFactor("adj-factor","adj-factor",1,true));
        adjustmentProduct.setAdjustmentType(new OrderQuantityAdjustmentType("adj-type","adj-type",1));
        adjustmentProduct.setFacility(facility);
        adjustmentProduct.setProduct(product);
        adjustmentProduct.setMinMOS(5L);
        adjustmentProduct.setMaxMOS(15L);
        when(adjustmentProductMapper.getByProductAndFacility(product.getId(), facility.getId())).thenReturn(adjustmentProduct);

        OrderQuantityAdjustmentProduct fetchedAdjustment = adjustmentProductRepository.getByProductAndFacility(product.getId(), facility.getId());

        verify(adjustmentProductMapper).getByProductAndFacility(product.getId(), facility.getId());
        assertThat(fetchedAdjustment, is(adjustmentProduct));
    }
}