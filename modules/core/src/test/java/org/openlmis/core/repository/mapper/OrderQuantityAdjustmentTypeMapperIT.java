/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */
package org.openlmis.core.repository.mapper;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.OrderQuantityAdjustmentFactor;
import org.openlmis.core.domain.OrderQuantityAdjustmentType;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
@Transactional
public class OrderQuantityAdjustmentTypeMapperIT {
    @Autowired
    private OrderQuantityAdjustmentTypeMapper adjustmentTypeMapper;
    @Autowired
    private QueryExecutor queryExecutor;

    @Test
    public void insertTest() throws SQLException {
        OrderQuantityAdjustmentType adjustmentType = new OrderQuantityAdjustmentType();

        adjustmentType.setName("Test Adjustment 1");
        adjustmentType.setDisplayOrder(2);
        adjustmentType.setDescription("Test Adjustment Description1");
        adjustmentType.setCreatedDate(new Date());

//        adjustmentType.setCreatedBy(Long.valueOf(2));
        this.adjustmentTypeMapper.insert(adjustmentType);

        ResultSet resultSet = queryExecutor.execute("SELECT * FROM order_quantity_adjustment_types WHERE name = ?", adjustmentType.getName());
        resultSet.next();
        assertThat(resultSet.getString("name"), is(adjustmentType.getName()));
        assertThat(resultSet.getString("description"), is(adjustmentType.getDescription()));

    }

    @Test
    public void updateTest() throws SQLException {
        OrderQuantityAdjustmentType adjustmentType = new OrderQuantityAdjustmentType();

        adjustmentType.setName("Test Adjustment inserted");
        adjustmentType.setDisplayOrder(2);
        adjustmentType.setDescription("Test Adjustment inserted Description");
        adjustmentType.setCreatedDate(new Date());

//        adjustmentType.setCreatedBy(Long.valueOf(2));
        this.adjustmentTypeMapper.insert(adjustmentType);
        adjustmentType.setName("Test Adjustment updated");

        adjustmentType.setDisplayOrder(4);
        adjustmentType.setDescription("Test Adjustment updated Description");
        this.adjustmentTypeMapper.update(adjustmentType);
        OrderQuantityAdjustmentType updatedAdjustmentType1= this.adjustmentTypeMapper.getById(adjustmentType.getId());
        assertThat(updatedAdjustmentType1.getName(), is(adjustmentType.getName()));
        assertThat(updatedAdjustmentType1.getDescription(), is(adjustmentType.getDescription()));
        assertThat(updatedAdjustmentType1.getDisplayOrder(), is(adjustmentType.getDisplayOrder()));

    }
    @Test
    public void testListAll(){
        List<OrderQuantityAdjustmentType> orderQuantityAdjustmentTypeList= this.adjustmentTypeMapper.getAll();
        Assert.assertNotNull(orderQuantityAdjustmentTypeList);
        Assert.assertTrue(orderQuantityAdjustmentTypeList.size()>0);

    }
}
