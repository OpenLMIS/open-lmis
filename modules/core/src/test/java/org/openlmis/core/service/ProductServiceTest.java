package org.openlmis.core.service;

import org.junit.Test;
import org.openlmis.core.dao.ProductRepository;
import org.openlmis.core.domain.Product;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


public class ProductServiceTest {

    @Test
    public void shouldStoreProduct() throws Exception {
        List<Product> productList =new ArrayList<Product>();
        ProductRepository productRepository=mock(ProductRepository.class);

        new ProductService(productRepository).storeProducts(productList);

        verify(productRepository).insertProducts(productList);

    }
}
