package org.openlmis.programs.domain.malaria.validators;

import org.openlmis.programs.domain.malaria.MalariaProgram;
import org.openlmis.programs.domain.malaria.validators.annotations.ValidateMalariaPeriodDates;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MalariaPeriodValidator implements ConstraintValidator<ValidateMalariaPeriodDates, MalariaProgram> {

    @Override
    public void initialize(ValidateMalariaPeriodDates constraintAnnotation) {

    }

    @Override
    public boolean isValid(MalariaProgram malariaProgram, ConstraintValidatorContext context) {
        return malariaProgram.getPeriodStartDate().compareTo(malariaProgram.getPeriodEndDate()) < 0;
    }
}
