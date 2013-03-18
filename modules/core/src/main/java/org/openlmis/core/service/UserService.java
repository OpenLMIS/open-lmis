package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.User;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.hash.Encoder;
import org.openlmis.core.repository.UserRepository;
import org.openlmis.email.domain.EmailMessage;
import org.openlmis.email.exception.EmailException;
import org.openlmis.email.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@NoArgsConstructor
public class UserService {
  public static final String USER_EMAIL_NOT_FOUND = "user.email.not.found";
  public static final String USER_EMAIL_INCORRECT = "user.email.incorrect";
  public static final String PASSWORD_RESET_TOKEN_INVALID = "user.password.reset.token.invalid";
  private static final String USER_USERNAME_INCORRECT = "user.username.incorrect";


  private Object[] noArgs = null;

  private UserRepository userRepository;

  private EmailService emailService;

  private RoleAssignmentService roleAssignmentService;
  private MessageSource messageSource;

  @Autowired
  public UserService(UserRepository userRepository, RoleAssignmentService roleAssignmentService, EmailService emailService, MessageSource messageSource) {
    this.userRepository = userRepository;
    this.emailService = emailService;
    this.roleAssignmentService = roleAssignmentService;
    this.messageSource = messageSource;

  }


  public void create(User user, String resetPasswordLink) {
    validateAndSave(user);
    EmailMessage emailMessage = accountCreatedEmailMessage(user, resetPasswordLink);
    sendEmail(emailMessage);
  }

  public void create(User user) {
    validateAndSave(user);
  }

  private void validateAndSave(User user) {
    user.validate();
    userRepository.create(user);
    roleAssignmentService.saveHomeFacilityRoles(user);
    roleAssignmentService.saveSupervisoryRoles(user);
  }

  public void update(User user) {
    user.validate();
    userRepository.update(user);
    roleAssignmentService.deleteAllRoleAssignmentsForUser(user.getId());
    roleAssignmentService.saveHomeFacilityRoles(user);
    roleAssignmentService.saveSupervisoryRoles(user);
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
      user = userRepository.getByUsername(user.getUserName());
      if (user == null) throw new DataException(USER_USERNAME_INCORRECT);
    }
    return user;
  }

  private EmailMessage createEmailMessage(User user, String resetPasswordLink) {
    EmailMessage emailMessage = new EmailMessage();
    emailMessage.setTo(user.getEmail());
    String passwordResetToken = generateUUID();
    String[] passwordResetLink = new String[]{resetPasswordLink + passwordResetToken};
    String mailBody = messageSource.getMessage("passwordreset.email.body", passwordResetLink, Locale.getDefault());
    emailMessage.setText(mailBody);

    userRepository.insertPasswordResetToken(user, passwordResetToken);

    return emailMessage;
  }

  private EmailMessage accountCreatedEmailMessage(User user, String resetPasswordLink) {
    EmailMessage emailMessage = createEmailMessage(user, resetPasswordLink);
    emailMessage.setSubject(messageSource.getMessage("accountcreated.email.subject", noArgs, Locale.getDefault()));
    return emailMessage;
  }

  private EmailMessage forgotPasswordEmailMessage(User user, String resetPasswordLink) {
    EmailMessage emailMessage = createEmailMessage(user, resetPasswordLink);
    emailMessage.setSubject(messageSource.getMessage("forgotpassword.email.subject", noArgs, Locale.getDefault()));
    return emailMessage;
  }

  private String generateUUID() {
    return Encoder.hash(UUID.randomUUID().toString());
  }

  public List<User> searchUser(String userSearchParam) {
    return userRepository.searchUser(userSearchParam);
  }

  public User getById(Integer id) {
    User user = userRepository.getById(id);
    user.setHomeFacilityRoles(roleAssignmentService.getHomeFacilityRoles(id));
    user.setSupervisorRoles(roleAssignmentService.getSupervisorRoles(id));
    return user;
  }

  public Integer getUserIdByPasswordResetToken(String token) {
    Integer userId = userRepository.getUserIdForPasswordResetToken(token);
    if (userId == null) {
      throw new DataException(PASSWORD_RESET_TOKEN_INVALID);
    }
    return userId;
  }

  public void updateUserPassword(String token, String password) {
    Integer userId = getUserIdByPasswordResetToken(token);
    userRepository.updateUserPassword(userId, Encoder.hash(password));
    userRepository.deletePasswordResetTokenForUser(userId);
  }

}
