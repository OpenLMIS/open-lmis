/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;

import org.openlmis.core.domain.User;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.hash.Encoder;
import org.openlmis.core.repository.UserRepository;
import org.openlmis.email.domain.EmailMessage;
import org.openlmis.email.exception.EmailException;
import org.openlmis.email.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
  public static final String USER_EMAIL_NOT_FOUND = "user.email.not.found";
  public static final String USER_EMAIL_INCORRECT = "user.email.incorrect";
  public static final String PASSWORD_RESET_TOKEN_INVALID = "user.password.reset.token.invalid";
  private static final String USER_USERNAME_INCORRECT = "user.username.incorrect";

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private EmailService emailService;
  @Autowired
  private RoleAssignmentService roleAssignmentService;
  @Autowired
  private MessageService messageService;

  public void create(User user, String resetPasswordLink) {
    save(user);
    sendUserCreationEmail(user, resetPasswordLink);
  }

  public void createUser(User user, String passwordResetLink) {
    save(user);
    prepareForEmailNotification(user, passwordResetLink);
  }

  public void sendUserCreationEmail(User user, String resetPasswordLink) {
    EmailMessage emailMessage = accountCreatedEmailMessage(user, resetPasswordLink);
    sendEmail(emailMessage);
  }

  private void save(User user) {
    user.validate();
    userRepository.create(user);
    roleAssignmentService.saveRolesForUser(user);
  }

  public void update(User user) {
    user.validate();
    userRepository.update(user);
    roleAssignmentService.saveRolesForUser(user);
  }

  private void sendEmail(EmailMessage emailMessage) {
    try {
      emailService.send(emailMessage);
    } catch (EmailException e) {
      throw new DataException(USER_EMAIL_NOT_FOUND);
    }
  }

  public void sendForgotPasswordEmail(User user, String resetPasswordLink) {
    user = getValidatedUser(user);
    userRepository.deletePasswordResetTokenForUser(user.getId());
    EmailMessage emailMessage = forgotPasswordEmailMessage(user, resetPasswordLink);
    sendEmail(emailMessage);
  }

  private User getValidatedUser(User user) {
    if (user.getEmail() != null && !user.getEmail().equals("")) {
      user = userRepository.getByEmail(user.getEmail());
      if (user == null) throw new DataException(USER_EMAIL_INCORRECT);
    } else {
      user = userRepository.getByUsernameAndVendorId(user);
      if (user == null) throw new DataException(USER_USERNAME_INCORRECT);
    }
    return user;
  }

  private EmailMessage createEmailMessage(User user, String resetPasswordLink) {
    EmailMessage emailMessage = new EmailMessage();
    emailMessage.setReceiver(user.getEmail());
    String passwordResetToken = generateUUID();
    String[] passwordResetLink = new String[]{resetPasswordLink + passwordResetToken};
    String mailBody = messageService.message("passwordreset.email.body", (Object[])passwordResetLink);
    emailMessage.setContent(mailBody);

    userRepository.insertPasswordResetToken(user, passwordResetToken);

    return emailMessage;
  }

  private EmailMessage accountCreatedEmailMessage(User user, String resetPasswordLink) {
    EmailMessage emailMessage = createEmailMessage(user, resetPasswordLink);
    emailMessage.setSubject(messageService.message("accountcreated.email.subject"));
    return emailMessage;
  }

  private EmailMessage forgotPasswordEmailMessage(User user, String resetPasswordLink) {
    EmailMessage emailMessage = createEmailMessage(user, resetPasswordLink);
    emailMessage.setSubject(messageService.message("forgotpassword.email.subject"));
    return emailMessage;
  }

  private String generateUUID() {
    return Encoder.hash(UUID.randomUUID().toString());
  }

  public List<User> searchUser(String userSearchParam) {
    return userRepository.searchUser(userSearchParam);
  }

  public User getById(Long id) {
    User user = userRepository.getById(id);
    user.setHomeFacilityRoles(roleAssignmentService.getHomeFacilityRoles(id));
    user.setSupervisorRoles(roleAssignmentService.getSupervisorRoles(id));
    user.setAdminRole(roleAssignmentService.getAdminRole(id));
    return user;
  }

  public Long getUserIdByPasswordResetToken(String token) {
    Long userId = userRepository.getUserIdForPasswordResetToken(token);
    if (userId == null) {
      throw new DataException(PASSWORD_RESET_TOKEN_INVALID);
    }
    return userId;
  }

  public void updateUserPassword(String token, String password) {
    Long userId = getUserIdByPasswordResetToken(token);
    userRepository.updateUserPassword(userId, Encoder.hash(password));
    userRepository.deletePasswordResetTokenForUser(userId);
  }

  public User getByUsernameAndVendorId(User user) {
    return userRepository.getByUsernameAndVendorId(user);
  }

  public User selectUserByUserNameAndPassword(String userName, String password) {
    return userRepository.selectUserByUserNameAndPassword(userName, password);
  }


  private void prepareForEmailNotification(User user, String passwordResetLink) {
    EmailMessage emailMessage = accountCreatedEmailMessage(user, passwordResetLink);
    userRepository.insertEmailNotification(emailMessage);
  }

}