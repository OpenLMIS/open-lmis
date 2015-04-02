package org.openlmis.core.repository.mapper;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.*;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
@Category(IntegrationTests.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class OrderQuantityAdjustmentProductMapperTest {

    @Autowired
    private OrderQuantityAdjustmentProductMapper mapper;

    @Autowired
    private QueryExecutor queryExecutor;


    @Test
    public void shouldGetAll() throws Exception {

    }

    @Test
    public void shouldInsert() throws Exception {
        Product product = new Product();
        product.setId(20L);
        Facility facility = new Facility(10L);
        OrderQuantityAdjustmentProduct adjustmentProduct = new OrderQuantityAdjustmentProduct();
        adjustmentProduct.setAdjustmentFactor(new OrderQuantityAdjustmentFactor("adj-factor","adj-factor",1,true));
        adjustmentProduct.setAdjustmentType(new OrderQuantityAdjustmentType("adj-type","adj-type",1));
        adjustmentProduct.setFacility(new Facility(10L));
        adjustmentProduct.setProduct(product);
        adjustmentProduct.setMinMOS(5L);
        adjustmentProduct.setMaxMOS(15L);

        mapper.insert(adjustmentProduct);

        OrderQuantityAdjustmentProduct fetchedResult = mapper.getByProductAndFacility(product.getId(), facility.getId());

        assertThat(fetchedResult.getAdjustmentType().getName(), is(adjustmentProduct.getAdjustmentType().getName()));
    }

    @Test
    public void shouldGetByProductAndFacility() throws Exception {

    }
}