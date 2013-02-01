package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.User;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.UserRepository;
import org.openlmis.email.domain.EmailMessage;
import org.openlmis.email.exception.EmailException;
import org.openlmis.email.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@NoArgsConstructor
public class UserService {

  public static final String USER_REQUEST_URL = "user.request.url";

  public static final String USER_EMAIL_NOT_FOUND = "user.email.not.found";
  public static final String USER_EMAIL_INCORRECT = "user.email.incorrect";
  public static final String PASSWORD_RESET_TOKEN_INVALID = "user.password.reset.token.invalid";
  private static final String USER_USERNAME_INCORRECT = "user.username.incorrect";

  @Value("${accountcreated.email.subject}")
  private String ACCOUNT_CREATED_EMAIL_SUBJECT;

  @Value("${passwordreset.email.body}")
  private String PASSWORD_RESET_CREATED_EMAIL_BODY;

  @Value("${forgotpassword.email.subject}")
  private String FORGOT_PASSWORD_EMAIL_SUBJECT;

  private UserRepository userRepository;

  private EmailService emailService;

  private RoleAssignmentService roleAssignmentService;

  @Autowired
  public UserService(UserRepository userRepository, RoleAssignmentService roleAssignmentService, EmailService emailService) {
    this.userRepository = userRepository;
    this.emailService = emailService;
    this.roleAssignmentService = roleAssignmentService;
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
    roleAssignmentService.insertUserProgramRoleMapping(user);
  }

  public void update(User user) {
    user.validate();
    userRepository.update(user);

    roleAssignmentService.deleteAllRoleAssignmentsForUser(user.getId());
    roleAssignmentService.insertUserProgramRoleMapping(user);
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
    String mailBody = null;
    if (PASSWORD_RESET_CREATED_EMAIL_BODY != null && resetPasswordLink != null) {
      mailBody = PASSWORD_RESET_CREATED_EMAIL_BODY.replace("{0}", resetPasswordLink);
    }

    String passwordResetToken = generateUUID();
    userRepository.insertPasswordResetToken(user, passwordResetToken);

    emailMessage.setText(mailBody + passwordResetToken);
    return emailMessage;
  }

  private EmailMessage accountCreatedEmailMessage(User user, String resetPasswordLink) {
    EmailMessage emailMessage = createEmailMessage(user, resetPasswordLink);
    emailMessage.setSubject(ACCOUNT_CREATED_EMAIL_SUBJECT);
    return emailMessage;
  }

  private EmailMessage forgotPasswordEmailMessage(User user, String requestUrl) {
    EmailMessage emailMessage = createEmailMessage(user, requestUrl);
    emailMessage.setSubject(FORGOT_PASSWORD_EMAIL_SUBJECT);
    emailMessage.setTo(user.getEmail());
    return emailMessage;
  }

  private String generateUUID() {
    return UUID.randomUUID().toString();
  }

  public List<User> searchUser(String userSearchParam) {
    return userRepository.searchUser(userSearchParam);
  }

  public User getById(Integer id) {
    User user = userRepository.getById(id);
    user.setRoleAssignments(roleAssignmentService.getRoleAssignments(id));
    return user;
  }

  public Integer getUserIdForPasswordResetToken(String token) {
    Integer userId = userRepository.getUserIdForPasswordResetToken(token);
    if (userId == null) {
      throw new DataException(PASSWORD_RESET_TOKEN_INVALID);
    }
    return userId;
  }

  public void updateUserPassword(User user) {
    userRepository.updateUserPassword(user);
    userRepository.deletePasswordResetTokenForUser(user);
  }

}
