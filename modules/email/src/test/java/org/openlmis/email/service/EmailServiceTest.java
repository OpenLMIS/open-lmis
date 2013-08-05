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
import org.openlmis.db.categories.UnitTests;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.junit.Assert.assertTrue;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.openlmis.email.builder.EmailMessageBuilder.defaultEmailMessage;
import static org.openlmis.email.builder.EmailMessageBuilder.receiver;

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
    SimpleMailMessage message = make(a(defaultEmailMessage,
      with(receiver, "alert.open.lmis@gmail.com")));

    EmailService service = new EmailService(mailSender, mailSendingFlag);
    boolean status = service.send(message).get();
    assertTrue(status);
    verify(mailSender).send(any(SimpleMailMessage.class));
  }

  @Test
  public void shouldNotSendEmailIfMailSendingFlagIsFalse() throws ExecutionException, InterruptedException {
    Boolean mailSendingFlag = false;
    SimpleMailMessage message = make(a(defaultEmailMessage,
      with(receiver, "alert.open.lmis@gmail.com")));
    EmailService service = new EmailService(mailSender, mailSendingFlag);
    boolean status = service.send(message).get();
    assertTrue(status);
  }

  @Test
  public void shouldSendMailsFromAListOfMailMessages() throws Exception {
    EmailService emailService = new EmailService(mailSender, true);

    SimpleMailMessage mockEmailMessage = mock(SimpleMailMessage.class);
    List<SimpleMailMessage> emailMessages = Arrays.asList(mockEmailMessage);

    emailService.processEmails(emailMessages);

    verify(mailSender).send(new SimpleMailMessage[]{mockEmailMessage});
  }
}
