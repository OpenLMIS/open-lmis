package org.openlmis.programs.domain.malaria.validators;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.MockitoAnnotations;
import org.openlmis.core.domain.Product;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.programs.domain.malaria.Treatment;
import org.openlmis.programs.domain.malaria.validators.annotations.ValidateProduct;

import javax.validation.*;
import java.lang.reflect.Field;
import java.util.Set;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.openlmis.programs.domain.malaria.validators.helpers.ValidationHelper.assertValidationHasPropertyWithError;
import static org.openlmis.programs.helpers.TreatmentBuilder.*;

@Category(UnitTests.class)
public class TreatmentValidationTest extends ValidationBaseTest {
    private Set<ConstraintViolation<Treatment>> validationResults;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        config = Validation.byDefaultProvider().configure().constraintValidatorFactory(this);
        validator = config.buildValidatorFactory().getValidator();
        when(productExistsValidator.isValid(any(Product.class), any(ConstraintValidatorContext.class))).thenReturn(true);
    }

    @Test
    public void shouldBeValidWhenHasNoErrors() throws Exception {
        Treatment treatment = make(a(randomTreatment));
        validationResults = validator.validate(treatment);
        assertThat(validationResults, is(empty()));
    }

    @Test
    public void shouldReturnErrorWhenAmountIsNegative() throws Exception {
        int negativeAmount = nextInt() * (-1);
        Treatment negativeAmountTreatment = make(a(randomTreatment, with(amount, negativeAmount)));
        validationResults = validator.validate(negativeAmountTreatment);
        assertValidationHasPropertyWithError(AMOUNT, SHOULD_BE_POSITIVE, validationResults);
    }

    @Test
    public void shouldReturnErrorWhenStockIsNegative() throws Exception {
        int negativeAmount = nextInt() * (-1);
        Treatment negativeAmountTreatment = make(a(randomTreatment, with(stock, negativeAmount)));
        validationResults = validator.validate(negativeAmountTreatment);
        assertValidationHasPropertyWithError(STOCK, SHOULD_BE_POSITIVE, validationResults);
    }

    @Test
    public void shouldReturnErrorWhenProductIsNotDefine() throws Exception {
        Product nullProduct = null;
        Treatment notDefinedProductTreatment = make(a(randomTreatment, with(product, nullProduct)));
        validationResults = validator.validate(notDefinedProductTreatment);
        assertValidationHasPropertyWithError(PRODUCT, SHOULD_NOT_BE_NULL, validationResults);
    }

    @Test
    public void shouldValidateIfProductExists() throws Exception {
        Field product = Treatment.class.getDeclaredField("product");
        assertThat(product.isAnnotationPresent(ValidateProduct.class), is(true));
    }
}