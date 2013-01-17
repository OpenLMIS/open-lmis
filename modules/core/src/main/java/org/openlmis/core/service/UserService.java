package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.User;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.UserRepository;
import org.openlmis.email.domain.EmailMessage;
import org.openlmis.email.exception.EmailException;
import org.openlmis.email.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@NoArgsConstructor
public class UserService {

  public static final String USER_EMAIL_NOT_FOUND = "user.email.not.found";
  public static final String USER_EMAIL_INCORRECT = "user.email.incorrect";
  private static final String USER_USERNAME_INCORRECT = "user.username.incorrect";
  private UserRepository userRepository;

  private EmailService emailService;

  @Autowired
  public UserService(UserRepository userRepository, EmailService emailService) {
    this.userRepository = userRepository;
    this.emailService = emailService;
  }

  public void save(User user) {
    user.validate();
    userRepository.insert(user);
  }

  public void sendForgotPasswordEmail(User user) {
    user = getValidatedUser(user);
    try {
      emailService.send(createEmailMessage(user.getEmail()));
    }catch (EmailException e){
      throw new DataException(USER_EMAIL_NOT_FOUND);
    }
  }

  private User getValidatedUser(User user) {
    if (user.getEmail() != null && !user.getEmail().equals("")) {
      user = userRepository.getByEmail(user.getEmail());
      if(user == null) throw new DataException(USER_EMAIL_INCORRECT);
    } else {
      user = userRepository.getByUsername(user.getUserName());
      if(user == null) throw new DataException(USER_USERNAME_INCORRECT);
    }
    return user;
  }

  private EmailMessage createEmailMessage(String email) {
    EmailMessage emailMessage = new EmailMessage();
    emailMessage.setTo(email);
    emailMessage.setSubject("Forgot Password");
    emailMessage.setText("Hi, Please follow the following link: https://localhost:/UUID=" + generateUUID());
    return emailMessage;
  }

  private String generateUUID() {
    return UUID.randomUUID().toString();
  }

  public List<User> searchUser(String userSearchParam) {
    return userRepository.searchUser(userSearchParam);
  }
}
