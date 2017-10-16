package org.openlmis.programs.domain.malaria.validators;

import com.natpryce.makeiteasy.Maker;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Matchers;
import org.mockito.MockitoAnnotations;
import org.openlmis.core.domain.Product;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.programs.domain.malaria.Implementation;
import org.openlmis.programs.domain.malaria.Treatment;

import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.openlmis.programs.domain.malaria.validators.helpers.ValidationHelper.assertValidationHasPropertyWithError;
import static org.openlmis.programs.helpers.ImplementationBuilder.*;
import static org.openlmis.programs.helpers.TreatmentBuilder.amount;
import static org.openlmis.programs.helpers.TreatmentBuilder.randomTreatment;

@Category(UnitTests.class)
public class ImplementationValidationTest extends ValidationBaseTest {
    private Maker<Implementation> implementation = a(randomImplementation);
    private Set<ConstraintViolation<Implementation>> validationResults;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        config = Validation.byDefaultProvider().configure();
        config.constraintValidatorFactory(this);
        factory = config.buildValidatorFactory();
        validator = factory.getValidator();
        when(usernameExistsValidator.isValid(anyString(), Matchers.<ConstraintValidatorContext>any())).thenReturn(true);
        when(productExistsValidator.isValid(any(Product.class), Matchers.<ConstraintValidatorContext>any())).thenReturn(true);
    }

    @Test
    public void shouldBeValidWhenHasNoErrors() throws Exception {
        validationResults = validator.validate(make(implementation));
        assertThat(validationResults, is(empty()));
    }

    @Test
    public void shouldReturnErrorWhenExecutorIsEmpty() throws Exception {
        Implementation implementationWithoutExecutor = make(implementation.but(with(executor, "")));
        validationResults = validator.validate(implementationWithoutExecutor);
        assertValidationHasPropertyWithError(EXECUTOR, MAY_NOT_BE_EMPTY, validationResults);
    }

    @Test
    public void shouldReturnErrorWhenExecutorIsNotDefined() throws Exception {
        String nullString = null;
        Implementation implementationWithoutExecutor = make(implementation.but(with(executor, nullString)));
        validationResults = validator.validate(implementationWithoutExecutor);
        assertValidationHasPropertyWithError(EXECUTOR, MAY_NOT_BE_EMPTY, validationResults);
    }

    @Test
    public void shouldReturnErrorWhenTreatmentsAreInvalid() throws Exception {
        Treatment treatment = make(a(randomTreatment, with(amount, -1)));
        Implementation implementationWithInvalidTreatment = make(implementation.but(with(treatments, newArrayList(treatment))));
        validationResults = validator.validate(implementationWithInvalidTreatment);
        assertValidationHasPropertyWithError(FIRST_TREATMENT, SHOULD_BE_POSITIVE, validationResults);
    }
}