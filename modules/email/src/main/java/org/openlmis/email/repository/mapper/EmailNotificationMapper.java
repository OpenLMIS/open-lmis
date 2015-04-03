package org.openlmis.email.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailNotificationMapper {

  @Insert("INSERT INTO email_notifications(receiver, subject, content, sent) VALUES (#{to}, #{subject}, #{text}, false)")
  Integer insert(SimpleMailMessage message);
}
