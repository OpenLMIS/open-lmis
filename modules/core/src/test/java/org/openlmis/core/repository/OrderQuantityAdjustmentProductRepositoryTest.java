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