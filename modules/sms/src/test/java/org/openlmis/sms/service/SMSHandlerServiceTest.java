/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.sms.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.sms.domain.SMS;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.junit.Assert.assertTrue;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.openlmis.sms.builder.SMSBuilder.defaultSMS;
import static org.openlmis.sms.builder.SMSBuilder.PhoneNumber;

@Category(UnitTests.class)
public class SMSHandlerServiceTest {

  @Rule
  public ExpectedException expectedException = none();

  SMSHandlerService smsSender;

  @Before
  public void setUp() throws Exception {
      smsSender = mock(SMSHandlerService.class);
  }

  @Test
  public void shouldSendSMS() throws Exception {
    SMS sms = make(a(defaultSMS,
      with(PhoneNumber, "12345")));

    boolean status = smsSender.send(sms).get();
    assertTrue(status);
    verify(smsSender).send(any(SMS.class));
  }
}
