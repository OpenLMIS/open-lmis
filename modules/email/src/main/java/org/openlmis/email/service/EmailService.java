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

import lombok.NoArgsConstructor;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.openlmis.email.domain.EmailAttachment;
import org.openlmis.email.domain.EmailMessage;
import org.openlmis.email.repository.EmailNotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * Exposes the services for processing emails and sending them using MailSender.
 */

@Service
@NoArgsConstructor
public class EmailService {

  private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

  private Boolean mailSendingFlag;

  private JavaMailSenderImpl mailSender;

  @Value("${mail.sender.from}")
  private String fromAddress;

  private EmailNotificationRepository repository;

  @Autowired
  public EmailService(@Qualifier("mailSender") JavaMailSenderImpl mailSender, EmailNotificationRepository repository,
                      @Value("${mail.sending.flag}") Boolean mailSendingFlag) {
    this.mailSender = mailSender;
    this.mailSendingFlag = mailSendingFlag;
    this.repository = repository;
  }

  @Async
  public Future<Boolean> send(SimpleMailMessage emailMessage) {
    if (!mailSendingFlag) {
      return new AsyncResult<>(true);
    }
    mailSender.send(emailMessage);
    return new AsyncResult<>(true);
  }

  public void processEmails(@Payload List<EmailMessage> mailMessage) {
    if (!mailSendingFlag) {
      return;
    }
    for (final EmailMessage oMessage : mailMessage) {
      initEmailAttachment(oMessage);

      if (oMessage.isHtml()) {
        mailSender.send(setUpMimeMessage(oMessage));
      } else {
        oMessage.setFrom(fromAddress);
        mailSender.send(oMessage);
      }
    }

  }

  private void initEmailAttachment(EmailMessage oMessage) {
    List<EmailAttachment> attachments = repository.getEmailAttachmentsByEmailId(oMessage.getId());
    if (attachments != null) {
      oMessage.setEmailAttachments(attachments);
    }
  }

  private MimeMessage setUpMimeMessage(final EmailMessage oMessage) {
    MimeMessage mimeMessage = mailSender.createMimeMessage();
    MimeMessageHelper messageHelper = null;
    try {
      messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
      messageHelper.setText(oMessage.getText(), true);
      messageHelper.setFrom(fromAddress);
      messageHelper.setTo(oMessage.getTo());
      messageHelper.setSubject(oMessage.getSubject());

      List<EmailAttachment> emailAttachments = oMessage.getEmailAttachments();
      if (emailAttachments != null) {
        for (EmailAttachment attachment : emailAttachments) {
          messageHelper.addAttachment(attachment.getAttachmentName(), attachment.getFileDataSource());
        }
      }
    } catch (MessagingException e) {
      logger.error("Error setup the email!" + e.getMessage());
    }

    return messageHelper.getMimeMessage();
  }

  @Async
  public void processEmailsAsync(List<EmailMessage> simpleMailMessage) {
    if (!mailSendingFlag) {
      return;
    }
    processEmails(simpleMailMessage);
  }

  public void queueMessage(SimpleMailMessage message) {
    repository.queueMessage(message);
  }

  public EmailMessage queueEmailMessage(EmailMessage message) {
    return repository.queueEmailMessage(message);
  }

  public List<EmailAttachment> insertEmailAttachmentList(List<EmailAttachment> attachments) {
    for (EmailAttachment attachment : attachments) {
      repository.insertEmailAttachment(attachment);
    }
    return attachments;
  }

  public void queueHtmlMessage(String to, String subject, String template, Map model) {
    StringWriter writer = new StringWriter();
    VelocityContext context = new VelocityContext();
    context.put("model", model);
    try {
      Velocity.evaluate(context, writer, "velocity", template);
    } catch (Exception exp) {
      logger.error("Velocity had some errors generating this email. The exception was .... ", exp);
    }
    repository.queueMessage(to, writer.toString(), subject, true);
  }

  public void sendMimeMessage(final String to, final String subject, final String messageBody,
                              final String attachmentFileName, final DataSource dataSource) {
    EmailMessage emailMessage = new EmailMessage();
    emailMessage.setTo(to);
    emailMessage.setSubject(subject);
    emailMessage.setText(messageBody);

    EmailAttachment emailAttachment = new EmailAttachment();
    emailAttachment.setAttachmentName(attachmentFileName);
    emailAttachment.setFileDataSource(dataSource);
    emailMessage.addEmailAttachment(emailAttachment);

    mailSender.send(setUpMimeMessage(emailMessage));
  }
}
