package org.openlmis.authentication.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openlmis.core.domain.User;
import org.openlmis.core.repository.mapper.UserMapper;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.authentication.hash.Encoder.hash;

public class UserAuthenticationServiceTest {
    private UserAuthenticationService userAuthenticationService;

    @Mock
    @SuppressWarnings("unused")
    private UserMapper mockUserMapper;

    @Before
    public void setup() {
        initMocks(this);
        userAuthenticationService = new UserAuthenticationService(mockUserMapper);
    }

    @Test
    public void shouldAuthenticateAValidUser() {
        String validUser = "validUser";
        String validPassword = "validPassword";
        String hashPassword = hash("validPassword");

        when(mockUserMapper.authenticate(validUser, hashPassword)).thenReturn(true);
        boolean isAuthenticated = userAuthenticationService.authorizeUser(validUser, validPassword);
        verify(mockUserMapper).authenticate(validUser, hashPassword);
        assertThat(isAuthenticated, is(true));
    }

    @Test
    public void shouldNotAuthenticateAnInvalidUser() {
        String validUser = "validUser";
        String invalidPassword = "invalidPassword";
        String hashPassword = hash(invalidPassword);
        when(mockUserMapper.authenticate(validUser, hashPassword)).thenReturn(false);
        boolean isAuthenticated = userAuthenticationService.authorizeUser(validUser, invalidPassword);
        verify(mockUserMapper).authenticate(validUser, hashPassword);
        assertThat(isAuthenticated, is(false));
    }
}
