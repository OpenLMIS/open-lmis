/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.RoleAssignment;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.domain.User;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.hash.Encoder;
import org.openlmis.core.repository.UserRepository;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.email.exception.EmailException;
import org.openlmis.email.service.EmailService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mail.SimpleMailMessage;

import java.util.Arrays;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.openlmis.core.service.UserService.PASSWORD_RESET_TOKEN_INVALID;
import static org.openlmis.email.builder.EmailMessageBuilder.*;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.*;

@Category(UnitTests.class)

@RunWith(PowerMockRunner.class)
@PrepareForTest({Encoder.class, UserService.class})
public class UserServiceTest {

  public static final String FORGET_PASSWORD_LINK = "http://openLMIS.org";
  @Rule
  public ExpectedException expectedException = none();

  @Mock
  @SuppressWarnings("unused")
  private UserRepository userRepository;

  @Mock
  @SuppressWarnings("unused")
  private EmailService emailService;

  @Mock
  @SuppressWarnings("unused")
  private RoleAssignmentService roleAssignmentService;

  @Mock
  private MessageService messageService;

  @InjectMocks
  private UserService userService;


  @Before
  public void setUp() throws Exception {
    when(messageService.message("accountcreated.email.subject")).thenReturn("Account created message");
    when(messageService.message("forgotpassword.email.subject")).thenReturn("Forgot password email subject");

  }

  private Matcher<SimpleMailMessage> emailMessageMatcher(final SimpleMailMessage that) {
    return new ArgumentMatcher<SimpleMailMessage>() {
      @Override
      public boolean matches(Object argument) {
        SimpleMailMessage emailMessage = (SimpleMailMessage) argument;
        return emailMessage.equals(that);
      }
    };
  }

  @Test
  public void shouldValidateUserBeforeInsert() throws Exception {
    User user = mock(User.class);
    doThrow(new DataException("user.email.invalid")).when(user).validate();
    expectedException.expect(DataException.class);
    expectedException.expectMessage("user.email.invalid");
    userService.create(user, "http://openLMIS.org");
    verify(userRepository, never()).create(user);
  }

