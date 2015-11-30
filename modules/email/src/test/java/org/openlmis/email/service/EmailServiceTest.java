/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.email.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.email.domain.EmailAttachment;
import org.openlmis.email.domain.EmailMessage;
import org.openlmis.email.repository.EmailNotificationRepository;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.openlmis.email.builder.EmailMessageBuilder.defaultEmailMessage;
import static org.openlmis.email.builder.EmailMessageBuilder.receiver;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest(EmailService.class)
public class EmailServiceTest {
  @Mock
  boolean mailSendingFlag = true;

  @Rule
  public ExpectedException expectedException = none();

  @Mock
  JavaMailSenderImpl mailSender;

  @Mock
  EmailNotificationRepository repository;

  @InjectMocks
  private EmailService service;

  @Before
  public void setUp() throws Exception {
    repository = mock(EmailNotificationRepository.class);
  }

  @Test
  public void shouldSendEmailMessage() throws Exception {
    SimpleMailMessage message = make(a(defaultEmailMessage,
      with(receiver, "alert.open.lmis@gmail.com")));

    service = new EmailService(mailSender, repository, true);
    boolean status = service.send(message).get();
    assertTrue(status);
    verify(mailSender).send(any(SimpleMailMessage.class));
  }

  @Test
  public void shouldNotSendEmailIfMailSendingFlagIsFalse() throws ExecutionException, InterruptedException {
    SimpleMailMessage message = make(a(defaultEmailMessage,
      with(receiver, "alert.open.lmis@gmail.com")));
    EmailService service = new EmailService(mailSender, repository, false);
    boolean status = service.send(message).get();
    assertTrue(status);
  }

  @Test
  public void shouldSendMailsFromAListOfMailMessages() throws Exception {
    EmailService service = new EmailService(mailSender, repository, true);

    EmailMessage mockEmailMessage = mock(EmailMessage.class);
    List<EmailMessage> emailMessages = asList(mockEmailMessage);
    when(mockEmailMessage.isHtml()).thenReturn(false);
    service.processEmails(emailMessages);

    verify(mailSender).send(any(SimpleMailMessage.class));
  }

  @Test
  public void shouldInsertAttachmentListOfMailMessage() throws Exception {
    repository = mock(EmailNotificationRepository.class);
    EmailService service = new EmailService(mailSender, repository, true);

    List<EmailAttachment> attachments = asList(new EmailAttachment(), new EmailAttachment());
    service.insertEmailAttachmentList(attachments);

    verify(repository, times(2)).insertEmailAttachment(any(EmailAttachment.class));
  }

  @Test
  public void shouldSendEmailWithAttachmentIfEmailHasAttachment() throws MessagingException, NoSuchFieldException, IllegalAccessException {
    //not know why can't inject the fromAddress field
    Field from = EmailService.class.getDeclaredField("fromAddress");
    from.setAccessible(true);
    from.set(service, "from");

    EmailMessage email = generateEmailMessage();
    EmailAttachment attachment1 = generateEmailAttachment();
    EmailAttachment attachment2 = generateEmailAttachment();
    when(repository.getEmailAttachmentsByEmailId(email.getId())).thenReturn(asList(attachment1, attachment2));

    MimeMessage mockMessage = mock(MimeMessage.class);
    when(mailSender.createMimeMessage()).thenReturn(mockMessage);

    service.processEmails(asList(email));
    verify(mailSender).send(any(MimeMessage.class));
  }

  private EmailMessage generateEmailMessage() {
    EmailMessage message = new EmailMessage();
    message.setTo("test@dev.org");
    message.setText("The Test Message");
    message.setSubject("test");
    message.setHtml(true);
    return message;
  }

  private EmailAttachment generateEmailAttachment() {
    EmailAttachment attachment = new EmailAttachment();
    attachment.setAttachmentName("test file");
    attachment.setAttachmentPath("/path");
    return attachment;
  }

}
