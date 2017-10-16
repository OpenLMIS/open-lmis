package org.openlmis.programs.domain.malaria.validators;

import com.natpryce.makeiteasy.Maker;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.MockitoAnnotations;
import org.openlmis.core.domain.Product;
import org.openlmis.programs.domain.malaria.Implementation;
import org.openlmis.programs.domain.malaria.MalariaProgram;
import org.openlmis.programs.domain.malaria.validators.annotations.ValidateUsernameExists;

import javax.validation.*;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.joda.time.DateMidnight.now;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.openlmis.programs.domain.malaria.validators.helpers.ValidationHelper.assertValidationHasPropertyWithError;
import static org.openlmis.programs.helpers.ImplementationBuilder.executor;
import static org.openlmis.programs.helpers.ImplementationBuilder.randomImplementation;
import static org.openlmis.programs.helpers.MalariaProgramBuilder.*;

public class MalariaProgramValidationTest extends ValidationBaseTest {
    private Maker<MalariaProgram> malariaProgram;
    private Set<ConstraintViolation<MalariaProgram>> validationResults;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        config = Validation.byDefaultProvider().configure();
        config.constraintValidatorFactory(this);
        factory = config.buildValidatorFactory();
        validator = factory.getValidator();
        malariaProgram = a(randomMalariaProgram);
        when(usernameExistsValidator.isValid(anyString(), Matchers.<ConstraintValidatorContext>any())).thenReturn(true);
        when(productExistsValidator.isValid(any(Product.class), Matchers.<ConstraintValidatorContext>any())).thenReturn(true);
    }

    @Test
    public void shouldNotHasErrorsWhenValid() throws Exception {
        validationResults = validator.validate(make(malariaProgram));
        assertThat(validationResults, is(empty()));
    }

    @Test
    public void shouldValidateUsernameExists() throws Exception {
        Field username = MalariaProgram.class.getDeclaredField("username");
        assertThat(username.getAnnotation(ValidateUsernameExists.class), is(notNullValue()));
    }

    @Test
    public void shouldReturnErrorWhenUsernameIsNotDefined() throws Exception {
        String nullUsername = null;
        MalariaProgram programWithoutUsername = make(malariaProgram.but(with(username, nullUsername)));
        validationResults = validator.validate(programWithoutUsername);
        assertValidationHasPropertyWithError(USERNAME, MAY_NOT_BE_EMPTY, validationResults);
    }

    @Test
    public void shouldReturnErrorWhenUsernameIsEmpty() throws Exception {
        MalariaProgram programWithoutUsername = make(malariaProgram.but(with(username, "")));
        validationResults = validator.validate(programWithoutUsername);
        assertValidationHasPropertyWithError(USERNAME, MAY_NOT_BE_EMPTY, validationResults);
    }

    @Test
    public void shouldReturnErrorWhenStartDateIsEqualsToEndDate() throws Exception {
        Date periodDate = now().toDate();
        MalariaProgram programWithInvalidDates = make(malariaProgram.but(
                with(periodStartDate, periodDate),
                with(periodEndDate, periodDate)));
        validationResults = validator.validate(programWithInvalidDates);
        assertValidationHasPropertyWithError("", SHOULD_BE_PREVIOUS, validationResults);
    }

    @Test
    public void shouldReturnErrorWhenStartDateIsOlderThanEndDate() throws Exception {
        MalariaProgram programWithInvalidDates = make(malariaProgram.but(
                with(periodStartDate, now().plusDays(nextInt(10)).toDate()),
                with(periodEndDate, now().toDate())));
        validationResults = validator.validate(programWithInvalidDates);
        assertValidationHasPropertyWithError("", SHOULD_BE_PREVIOUS, validationResults);
    }

    @Test
    public void shouldReturnErrorWhenImplementationsAreInvalid() throws Exception {
        String nullValue = null;
        Implementation invalidImplementation = make(a(randomImplementation, with(executor, nullValue)));
        MalariaProgram programWithInvalidImplementation = make(malariaProgram.but(with(implementations, newArrayList(invalidImplementation))));
        validationResults = validator.validate(programWithInvalidImplementation);
        assertValidationHasPropertyWithError(FIRST_IMPLEMENTATION, MAY_NOT_BE_EMPTY, validationResults);
    }
}
