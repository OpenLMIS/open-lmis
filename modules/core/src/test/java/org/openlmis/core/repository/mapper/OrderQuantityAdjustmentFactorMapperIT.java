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

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.OrderQuantityAdjustmentFactor;
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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
@Transactional
public class OrderQuantityAdjustmentFactorMapperIT {
    @Autowired
    private OrderQuantityAdjustmentFactorMapper adjustmentFactorMapper;
    @Autowired
    private QueryExecutor queryExecutor;

    @Test
    public void insertTest() throws SQLException {
        OrderQuantityAdjustmentFactor adjustmentFactor = new OrderQuantityAdjustmentFactor();

        adjustmentFactor.setName("Test Adjustment 1");
        adjustmentFactor.setDisplayOrder(2);
        adjustmentFactor.setDescription("Test Adjustment Description1");
        adjustmentFactor.setBasedOnFormula(true);
        adjustmentFactor.setCreatedDate(new Date());

//        adjustmentType.setCreatedBy(Long.valueOf(2));
        this.adjustmentFactorMapper.insert(adjustmentFactor);

        ResultSet resultSet = queryExecutor.execute("SELECT * FROM order_quantity_adjustment_factors WHERE name = ?", adjustmentFactor.getName());
        resultSet.next();
        assertThat(resultSet.getString("name"), is(adjustmentFactor.getName()));
        assertThat(resultSet.getString("description"), is(adjustmentFactor.getDescription()));
        assertThat(String.valueOf(resultSet.getBoolean("basedOnFormula")), is(String.valueOf(adjustmentFactor.isBasedOnFormula())));
        assertThat(String.valueOf(resultSet.getInt("displayOrder")), is(String.valueOf(adjustmentFactor.getDisplayOrder())));

    }

    @Test
    public void updateTest() throws SQLException {
        OrderQuantityAdjustmentFactor adjustmentFactor = new OrderQuantityAdjustmentFactor();

        adjustmentFactor.setName("Test Adjustment inserted");
        adjustmentFactor.setDisplayOrder(2);
        adjustmentFactor.setDescription("Test Adjustment inserted Description");
        adjustmentFactor.setCreatedDate(new Date());
        adjustmentFactor.setBasedOnFormula(true);
//        adjustmentType.setCreatedBy(Long.valueOf(2));
        this.adjustmentFactorMapper.insert(adjustmentFactor);
        adjustmentFactor.setName("Test Adjustment updated");

        adjustmentFactor.setDisplayOrder(4);
        adjustmentFactor.setDescription("Test Adjustment updated Description");
        this.adjustmentFactorMapper.update(adjustmentFactor);
        OrderQuantityAdjustmentFactor updatedAdjustmentType1= this.adjustmentFactorMapper.getById(adjustmentFactor.getId());
        assertThat(updatedAdjustmentType1.getName(), is(updatedAdjustmentType1.getName()));
        assertThat(updatedAdjustmentType1.getDescription(), is(updatedAdjustmentType1.getDescription()));
        assertThat(updatedAdjustmentType1.getDisplayOrder(), is(updatedAdjustmentType1.getDisplayOrder()));

    }
    @Test
    public void testListAll(){
        List<OrderQuantityAdjustmentFactor> orderQuantityAdjustmentTypeList= this.adjustmentFactorMapper.getAll();
        Assert.assertNotNull(orderQuantityAdjustmentTypeList);
        Assert.assertTrue(orderQuantityAdjustmentTypeList.size()>0);


    }
}
