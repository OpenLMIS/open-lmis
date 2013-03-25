/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.email.service;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openlmis.email.builder.EmailMessageBuilder;
import org.openlmis.email.domain.EmailMessage;
import org.openlmis.email.exception.EmailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.concurrent.ExecutionException;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.junit.Assert.assertTrue;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class EmailServiceTest {

  @Rule
  public ExpectedException expectedException = none();


  @Test
  public void shouldSendEmailMessage() throws Exception {
    JavaMailSender mailSender = mock(JavaMailSender.class);
    SimpleMailMessage mailMessage = mock(SimpleMailMessage.class);
    EmailService service = new EmailService(mailSender);
    EmailMessage message = make(a(EmailMessageBuilder.defaultEmailMessage,
      with(EmailMessageBuilder.to, "alert.open.lmis@gmail.com")));
    boolean status = service.send(message).get();
    assertTrue(status);
    verify(mailSender).send(any(SimpleMailMessage.class));
  }

  @Test
  public void shouldGiveErrorIfMessageToNotSet() throws ExecutionException, InterruptedException {
    JavaMailSender mailSender = mock(JavaMailSender.class);
    SimpleMailMessage mailMessage = mock(SimpleMailMessage.class);
    EmailService service = new EmailService(mailSender);
    EmailMessage message = make(a(EmailMessageBuilder.defaultEmailMessage,
      with(EmailMessageBuilder.to, "")));
    expectedException.expect(EmailException.class);
    expectedException.expectMessage("Message 'To' not set");

    service.send(message).get();
  }
}
