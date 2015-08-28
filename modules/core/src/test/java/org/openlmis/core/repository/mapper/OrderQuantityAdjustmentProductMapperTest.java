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

package org.openlmis.core.repository.mapper;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.*;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
@Category(IntegrationTests.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class OrderQuantityAdjustmentProductMapperTest {

    @Autowired
    private OrderQuantityAdjustmentProductMapper mapper;

    @Test
    public void shouldGetAll() throws Exception {
        Product product = new Product();
        product.setId(20L);
        Facility facility = new Facility(10L);
        OrderQuantityAdjustmentFactor factor = new OrderQuantityAdjustmentFactor();
        factor.setId(1L);
        OrderQuantityAdjustmentType type = new OrderQuantityAdjustmentType();
        type.setId(1L);
        OrderQuantityAdjustmentProduct adjustmentProduct = new OrderQuantityAdjustmentProduct();
        adjustmentProduct.setAdjustmentFactor(factor);
        adjustmentProduct.setAdjustmentType(type);
        adjustmentProduct.setFacility(facility);
        adjustmentProduct.setProduct(product);
        adjustmentProduct.setMinMOS(5L);
        adjustmentProduct.setMaxMOS(15L);

       /* mapper.insert(adjustmentProduct);

        List<OrderQuantityAdjustmentProduct> fetchedResult = mapper.getAll();

        assertThat(fetchedResult.size(), is(1));*/

    }

    @Test
    public void shouldInsert() throws Exception {
        Product product = new Product();
        product.setId(20L);
        Facility facility = new Facility(10L);
        OrderQuantityAdjustmentFactor factor = new OrderQuantityAdjustmentFactor();
        factor.setId(1L);
        OrderQuantityAdjustmentType type = new OrderQuantityAdjustmentType();
        type.setId(1L);
        OrderQuantityAdjustmentProduct adjustmentProduct = new OrderQuantityAdjustmentProduct();
        adjustmentProduct.setAdjustmentFactor(factor);
        adjustmentProduct.setAdjustmentType(type);
        adjustmentProduct.setFacility(facility);
        adjustmentProduct.setProduct(product);
        adjustmentProduct.setMinMOS(5L);
        adjustmentProduct.setMaxMOS(15L);

       /* mapper.insert(adjustmentProduct);

        OrderQuantityAdjustmentProduct fetchedResult = mapper.getByProductAndFacility(product.getId(), facility.getId());

        assertThat(fetchedResult.getMaxMOS(), is(adjustmentProduct.getMaxMOS()));*/
    }

    @Test
    public void shouldGetByProductAndFacility() throws Exception {
        Product product = new Product();
        product.setId(20L);
        Facility facility = new Facility(10L);
        OrderQuantityAdjustmentFactor factor = new OrderQuantityAdjustmentFactor();
        factor.setId(1L);
        OrderQuantityAdjustmentType type = new OrderQuantityAdjustmentType();
        type.setId(1L);
        OrderQuantityAdjustmentProduct adjustmentProduct = new OrderQuantityAdjustmentProduct();
        adjustmentProduct.setAdjustmentFactor(factor);
        adjustmentProduct.setAdjustmentType(type);
        adjustmentProduct.setFacility(facility);
        adjustmentProduct.setProduct(product);
        adjustmentProduct.setMinMOS(5L);
        adjustmentProduct.setMaxMOS(15L);

        /*mapper.insert(adjustmentProduct);

        OrderQuantityAdjustmentProduct fetchedResult = mapper.getByProductAndFacility(product.getId(), facility.getId());

        assertThat(fetchedResult.getMaxMOS(), is(adjustmentProduct.getMaxMOS()));*/
    }
}