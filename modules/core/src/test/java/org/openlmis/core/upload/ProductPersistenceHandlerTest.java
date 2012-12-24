package org.openlmis.core.upload;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openlmis.core.domain.Product;
import org.openlmis.core.repository.ProductRepository;

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
        ProductRepository productRepository = mock(ProductRepository.class);
        Product product = new Product();

        new ProductPersistenceHandler(productRepository).execute(product, 0, "user");
        assertThat(product.getModifiedBy(), is("user"));
        assertThat(product.getModifiedDate(), is(notNullValue()));
        verify(productRepository).insert(product);
    }
 }



