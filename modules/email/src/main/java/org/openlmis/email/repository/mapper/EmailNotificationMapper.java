package org.openlmis.email.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailNotificationMapper {

  @Insert("INSERT INTO email_notifications(receiver, subject, content, isHtml , sent) VALUES (#{to}, #{subject}, #{text}, #{isHtml}, false)")
  Integer insert(@Param("to") String receiver, @Param("text") String content, @Param("subject") String subject, @Param("isHtml") Boolean isHtml);
}
