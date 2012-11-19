package org.openlmis.authentication.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.authentication.UserToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-authentication.xml")
public class UserAuthenticationServiceIT {

    @Autowired
    UserAuthenticationService userAuthenticationService;

    @Test
    public void shouldFetchUserFromDbByUserNameAndPassword() {
        UserToken userToken = userAuthenticationService.authorizeUser("User123", "User123");
        assertThat(userToken.isAuthenticated(), is(true));
        assertThat(userToken.getUserName(), is(equalTo("User123")));
        assertThat(userToken.getRole(), is(equalTo("USER")));
    }
}
