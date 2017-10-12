package org.openlmis.programs.domain.malaria.validators;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Product;
import org.openlmis.core.repository.ProductRepository;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.openlmis.core.builder.ProductBuilder.randomProduct;

@RunWith(MockitoJUnitRunner.class)
public class ProductExistsValidatorTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductExistsValidator validator;
    private Product product = make(a(randomProduct));

    @Test
    public void shouldReturnTrueWhenProductExists() throws Exception {
        when(productRepository.getByCode(product.getCode())).thenReturn(product);
        boolean result = validator.isValid(product, null);
        assertThat(result, is(true));
    }

    @Test
    public void shouldReturnFalseWhenProductDoesNotExist() throws Exception {
        boolean result = validator.isValid(product, null);
        assertThat(result, is(false));
    }
}