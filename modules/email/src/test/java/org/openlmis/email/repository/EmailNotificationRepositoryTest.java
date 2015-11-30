package org.openlmis.email.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.email.domain.EmailAttachment;
import org.openlmis.email.domain.EmailMessage;
import org.openlmis.email.repository.mapper.EmailNotificationMapper;
import org.springframework.mail.SimpleMailMessage;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class EmailNotificationRepositoryTest {

  @Mock
  EmailNotificationMapper mapper;

  @InjectMocks
  EmailNotificationRepository repository;

  @Test
  public void shouldQueueMessage() throws Exception {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo("test@gmail.com");
    message.setSubject("the subject");
    message.setText("The main message.");
    repository.queueMessage(message);
    verify(mapper).insert(anyString(), anyString(), anyString(), anyBoolean());
  }

  @Test
  public void shouldInsertEmailMessageWhenNoAttachments() throws Exception {
    EmailMessage message = generateEmailMessage();
    repository.queueEmailMessage(message);
    verify(mapper).insertEmailMessage(message);

  }

  @Test
  public void shouldInsertEmailMessageAndRelationWhenHasAttachments() throws Exception {
    EmailMessage message = generateEmailMessage();
    message.addEmailAttachment(generateEmailAttachment());
    message.addEmailAttachment(generateEmailAttachment());

    repository.queueEmailMessage(message);
    verify(mapper).insertEmailMessage(message);
    verify(mapper, times(2)).insertEmailAttachmentsRelation(anyLong(), anyLong());

  }

  private EmailMessage generateEmailMessage() {
    EmailMessage message = new EmailMessage();
    message.setTo("test@dev.org");
    message.setText("The Test Message");
    message.setSubject("test");
    return message;
  }

  private EmailAttachment generateEmailAttachment() {
    EmailAttachment attachment = new EmailAttachment();
    attachment.setAttachmentName("test file");
    attachment.setAttachmentPath("/path");
    return attachment;
  }
}