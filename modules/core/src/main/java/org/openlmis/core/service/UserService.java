package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.User;
import org.openlmis.core.repository.UserRepository;
import org.openlmis.email.domain.EmailMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@NoArgsConstructor
public class UserService {

  private UserRepository userRepository;

//  private EmailService emailService;

  @Autowired
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
//    this.emailService= emailService;
  }

  public void save(User user) {
    user.validate();
    userRepository.insert(user);
  }

  public void sendForgotPasswordEmail(User user) {
    User userReturned;

    if(user.getEmail()!= null){
      userReturned = userRepository.getByEmail(user.getEmail());
    }else{
      userReturned = userRepository.getByUsername(user.getUserName());
    }

//    emailService.send(createEmailMessage(user));


  }

  private EmailMessage createEmailMessage(User user){
    EmailMessage emailMessage = new EmailMessage();
    emailMessage.setTo(user.getEmail());
    emailMessage.setSubject("Forgot Password");
    emailMessage.setText("Hi, Please follow the following link:https://localhost:/UUID="+generateUUID());
    return emailMessage;
  }

  private String generateUUID(){
    return UUID.randomUUID().toString();
  }

}
