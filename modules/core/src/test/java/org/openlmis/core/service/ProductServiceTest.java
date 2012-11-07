package org.openlmis.core.service;

import org.junit.Test;
import org.openlmis.core.domain.Product;
import org.openlmis.core.repository.ProductRepository;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


public class ProductServiceTest {

    @Test
    public void shouldStoreProduct() throws Exception {
        Product product = new Product();
        ProductRepository productRepository = mock(ProductRepository.class);

        new ProductService(productRepository).save(product);

        verify(productRepository).insertProducts(product);

    }
}
