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
import org.openlmis.email.domain.EmailMessage;
import org.openlmis.email.service.EmailService;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.rules.ExpectedException.none;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
//@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-core.xml")
public class UserServiceTest {

  @Rule
  public ExpectedException expectedException = none();
  @Mock
  @SuppressWarnings("unused")
  private UserRepository userRepository;

  @Mock
  private EmailService emailService;

//  @Autowired
//  private MailSender mailSender;
//  @Autowired
//  private UserRepository userRepository;

  private UserService userService;

  @Test
  public void shouldValidateUserBeforeInsert() throws Exception {
    User user = mock(User.class);
    doThrow(new DataException("user.email.invalid")).when(user).validate();
    UserService userService = new UserService(userRepository,null);
    expectedException.expect(DataException.class);
    expectedException.expectMessage("user.email.invalid");
    userService.save(user);
    verify(userRepository, never()).insert(user);
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

      //EmailService emailService = new EmailService(mailSender);

      userService  = new UserService(userRepository,emailService);

      userService.sendForgotPasswordEmail(user);

      verify(emailService).send(any(EmailMessage.class));
      verify(userRepository).getByEmail(user.getEmail());

  }
}
