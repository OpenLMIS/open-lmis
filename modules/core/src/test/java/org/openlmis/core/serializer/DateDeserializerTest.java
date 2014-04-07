/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.core.serializer;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.db.categories.UnitTests;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Date;
import java.util.TimeZone;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest(TimeZone.class)
public class DateDeserializerTest {


  JsonParser jsonParser = mock(JsonParser.class);

  DeserializationContext deserializationContext = mock(DeserializationContext.class);

  DateDeserializer dateDeserializer = new DateDeserializer();

  @Test
  public void shouldConvertLongToDate() throws IOException {
    Date date = new Date();
    when(jsonParser.getText()).thenReturn(String.valueOf(date.getTime()));

    Date deserializedDate = dateDeserializer.deserialize(jsonParser, deserializationContext);

    assertThat(deserializedDate.getTime(), is(date.getTime()));
  }

  @Test
  public void shouldConvertStringToDate() throws IOException {
    String date = "1985-11-01";
    when(jsonParser.getText()).thenReturn(date);
    Date deserializedDate = dateDeserializer.deserialize(jsonParser, deserializationContext);

    assertThat(deserializedDate.toString().contains("Fri Nov 01 00:00:00"), is(true));
  }
}
