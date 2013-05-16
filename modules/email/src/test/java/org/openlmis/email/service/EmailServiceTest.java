/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.email.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.email.builder.EmailMessageBuilder;
import org.openlmis.email.domain.EmailMessage;
import org.openlmis.email.exception.EmailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.concurrent.ExecutionException;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.junit.Assert.assertTrue;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
@Category(UnitTests.class)
public class EmailServiceTest {

  @Rule
  public ExpectedException expectedException = none();

  private JavaMailSender mailSender;


  @Before
  public void setUp() throws Exception {
    mailSender = mock(JavaMailSenderImpl.class);
  }

  @Test
  public void shouldSendEmailMessage() throws Exception {
    Boolean mailSendingFlag = true;
    EmailMessage message = make(a(EmailMessageBuilder.defaultEmailMessage,
      with(EmailMessageBuilder.to, "alert.open.lmis@gmail.com")));
    EmailService service = new EmailService(mailSender, mailSendingFlag);
    boolean status = service.send(message).get();
    assertTrue(status);
    verify(mailSender).send(any(SimpleMailMessage.class));
  }



  @Test
  public void shouldGiveErrorIfMessageToNotSet() throws ExecutionException, InterruptedException {
    EmailMessage message = make(a(EmailMessageBuilder.defaultEmailMessage,
      with(EmailMessageBuilder.to, "")));
    expectedException.expect(EmailException.class);
    expectedException.expectMessage("Message 'To' not set");
    Boolean mailSendingFlag = true;
    EmailService service = new EmailService(mailSender, mailSendingFlag);
    service.send(message).get();
  }

  @Test
  public void shouldNotSendEmailIfMailSendingFlagIsFalse() throws ExecutionException, InterruptedException {
    Boolean mailSendingFlag = false;
    EmailMessage message = make(a(EmailMessageBuilder.defaultEmailMessage,
      with(EmailMessageBuilder.to, "alert.open.lmis@gmail.com")));
    EmailService service = new EmailService(mailSender, mailSendingFlag);
    boolean status = service.send(message).get();
    assertTrue(status);
  }
}
