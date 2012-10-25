package org.openlmis.web.authentication;

import org.junit.Before;
import org.junit.Test;
import org.openlmis.authentication.service.UserAuthenticationService;
import org.openlmis.authentication.UserToken;
import org.openlmis.authentication.domain.User;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class UserAuthenticationProviderTest {

    private UserAuthenticationService userService;
    private UserAuthenticationProvider userAuthenticationProvider;

    @Before
    public void setup(){
        userService = mock(UserAuthenticationService.class);
        userAuthenticationProvider = new UserAuthenticationProvider(userService);
    }

    @Test
    public void shouldAuthenticateValidUser(){

        String validUser = "validUser";
        String password = "password";
        when(userService.authorizeUser(validUser, password)).thenReturn(new UserToken(new User(validUser, "ADMIN"), true));
        Authentication authentication = new TestingAuthenticationToken(validUser, password);

        Authentication authenticate = userAuthenticationProvider.authenticate(authentication);


        assertThat(authenticate,instanceOf(UsernamePasswordAuthenticationToken.class));
        assertThat(authenticate.getPrincipal().toString(), is(equalTo(validUser)));
        assertThat(authenticate.getCredentials().toString(), is(equalTo(password)));
        assertThat(authenticate.isAuthenticated(),is(true));
        assertThat(authenticate.getAuthorities().iterator().next().getAuthority(),  is(equalTo("ADMIN")));
    }


    @Test
    public void shouldNotAuthenticateInvalidUser() {
        String invalidUser = "invalidUser";
        String password = "password";
        when(userService.authorizeUser(invalidUser, password)).thenReturn(new UserToken(new User(invalidUser, null), false));
        Authentication authentication = new TestingAuthenticationToken(invalidUser, password);

        Authentication authenticate = userAuthenticationProvider.authenticate(authentication);

        assertThat(authenticate,instanceOf(UsernamePasswordAuthenticationToken.class));
        assertThat(authenticate.getPrincipal().toString(), is(equalTo(invalidUser)));
        assertThat(authenticate.getCredentials().toString(), is(equalTo(password)));
        assertThat(authenticate.isAuthenticated(), is(false));
        assertThat(authenticate.getAuthorities().size(),  is(equalTo(0)));
    }

}
