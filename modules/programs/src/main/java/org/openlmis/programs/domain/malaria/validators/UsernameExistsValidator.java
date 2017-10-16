package org.openlmis.programs.domain.malaria.validators;

import org.openlmis.core.domain.User;
import org.openlmis.core.repository.UserRepository;
import org.openlmis.programs.domain.malaria.validators.annotations.ValidateUsernameExists;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class UsernameExistsValidator implements ConstraintValidator<ValidateUsernameExists, String> {
    @Autowired
    private UserRepository userRepository;

    @Override
    public void initialize(ValidateUsernameExists constraintAnnotation) {

    }

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        User user = userRepository.getByUserName(username);
        boolean b = user != null;
        return b;
    }
}
