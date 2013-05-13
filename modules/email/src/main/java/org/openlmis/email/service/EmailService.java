/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.email.service;

import lombok.NoArgsConstructor;
import org.openlmis.email.domain.EmailMessage;
import org.openlmis.email.exception.EmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

@Service
@NoArgsConstructor
public class EmailService {


  private Boolean mailSendingFlag;


  private MailSender mailSender;

  @Autowired
  public EmailService(MailSender mailSender,@Value("${mail.sending.flag}") Boolean mailSendingFlag) {
    this.mailSender = mailSender;
    this.mailSendingFlag = mailSendingFlag;
  }

  @Async
  public Future<Boolean> send(EmailMessage emailMessage) {
    if(!mailSendingFlag)
      return new AsyncResult(true);
    mailSender.send(copyToSimpleMailMessage(emailMessage));
    return new AsyncResult(true);
  }

  private SimpleMailMessage copyToSimpleMailMessage(EmailMessage message) {
    if (message.getTo() == null || message.getTo().equals("")) throw new EmailException("Message 'To' not set");

    SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
    simpleMailMessage.setSubject(message.getSubject());
    simpleMailMessage.setText(message.getText());
    simpleMailMessage.setTo(message.getTo());

    return simpleMailMessage;
  }
}
