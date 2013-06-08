/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.email.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.email.exception.EmailException;
import org.springframework.mail.SimpleMailMessage;

import static org.apache.commons.lang.StringUtils.isEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailMessage {
  private Long id;
  private String receiver;
  private String subject;
  private String content;

  public EmailMessage(String receiver, String subject, String content) {
    this.receiver = receiver;
    this.subject = subject;
    this.content = content;
  }

  public SimpleMailMessage createSimpleMailMessage() {
    if (isEmpty(this.receiver)) throw new EmailException("Message 'To' not set");

    SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
    simpleMailMessage.setSubject(this.subject);
    simpleMailMessage.setText(this.content);
    simpleMailMessage.setTo(this.receiver);

    return simpleMailMessage;
  }
}
