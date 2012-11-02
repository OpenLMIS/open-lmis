package org.openlmis.core.handler;

import org.junit.Test;
import org.openlmis.core.domain.Product;
import org.openlmis.core.service.ProductService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


public class ProductImportHandlerTest {
    @Test
    public void shouldUseServiceToStoreImportedProducts() throws Exception {
        ProductService productService = mock(ProductService.class);
        Product product = new Product();

        new ProductImportHandler(productService).execute(product);

        verify(productService).save(product);

    }
}



