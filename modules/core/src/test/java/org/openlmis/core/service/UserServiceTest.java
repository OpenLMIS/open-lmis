package org.openlmis.core.service;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.User;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.UserRepository;

import static org.junit.rules.ExpectedException.none;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

  @Rule
  public ExpectedException expectedException = none();
  @Mock
  @SuppressWarnings("unused")
  private UserRepository userRepository;

  @Test
  public void shouldValidateUserBeforeInsert() throws Exception {
    User user = mock(User.class);
    doThrow(new DataException("user.email.invalid")).when(user).validate();
    UserService userService = new UserService(userRepository);
    expectedException.expect(DataException.class);
    expectedException.expectMessage("user.email.invalid");
    userService.save(user);
    verify(userRepository, never()).insert(user);
  }
}
