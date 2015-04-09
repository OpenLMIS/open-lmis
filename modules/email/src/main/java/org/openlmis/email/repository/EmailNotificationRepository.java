package org.openlmis.email.repository;

import org.openlmis.email.repository.mapper.EmailNotificationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Repository;

@Repository
public class EmailNotificationRepository {

  @Autowired
  EmailNotificationMapper mapper;

  @Deprecated
  public void queueMessage(SimpleMailMessage message){
    mapper.insert(message.getTo()[0],message.getText(), message.getSubject(), false);
  }


  public void queueMessage(String to, String message, String subject, Boolean isHtml){
    mapper.insert(to, message, subject, isHtml);
  }
}
