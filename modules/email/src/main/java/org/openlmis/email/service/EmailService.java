/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.email.service;

import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.openlmis.email.domain.EmailMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.annotation.Payload;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;

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
  public Future<Boolean> send(EmailMessage emailMessage) {
    EmailMessage msg = emailMessage;
    if (!mailSendingFlag) {
      return new AsyncResult(true);
    }
    mailSender.send(emailMessage.createSimpleMailMessage());
    return new AsyncResult(true);
  }

  @ServiceActivator(inputChannel = "inputChannel")
  public void processEmails(@Payload List<EmailMessage> emailMessages) {
    if(!mailSendingFlag) {
      return;
    }
    Collection<SimpleMailMessage> simpleMailMessages = CollectionUtils.collect(emailMessages, new Transformer() {
      @Override
      public Object transform(Object o) {
        return ((EmailMessage) o).createSimpleMailMessage();
      }
    });
    mailSender.send(simpleMailMessages.toArray(new SimpleMailMessage[0]));
  }
}