  @Test
  public void shouldGiveErrorIfUserDoesNotExist() throws Exception {
    User user = new User();
    String email = "some email";
    user.setEmail(email);
    when(userRepository.getByEmail(email)).thenReturn(null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage(UserService.USER_EMAIL_INCORRECT);

    userService.sendForgotPasswordEmail(user, FORGET_PASSWORD_LINK);

  }

  @Test
  public void shouldSendForgotPasswordEmailIfUserEmailExists() throws Exception {
    User user = new User();
    user.setUserName("Admin");
    user.setEmail("random@random.com");
    user.setId(1111L);

    SimpleMailMessage emailMessage = make(a(defaultEmailMessage, with(receiver, "random@random.com"),
      with(subject, "Forgot password email subject"), with(content, "email body")));
    when(userRepository.getByEmail(user.getEmail())).thenReturn(user);

    mockStatic(Encoder.class);
    when(Encoder.hash(anyString())).thenReturn("token");

    when(messageService.message("passwordreset.email.body", new Object[]{FORGET_PASSWORD_LINK + "token"}))
      .thenReturn("email body");

    userService.sendForgotPasswordEmail(user, FORGET_PASSWORD_LINK);

    verify(emailService).send(argThat(emailMessageMatcher(emailMessage)));
    verify(userRepository).getByEmail(user.getEmail());
    verify(userRepository).insertPasswordResetToken(eq(user), anyString());
  }

  @Test
  public void shouldGiveErrorIfUserEmailDoesNotExist() throws Exception {
    User userWithoutEmail = new User();
    User user = new User();
    user.setEmail("some email");
    when(userRepository.getByEmail(user.getEmail())).thenReturn(userWithoutEmail);
    doThrow(new EmailException("")).when(emailService).send(any(SimpleMailMessage.class));

    expectedException.expect(DataException.class);
    expectedException.expectMessage(UserService.USER_EMAIL_NOT_FOUND);

    userService.sendForgotPasswordEmail(user, FORGET_PASSWORD_LINK);
  }

  @Test
  public void shouldReturnUserIfIdExists() throws Exception {
    User user = new User();
    List<RoleAssignment> homeFacilityRoles = Arrays.asList(new RoleAssignment());
    List<RoleAssignment> supervisorRoles = Arrays.asList(new RoleAssignment());
    List<RoleAssignment> allocationRoles = Arrays.asList(new RoleAssignment());

    when(userRepository.getById(1L)).thenReturn(user);
    when(roleAssignmentService.getHomeFacilityRoles(1L)).thenReturn(homeFacilityRoles);
    when(roleAssignmentService.getSupervisorRoles(1L)).thenReturn(supervisorRoles);
    when(roleAssignmentService.getAllocationRoles(1L)).thenReturn(allocationRoles);
    RoleAssignment adminRole = new RoleAssignment();
    when(roleAssignmentService.getAdminRole(1L)).thenReturn(adminRole);

    User returnedUser = userService.getById(1L);

    assertThat(returnedUser, is(user));
    assertThat(returnedUser.getHomeFacilityRoles(), is(homeFacilityRoles));
    assertThat(returnedUser.getSupervisorRoles(), is(supervisorRoles));
    assertThat(returnedUser.getAllocationRoles(), is(allocationRoles));
    assertThat(returnedUser.getAdminRole(), is(adminRole));
  }

  @Test
  public void shouldSendPasswordEmailWhenUserCreated() throws Exception {
    User user = new User();

    userService.create(user, FORGET_PASSWORD_LINK);

    verify(emailService).send(any(SimpleMailMessage.class));
  }


  @Test
  public void shouldReturnSearchResultsWhenUserExists() throws Exception {
    User user = new User();
    String userSearchParam = "abc";
    List<User> listOfUsers = Arrays.asList(new User());

    when(userRepository.searchUser(userSearchParam)).thenReturn(listOfUsers);

    List<User> listOfReturnedUsers = userService.searchUser(userSearchParam);

    assertTrue(listOfReturnedUsers.contains(user));
  }

  @Test
  public void shouldUpdateUser() throws Exception {
    User user = new User();
    final RoleAssignment roleAssignment = new RoleAssignment(1L, 1L, 1L, new SupervisoryNode(1L));
    List<RoleAssignment> supervisorRoles = Arrays.asList(roleAssignment);
    user.setSupervisorRoles(supervisorRoles);

    userService.update(user);

    verify(userRepository).update(user);
    verify(roleAssignmentService).saveRolesForUser(user);
  }

  @Test
  public void shouldThrowErrorIfPasswordResetTokenIsInValidWhileGettingUserId() throws Exception {

    String invalidToken = "invalidToken";
    when(userRepository.getUserIdForPasswordResetToken(invalidToken)).thenReturn(null);
    expectedException.expect(DataException.class);
    expectedException.expectMessage(PASSWORD_RESET_TOKEN_INVALID);

    userService.getUserIdByPasswordResetToken(invalidToken);
  }

  @Test
  public void shouldReturnUserIdIfPasswordResetTokenIsValid() throws Exception {
    String validToken = "validToken";
    Long expectedUserId = 1L;
    when(userRepository.getUserIdForPasswordResetToken(validToken)).thenReturn(expectedUserId);

    Long userId = userService.getUserIdByPasswordResetToken(validToken);

    verify(userRepository).getUserIdForPasswordResetToken(validToken);
    assertThat(userId, is(expectedUserId));
  }

  @Test
  public void shouldCreateUserInDB() throws Exception {
    User user = new User();

    SimpleMailMessage emailMessage = new SimpleMailMessage();
    whenNew(SimpleMailMessage.class).withNoArguments().thenReturn(emailMessage);

    when(messageService.message("accountcreated.email.subject")).thenReturn("Account created message");

    userService.createUser(user, "resetPasswordLink");

    verify(userRepository).create(user);
    verify(userRepository).insertEmailNotification(emailMessage);
    verify(emailService, never()).send(emailMessage);
    verify(roleAssignmentService).saveRolesForUser(user);
  }

  @Test
  public void shouldInsertUsersWithAllRoles() throws Exception {
    User user = new User();

    userService.create(user, FORGET_PASSWORD_LINK);

    verify(userRepository).create(user);
    verify(roleAssignmentService).saveRolesForUser(user);
  }

  @Test
  public void shouldUpdateUserPassword() {
    Long userId = 1l;
    String newPassword = "newPassword";

    userService.updateUserPassword(userId, newPassword);

    verify(userRepository).updateUserPassword(userId, newPassword);
  }
}
