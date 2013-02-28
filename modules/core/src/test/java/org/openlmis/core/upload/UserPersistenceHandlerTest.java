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

@RunWith(MockitoJUnitRunner.class)
public class UserPersistenceHandlerTest {

  private UserPersistenceHandler userPersistenceHandler;

  @Mock
  private UserService userService;

  @Test
  public void shouldSaveAUser() throws Exception {
    userPersistenceHandler = new UserPersistenceHandler(userService);
    User user = new User();
    userPersistenceHandler.save(user, 1);
    verify(userService).create(user);
    assertThat(user.getModifiedBy(), is(1));
  }
}
