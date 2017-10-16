package org.openlmis.programs.domain.malaria.validators;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.repository.UserRepository;
import org.openlmis.db.categories.UnitTests;

import javax.validation.ConstraintValidator;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;
import static org.openlmis.core.builder.UserBuilder.defaultUser;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class UsernameExistsValidatorTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UsernameExistsValidator validator;

    private String username;

    @Before
    public void setUp() throws Exception {
        username = randomAlphabetic(10);
        when(userRepository.getByUserName(username)).thenReturn(make(a(defaultUser)));
    }

    @Test
    public void shouldImplementsValidatorInterface() throws Exception {
        assertThat(validator, is(instanceOf(ConstraintValidator.class)));
    }

    @Test
    public void shouldReturnTrueWhenValid() throws Exception {
        boolean validationResult = validator.isValid(username, null);
        assertThat(validationResult, is(true));
    }

    @Test
    public void shouldReturnFalseWhenUsernameDoesNotExist() throws Exception {
        when(userRepository.getByUserName(username)).thenReturn(null);
        boolean validationResult = validator.isValid(username, null);
        assertThat(validationResult, is(false));
    }
}