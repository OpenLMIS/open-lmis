package org.openlmis.email.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.email.domain.EmailAttachment;
import org.openlmis.email.domain.EmailMessage;
import org.springframework.stereotype.Repository;

import java.util.List;

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

  @Insert("INSERT INTO email_attachments(attachmentName, attachmentPath, attachmentFileType) VALUES (#{attachmentName}, #{attachmentPath}, #{attachmentFileType})")
  @Options(useGeneratedKeys = true)
  Integer insertEmailAttachment(EmailAttachment attachment);

  @Insert("INSERT INTO email_attachments_relation(emailId, attachmentId) VALUES (#{emailId}, #{attachmentId})")
  Integer insertEmailAttachmentsRelation(@Param("emailId") Long emailId, @Param("attachmentId") Long attachmentId);

  @Select("SELECT ea.* FROM email_attachments_relation ear " +
                  "LEFT JOIN email_attachments ea " +
                  "ON ear.attachmentId = ea.id " +
                  "where ear.emailId = #{emailId}")
  List<EmailAttachment> queryEmailAttachmentsByEmailId(Long emailId);
}