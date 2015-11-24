package org.openlmis.email.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.openlmis.email.domain.EmailAttachment;
import org.openlmis.email.domain.EmailMessage;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailNotificationMapper {

  @Insert("INSERT INTO email_notifications(receiver, subject, content, isHtml , sent) VALUES (#{to}, #{subject}, #{text}, #{isHtml}, false)")
  @Deprecated
  Integer insert(@Param("to") String receiver, @Param("text") String content, @Param("subject") String subject, @Param("isHtml")
  Boolean isHtml);

  @Insert("INSERT INTO email_notifications(receiver, subject, content, isHtml , sent) VALUES ( #{receiver}, #{subject}, #{text}, " +
                  "#{isHtml}, false)")
  @Options(useGeneratedKeys = true)
  Integer insertEmailMessage(EmailMessage emailMessage);

  @Insert("INSERT INTO email_attachment(emailId, attachmentName, attachmentPath) VALUES ( #{emailId}, #{attachmentName}, #{attachmentPath})")
  @Options(useGeneratedKeys = true)
  Integer insertEmailAttachment(EmailAttachment attachment);

}