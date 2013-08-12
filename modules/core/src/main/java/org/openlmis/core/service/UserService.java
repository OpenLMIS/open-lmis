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
import org.openlmis.email.exception.EmailException;
import org.openlmis.email.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
  static final String USER_EMAIL_NOT_FOUND = "user.email.not.found";
  static final String USER_EMAIL_INCORRECT = "user.email.incorrect";
  static final String PASSWORD_RESET_TOKEN_INVALID = "user.password.reset.token.invalid";
  static final String USER_USERNAME_INCORRECT = "user.username.incorrect";

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private EmailService emailService;
  @Autowired
  private RoleAssignmentService roleAssignmentService;
  @Autowired
  private MessageService messageService;

  @Transactional
  public void create(User user, String resetPasswordLink) {
    save(user);
    sendUserCreationEmail(user, resetPasswordLink);
  }

  public void createUser(User user, String passwordResetLink) {
    save(user);
    prepareForEmailNotification(user, passwordResetLink);
  }

  private void sendUserCreationEmail(User user, String resetPasswordLink) {
    String subject = messageService.message("accountcreated.email.subject");
    SimpleMailMessage emailMessage = createEmailMessage(user, resetPasswordLink, subject);
    sendEmail(emailMessage);
  }

  private void save(User user) {
    user.validate();
    userRepository.create(user);
    roleAssignmentService.saveRolesForUser(user);
  }

  @Transactional
  public void update(User user) {
    user.validate();
    userRepository.update(user);
    roleAssignmentService.saveRolesForUser(user);
  }

  private void sendEmail(SimpleMailMessage emailMessage) {
    try {
      emailService.send(emailMessage);
    } catch (EmailException e) {
      throw new DataException(USER_EMAIL_NOT_FOUND);
    }
  }

  public void sendForgotPasswordEmail(User user, String resetPasswordLink) {
    user = getValidatedUser(user);

    userRepository.deletePasswordResetTokenForUser(user.getId());

    String subject = messageService.message("forgotpassword.email.subject");
    SimpleMailMessage emailMessage = createEmailMessage(user, resetPasswordLink, subject);

    sendEmail(emailMessage);
  }

  private User getValidatedUser(User user) {
    if (user.getEmail() != null && !user.getEmail().equals("")) {
      user = userRepository.getByEmail(user.getEmail());
      if (user == null || !user.getActive()) throw new DataException(USER_EMAIL_INCORRECT);
    } else {
      user = userRepository.getByUsernameAndVendorId(user);
      if (user == null || !user.getActive()) throw new DataException(USER_USERNAME_INCORRECT);
    }
    return user;
  }

  private SimpleMailMessage createEmailMessage(User user, String resetPasswordLink, String subject) {
    String passwordResetToken = generateUUID();
    String[] passwordResetLink = new String[]{user.getUserName(), resetPasswordLink + passwordResetToken};
    String mailBody = messageService.message("passwordreset.email.body", (Object[]) passwordResetLink);

    userRepository.insertPasswordResetToken(user, passwordResetToken);

    SimpleMailMessage emailMessage = new SimpleMailMessage();
    emailMessage.setSubject(subject);
    emailMessage.setText(mailBody);
    emailMessage.setTo(user.getEmail());

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
    user.setAllocationRoles(roleAssignmentService.getAllocationRoles(id));
    return user;
  }

  public Long getUserIdByPasswordResetToken(String token) {
    Long userId = userRepository.getUserIdForPasswordResetToken(token);
    if (userId == null) {
      throw new DataException(PASSWORD_RESET_TOKEN_INVALID);
    }
    return userId;
  }

  @Transactional
  public void updateUserPassword(String token, String password) {
    Long userId = getUserIdByPasswordResetToken(token);
    userRepository.updateUserPasswordAndActivate(userId, Encoder.hash(password));
    userRepository.deletePasswordResetTokenForUser(userId);
  }

  public User getByUsernameAndVendorId(User user) {
    return userRepository.getByUsernameAndVendorId(user);
  }

  public User selectUserByUserNameAndPassword(String userName, String password) {
    return userRepository.selectUserByUserNameAndPassword(userName, password);
  }


  private void prepareForEmailNotification(User user, String passwordResetLink) {
    String subject = messageService.message("accountcreated.email.subject");
    SimpleMailMessage emailMessage = createEmailMessage(user, passwordResetLink, subject);
    userRepository.insertEmailNotification(emailMessage);
  }

  public void updateUserPassword(Long userId, String password) {
    userRepository.updateUserPassword(userId, Encoder.hash(password));
  }

  @Transactional
  public void disable(Long userId, Long modifiedBy) {
    userRepository.disable(userId, modifiedBy);
    userRepository.deletePasswordResetTokenForUser(userId);
  }
}