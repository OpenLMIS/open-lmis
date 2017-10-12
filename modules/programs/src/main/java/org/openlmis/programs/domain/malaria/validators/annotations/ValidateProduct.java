package org.openlmis.programs.domain.malaria.validators.annotations;


import org.openlmis.programs.domain.malaria.validators.ProductExistsValidator;
import org.openlmis.programs.domain.malaria.validators.UsernameExistsValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ProductExistsValidator.class)
public @interface ValidateProduct {
    String message() default "{error.user.username.not.found}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
