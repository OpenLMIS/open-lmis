/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 20.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.email.domain;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openlmis.email.exception.EmailException;
import org.springframework.mail.SimpleMailMessage;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.none;
import static org.openlmis.email.builder.EmailMessageBuilder.defaultEmailMessage;

public class EmailMessageTest {

  @Rule
  public ExpectedException expectedException = none();

  @Test
  public void shouldCreateSimpleMailMessage() throws Exception {
    EmailMessage emailMessage = make(a(defaultEmailMessage));
    SimpleMailMessage simpleMailMessage = emailMessage.createSimpleMailMessage();
    assertThat(simpleMailMessage.getTo()[0], is(emailMessage.getReceiver()));
    assertThat(simpleMailMessage.getSubject(), is(emailMessage.getSubject()));
    assertThat(simpleMailMessage.getText(), is(emailMessage.getContent()));
  }

  @Test
  public void shouldThrowExceptionWhileCreatingSimpleMailMessageWhenReceiverIsNull() throws Exception {
    EmailMessage emailMessage = make(a(defaultEmailMessage));
    emailMessage.setReceiver(null);
    expectedException.expect(EmailException.class);
    expectedException.expectMessage("Message 'To' not set");
    emailMessage.createSimpleMailMessage();
  }

  @Test
  public void shouldThrowExceptionWhileCreatingSimpleMailMessageWhenReceiverIsEmptyString() throws Exception {
    EmailMessage emailMessage = make(a(defaultEmailMessage));
    emailMessage.setReceiver("");
    expectedException.expect(EmailException.class);
    expectedException.expectMessage("Message 'To' not set");
    emailMessage.createSimpleMailMessage();
  }
}