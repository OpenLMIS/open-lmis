package org.openlmis.core.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Product;
import org.openlmis.core.repository.mapper.ProductMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;

import static org.mockito.Mockito.*;
@RunWith(MockitoJUnitRunner.class)
public class ProductRepositoryTest {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Mock
    ProductMapper mockedMapper;

    ProductRepository repository;

    @Before
    public void setUp() {
        repository = new ProductRepository(mockedMapper);
    }

    @Test
    public void shouldInsertProduct() throws Exception {
        Product product = new Product();
        repository.insert(product);
        verify(mockedMapper).insert(product);
    }

    @Test
    public void shouldRaiseDuplicateProductCodeError() throws Exception {
        Product product = new Product();
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Duplicate Product Code found");
        doThrow(new DuplicateKeyException("")).when(mockedMapper).insert(product);
        repository.insert(product);
    }

    @Test
    public void shouldRaiseIncorrectReferenceDataError() throws Exception {
        Product product = new Product();
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Missing Reference data");
        doThrow(new DataIntegrityViolationException("foreign key")).when(mockedMapper).insert(product);
        repository.insert(product);
    }

}
