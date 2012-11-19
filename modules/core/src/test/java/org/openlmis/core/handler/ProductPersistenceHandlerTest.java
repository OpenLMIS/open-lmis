package org.openlmis.core.handler;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openlmis.core.domain.Product;
import org.openlmis.core.service.ProductService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


public class ProductPersistenceHandlerTest {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void shouldSaveImportedProduct() throws Exception {
        ProductService productService = mock(ProductService.class);
        Product product = new Product();

        new ProductPersistenceHandler(productService).execute(product, 0, "user");

        verify(productService).save(product);
    }
 }



