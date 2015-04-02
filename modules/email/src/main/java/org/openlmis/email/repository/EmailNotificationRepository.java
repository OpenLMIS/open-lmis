package org.openlmis.email.repository;

import org.openlmis.email.domain.OpenlmisEmailMessage;
import org.openlmis.email.repository.mapper.EmailNotificationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Repository;

@Repository
public class EmailNotificationRepository {

  @Autowired
  EmailNotificationMapper mapper;

  public void queueMessage(SimpleMailMessage message){
    mapper.insert(message);
  }

}
