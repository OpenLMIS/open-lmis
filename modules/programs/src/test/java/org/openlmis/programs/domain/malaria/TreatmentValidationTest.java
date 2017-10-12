package org.openlmis.programs.domain.malaria;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openlmis.core.domain.Product;
import org.openlmis.programs.domain.malaria.validators.ProductExistsValidator;
import org.openlmis.programs.domain.malaria.validators.annotations.ValidateProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.validation.*;
import java.lang.reflect.Field;
import java.util.Set;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.MakeItEasy.with;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.openlmis.programs.domain.malaria.validators.helpers.ValidationHelper.assertValidationHasPropertyWithError;
import static org.openlmis.programs.helpers.TreatmentBuilder.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:test-applicationContext-programs.xml" })
public class TreatmentValidationTest implements ConstraintValidatorFactory {

    private static final String SHOULD_BE_POSITIVE = "must be greater than or equal to 0";
    private static final String SHOULD_NOT_BE_NULL = "may not be null";
    public static final String PRODUCT = "product";
    private static final String STOCK = "stock";
    private static final String AMOUNT = "amount";

    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    @Mock
    private ProductExistsValidator productExistsValidator;

    private Validator validator;
    private Set<ConstraintViolation<Treatment>> validationResults;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Configuration<?> configuration = Validation.byDefaultProvider().configure().constraintValidatorFactory(this);
        validator = configuration.buildValidatorFactory().getValidator();
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

    @Override
    public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
        if (key == ProductExistsValidator.class) {
            return (T) productExistsValidator;
        }
        return beanFactory.createBean(key);
    }

    @Override
    public void releaseInstance(ConstraintValidator<?, ?> instance) {

    }
}