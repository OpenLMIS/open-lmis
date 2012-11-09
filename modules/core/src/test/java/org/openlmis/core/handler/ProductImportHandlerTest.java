package org.openlmis.core.handler;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openlmis.core.domain.Product;
import org.openlmis.core.service.ProductService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;

import static org.mockito.Mockito.*;


public class ProductImportHandlerTest {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void shouldUseServiceToStoreImportedProducts() throws Exception {
        ProductService productService = mock(ProductService.class);
        Product product = new Product();

        new ProductImportHandler(productService).execute(product, 0);

        verify(productService).save(product);

    }

    @Test
    public void shouldRaiseDuplicateProductCodeError() throws Exception {
        ProductService productService = mock(ProductService.class);
        Product product = new Product();
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Duplicate Product Code found in Record No. 9");
        doThrow(new DuplicateKeyException("")).when(productService).save(product);

        new ProductImportHandler(productService).execute(product, 10);
    }

    @Test
    public void shouldRaiseIncorrectReferenceDataError() throws Exception {
        ProductService productService = mock(ProductService.class);
        Product product = new Product();
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Missing Reference data of Record No. 9");
        doThrow(new DataIntegrityViolationException("foreign key")).when(productService).save(product);

        new ProductImportHandler(productService).execute(product, 10);
    }
}



