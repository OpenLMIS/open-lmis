package org.openlmis.core.upload;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openlmis.core.domain.Product;
import org.openlmis.core.repository.ProductRepository;
import org.openlmis.core.service.ProductService;
import org.openlmis.upload.model.AuditFields;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


public class ProductPersistenceHandlerTest {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void shouldSaveImportedProduct() throws Exception {
        ProductService productService = mock(ProductService.class);
        Product product = new Product();

        new ProductPersistenceHandler(productService).execute(product, 0, new AuditFields(1,null));
        assertThat(product.getModifiedBy(), is(1));
        assertThat(product.getModifiedDate(), is(notNullValue()));
        verify(productService).save(product);
    }
 }



