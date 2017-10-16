package org.openlmis.programs.domain.malaria.validators;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.validation.*;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test`-applicationContext-programs.xml")
public class ValidationBaseTest implements ConstraintValidatorFactory{
    protected static final String MAY_NOT_BE_EMPTY = "may not be empty";
    protected static final String EXECUTOR = "executor";
    protected static final String FIRST_TREATMENT = "treatments[0].amount";
    protected static final String SHOULD_BE_POSITIVE = "must be greater than or equal to 0";
    protected static final String SHOULD_BE_PREVIOUS = "Start period date needs to be previous the end date";
    protected static final String USERNAME = "username";
    protected static final String FIRST_IMPLEMENTATION = "implementations[0].executor";
    protected static final String SHOULD_NOT_BE_NULL = "may not be null";
    protected static final String PRODUCT = "product";
    protected static final String STOCK = "stock";
    protected static final String AMOUNT = "amount";

    protected Configuration<?> config;
    protected Validator validator;
    protected ValidatorFactory factory;

    @Autowired
    protected AutowireCapableBeanFactory beanFactory;
    @Mock
    protected UsernameExistsValidator usernameExistsValidator;
    @Mock
    protected ProductExistsValidator productExistsValidator;

    @Override
    public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
        if (key == UsernameExistsValidator.class) { return (T) usernameExistsValidator; }
        if (key == ProductExistsValidator.class) { return (T) productExistsValidator; }
        return beanFactory.createBean(key);
    }

    @Override
    public void releaseInstance(ConstraintValidator<?, ?> instance) {

    }
}
