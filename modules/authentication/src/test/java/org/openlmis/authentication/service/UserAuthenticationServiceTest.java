package org.openlmis.authentication.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openlmis.authentication.UserToken;
import org.openlmis.core.domain.User;
import org.openlmis.core.repository.mapper.UserMapper;

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
    String defaultUserName = "defaultUserName";
    String defaultPassword = "defaultPassword";
    User user = new User();
    user.setUserName(defaultUserName);

    String hashPassword = hash(defaultPassword);

    when(mockUserMapper.selectUserByUserNameAndPassword(defaultUserName, hashPassword)).thenReturn(user);

    UserToken userToken = userAuthenticationService.authorizeUser(defaultUserName, defaultPassword);
    verify(mockUserMapper).selectUserByUserNameAndPassword(defaultUserName, hashPassword);
    assertThat(userToken.isAuthenticated(), is(true));
  }

  @Test
  public void shouldNotAuthenticateAnInvalidUser() {
    String invalidPassword = "invalidPassword";
    String hashPassword = hash(invalidPassword);
    String defaultUserName = "defaultUserName";
    when(mockUserMapper.selectUserByUserNameAndPassword(defaultUserName, hashPassword)).thenReturn(null);

    UserToken userToken = userAuthenticationService.authorizeUser(defaultUserName, invalidPassword);

    verify(mockUserMapper).selectUserByUserNameAndPassword(defaultUserName, hashPassword);
    assertThat(userToken.isAuthenticated(), is(false));
  }
}
