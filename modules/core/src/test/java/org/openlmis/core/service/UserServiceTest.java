package org.openlmis.core.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.User;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.UserRepository;
import org.openlmis.email.domain.EmailMessage;
import org.openlmis.email.exception.EmailException;
import org.openlmis.email.service.EmailService;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

  @Rule
  public ExpectedException expectedException = none();

  @Mock
  private UserRepository userRepository;

  @Mock
  private EmailService emailService;

  private UserService userService;


  @Before
  public void setUp() throws Exception {
    userService = new UserService(userRepository, emailService);
  }

  @Test
  public void shouldValidateUserBeforeInsert() throws Exception {
    User user = mock(User.class);
    doThrow(new DataException("user.email.invalid")).when(user).validate();
    expectedException.expect(DataException.class);
    expectedException.expectMessage("user.email.invalid");
    userService.save(user);
    verify(userRepository, never()).insert(user);
  }

  @Test
  public void shouldGiveErrorIfUserDoesNotExist() throws Exception {
    User user = new User();
    String email = "some email";
    user.setEmail(email);
    when(userRepository.getByEmail(email)).thenReturn(null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage(UserService.USER_EMAIL_INCORRECT);

    userService.sendForgotPasswordEmail(user);
  }

  @Test
  public void shouldSendForgotPasswordEmailIfUserEmailExists() throws Exception {
      User user = new User();
      user.setEmail("shibhama@thoughtworks.com");

      User userToBeReturned = new User();
      userToBeReturned.setUserName("Admin");
      userToBeReturned.setEmail("shibhama@thoughtworks.com");
      userToBeReturned.setId(1111);
      when(userRepository.getByEmail(user.getEmail())).thenReturn(userToBeReturned);

      userService.sendForgotPasswordEmail(user);

      verify(emailService).send(any(EmailMessage.class));
      verify(userRepository).getByEmail(user.getEmail());
  }

  @Test
  public void shouldGiveErrorIfUserEmailDoesNotExist() throws Exception {
    User userWithoutEmail = new User();
    User user = new User();
    user.setEmail("some email");
    when(userRepository.getByEmail(user.getEmail())).thenReturn(userWithoutEmail);
    doThrow(new EmailException("")).when(emailService).send(any(EmailMessage.class));

    expectedException.expect(DataException.class);
    expectedException.expectMessage(UserService.USER_EMAIL_NOT_FOUND);

    userService.sendForgotPasswordEmail(user);
  }

  @Test
  public void shouldReturnSearchResultsWhenUserExists() throws Exception {
    User user = new User();
    String userSearchParam="abc";
    List<User> listOfUsers = Arrays.asList(new User());

    when(userRepository.searchUser(userSearchParam)).thenReturn(listOfUsers);

    List<User> listOfReturnedUsers = userService.searchUser(userSearchParam);

    assertTrue(listOfReturnedUsers.contains(user));
  }
}
