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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.annotation.Payload;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Future;

/**
 * Exposes the services for processing emails and sending them using MailSender.
 */

@Service
@NoArgsConstructor
public class EmailService {

  private Boolean mailSendingFlag;

  private MailSender mailSender;

  @Autowired
  public EmailService(MailSender mailSender, @Value("${mail.sending.flag}") Boolean mailSendingFlag) {
    this.mailSender = mailSender;
    this.mailSendingFlag = mailSendingFlag;
  }

  @Async
  public Future<Boolean> send(SimpleMailMessage emailMessage) {
    if (!mailSendingFlag) {
      return new AsyncResult<>(true);
    }
    mailSender.send(emailMessage);
    return new AsyncResult<>(true);
  }

  public void processEmails(@Payload List<SimpleMailMessage> simpleMailMessage) {
    if (!mailSendingFlag) {
      return;
    }

    mailSender.send(simpleMailMessage.toArray(new SimpleMailMessage[simpleMailMessage.size()]));
  }
}
