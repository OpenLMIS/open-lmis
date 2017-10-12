package org.openlmis.programs.domain.malaria.validators.annotations;

import org.openlmis.programs.domain.malaria.validators.MalariaPeriodValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MalariaPeriodValidator.class )
public @interface ValidateMalariaPeriodDates {
    String message() default "Start period date needs to be previous the end date";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default { };
}
