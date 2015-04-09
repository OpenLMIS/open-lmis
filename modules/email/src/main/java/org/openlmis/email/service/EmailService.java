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
import org.apache.velocity.app.VelocityEngine;
import org.openlmis.email.domain.OpenlmisEmailMessage;
import org.openlmis.email.repository.EmailNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.annotation.Payload;
import org.springframework.mail.MailMessage;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

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

  private Boolean mailSendingFlag;

  private JavaMailSenderImpl mailSender;

  private EmailNotificationRepository repository;

  @Autowired
  public EmailService(@Qualifier("mailSender")  JavaMailSenderImpl mailSender, EmailNotificationRepository repository, @Value("#{mail.sending.flag}") Boolean mailSendingFlag) {
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

  public void processEmails(@Payload List<OpenlmisEmailMessage> mailMessage) {
    if (!mailSendingFlag) {
      return;
    }
    for(final OpenlmisEmailMessage oMessage: mailMessage){
      if(oMessage.getIsHtml()){
        mailSender.send(new MimeMessagePreparator() {
          public void prepare(MimeMessage mimeMessage) throws MessagingException {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            message.setTo(oMessage.getTo());
            message.setSubject(oMessage.getSubject());
            message.setText(oMessage.getText(), true);
          }
        });
      }else{
        mailSender.send(oMessage);
      }
    }

  }

  @Async
  public void processEmailsAsync(List<OpenlmisEmailMessage> simpleMailMessage) {
    if (!mailSendingFlag) {
      return;
    }
    processEmails(simpleMailMessage);
  }

  public void queueMessage(SimpleMailMessage message){
    repository.queueMessage(message);
  }

  public void queueHtmlMessage(String to, String subject, String template, Map model){
    StringWriter writer = new StringWriter();
    VelocityContext context = new VelocityContext();
    context.put("model", model);
    try {
      Velocity.evaluate(context, writer, "velocity", template);
    }catch(Exception exp)
    {

    }
    repository.queueMessage(to, writer.toString(), subject, true);
  }
}
