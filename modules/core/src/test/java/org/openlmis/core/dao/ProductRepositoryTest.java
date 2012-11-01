package org.openlmis.core.dao;

import org.junit.Test;
import org.openlmis.core.domain.Product;
import org.springframework.test.context.ContextConfiguration;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ContextConfiguration(locations = "classpath*:/applicationTestContext-core.xml")
public class ProductRepositoryTest {

    @Test
    public void shouldInsertProduct() throws Exception {
        ProductMapper mockedMapper = mock(ProductMapper.class);
        ProductRepository repository = new ProductRepository(mockedMapper);
        Product product = new Product();

        repository.insertProducts(product);

        verify(mockedMapper).insert(product);
    }

}
