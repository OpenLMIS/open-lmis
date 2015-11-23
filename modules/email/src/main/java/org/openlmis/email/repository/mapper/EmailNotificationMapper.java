package org.openlmis.email.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectKey;
import org.openlmis.email.domain.EmailMessage;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailNotificationMapper {

  @Insert("INSERT INTO email_notifications(receiver, subject, content, isHtml , sent) VALUES (#{to}, #{subject}, #{text}, #{isHtml}, false)")
  @Deprecated
  Integer insert(@Param("to") String receiver, @Param("text") String content, @Param("subject") String subject, @Param("isHtml")
  Boolean isHtml);

  @SelectKey(statement="SELECT nextval('email_notifications_id_seq')", keyProperty="id", before=true, resultType=long.class)
  @Insert("INSERT INTO email_notifications(receiver, subject, content, isHtml , sent) VALUES ( #{receiver}, #{subject}, #{text}, #{isHtml}, false)")
  Integer insertEmailMessage(EmailMessage emailMessage);
}
