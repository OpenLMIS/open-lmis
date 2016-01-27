/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.sms.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.sms.domain.SMS;
import org.springframework.beans.factory.annotation.Autowired;

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

  SMSService smsSender;

  @Before
  public void setUp() throws Exception {
      smsSender = mock(SMSService.class);
  }

  @Test
  public void shouldSendSMS() throws Exception {
    /*SMS sms = make(a(defaultSMS,
    with(PhoneNumber, "12345")));

    boolean status = smsSender.send(sms).get();
    assertTrue(status);
    verify(smsSender).send(any(SMS.class));*/
  }
}
