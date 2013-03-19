package org.openlmis.core.upload;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.User;
import org.openlmis.core.service.UserService;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.openlmis.core.upload.UserPersistenceHandler.RESET_PASSWORD_PATH;

@RunWith(MockitoJUnitRunner.class)
public class UserPersistenceHandlerTest {

  private UserPersistenceHandler userPersistenceHandler;

  @Mock
  private UserService userService;

  @Test
  public void shouldSaveAUser() throws Exception {
    String baseUrl = "http://localhost:9091/";
    userPersistenceHandler = new UserPersistenceHandler(userService, baseUrl);
    User user = new User();
    userPersistenceHandler.save(user, 1);
    verify(userService).create(user, baseUrl + RESET_PASSWORD_PATH);
    assertThat(user.getModifiedBy(), is(1));
  }
}
