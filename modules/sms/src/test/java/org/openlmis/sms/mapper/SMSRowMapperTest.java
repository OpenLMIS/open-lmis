/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.sms.mapper;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.sms.domain.SMS;

import java.sql.ResultSet;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class SMSRowMapperTest {

  @Mock
  private ResultSet resultSet;

  @InjectMocks
  SMSRowMapper smsRowMapper;

  @Test
  public void shouldCreateSMSFromResultSet() throws Exception {
    when(resultSet.getString("PhoneNumber")).thenReturn("PhoneNumber");
    when(resultSet.getString("Message")).thenReturn("Message");
    when(resultSet.getString("Direction")).thenReturn("Direction");

    SMS sms = smsRowMapper.mapRow(resultSet, 1);

    assertThat(sms.getPhoneNumber(), is("PhoneNumber"));
    assertThat(sms.getMessage(), is("Message"));
    assertThat(sms.getDirection(), is("Direction"));
  }
}
