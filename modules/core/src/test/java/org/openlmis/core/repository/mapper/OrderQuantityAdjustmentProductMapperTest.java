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

import java.util.List;

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

        mapper.insert(adjustmentProduct);

        List<OrderQuantityAdjustmentProduct> fetchedResult = mapper.getAll();

        assertThat(fetchedResult.size(), is(1));

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

        mapper.insert(adjustmentProduct);

        OrderQuantityAdjustmentProduct fetchedResult = mapper.getByProductAndFacility(product.getId(), facility.getId());

        assertThat(fetchedResult.getMaxMOS(), is(adjustmentProduct.getMaxMOS()));
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

        mapper.insert(adjustmentProduct);

        OrderQuantityAdjustmentProduct fetchedResult = mapper.getByProductAndFacility(product.getId(), facility.getId());

        assertThat(fetchedResult.getMaxMOS(), is(adjustmentProduct.getMaxMOS()));
    }
}