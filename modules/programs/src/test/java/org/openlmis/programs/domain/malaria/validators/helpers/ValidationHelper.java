package org.openlmis.programs.domain.malaria.validators.helpers;

import javax.validation.ConstraintViolation;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ValidationHelper {
    public static <T> void assertValidationHasPropertyWithError(String property, String message, Set<ConstraintViolation<T>> validationResults) {
        ConstraintViolation<T> constraintViolation = validationResults.iterator().next();
        assertThat(validationResults.size(), is(1));
        assertThat(constraintViolation.getPropertyPath().toString(), is(property));
        assertThat(constraintViolation.getMessage(), is(message));
    }
}
