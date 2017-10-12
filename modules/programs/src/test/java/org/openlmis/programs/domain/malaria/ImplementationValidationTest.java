package org.openlmis.programs.domain.malaria;

import com.natpryce.makeiteasy.Maker;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.openlmis.programs.domain.malaria.validators.helpers.ValidationHelper.assertValidationHasPropertyWithError;
import static org.openlmis.programs.helpers.ImplementationBuilder.executor;
import static org.openlmis.programs.helpers.ImplementationBuilder.randomImplementation;
import static org.openlmis.programs.helpers.ImplementationBuilder.treatments;
import static org.openlmis.programs.helpers.TreatmentBuilder.amount;
import static org.openlmis.programs.helpers.TreatmentBuilder.randomTreatment;

public class ImplementationValidationTest {
    private static final String MAY_NOT_BE_EMPTY = "may not be empty";
    private static final String EXECUTOR = "executor";
    private static final String FIRST_TREATMENT = "treatments[0].amount";
    private static final String SHOULD_BE_POSITIVE = "must be greater than or equal to 0";
    private Maker<Implementation> implementation = a(randomImplementation);
    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private Set<ConstraintViolation<Implementation>> validationResults;

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