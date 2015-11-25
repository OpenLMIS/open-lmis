package org.openlmis.email.repository;

import org.openlmis.email.domain.EmailAttachment;
import org.openlmis.email.domain.EmailMessage;
import org.openlmis.email.repository.mapper.EmailNotificationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EmailNotificationRepository {

  @Autowired
  EmailNotificationMapper mapper;

  @Deprecated
  public void queueMessage(SimpleMailMessage message) {
    mapper.insert(message.getTo()[0], message.getText(), message.getSubject(), false);
  }

  @Deprecated
  public void queueMessage(String to, String message, String subject, Boolean isHtml) {
    mapper.insert(to, message, subject, isHtml);
  }

  public EmailMessage queueEmailMessage(EmailMessage message) {
    mapper.insertEmailMessage(message);
    for (EmailAttachment attachment : message.getEmailAttachments()) {
      mapper.insertEmailAttachmentsRelation(message.getId(), attachment.getId());
    }
    return message;
  }

  public EmailAttachment insertEmailAttachment(EmailAttachment attachment) {
    mapper.insertEmailAttachment(attachment);
    return attachment;
  }

  public List<EmailAttachment> getEmailAttachmentsByEmailId(Long id) {
    return mapper.queryEmailAttachmentsByEmailId(id);
  }
}
