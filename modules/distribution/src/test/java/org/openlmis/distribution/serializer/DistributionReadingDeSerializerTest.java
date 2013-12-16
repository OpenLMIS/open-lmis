/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.distribution.serializer;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.map.DeserializationContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.distribution.dto.Reading;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
public class DistributionReadingDeSerializerTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private JsonNode jsonNode = mock(JsonNode.class);
  private JsonParser jp;

  @Before
  public void setUp() throws Exception {
    jp = mock(JsonParser.class);
    ObjectCodec objectCodec = mock(ObjectCodec.class);
    when(jp.getCodec()).thenReturn(objectCodec);
    when(objectCodec.readTree(jp)).thenReturn(jsonNode);
  }

  @Test
  public void shouldReturnReadingIfValueIsNotEmptyAndNRIsNull() throws Exception {
    JsonNode valueNode = mock(JsonNode.class);
    when(valueNode.getTextValue()).thenReturn("55");
    when(jsonNode.get("value")).thenReturn(valueNode);
    when(jsonNode.get("notRecorded")).thenReturn(null);

    Reading reading = new DistributionReadingDeSerializer().deserialize(jp, mock(DeserializationContext.class));
    assertThat(reading.getValue(), is("55"));
  }

  @Test
  public void shouldReturnReadingIfValueIsNotEmptyAndNRIsFalse() throws Exception {
    JsonNode valueNode = mock(JsonNode.class);
    when(jsonNode.get("value")).thenReturn(valueNode);
    when(valueNode.getTextValue()).thenReturn("55");

    JsonNode notRecordedNode = mock(JsonNode.class);
    when(jsonNode.get("notRecorded")).thenReturn(notRecordedNode);
    when(notRecordedNode.getBooleanValue()).thenReturn(false);

    Reading reading = new DistributionReadingDeSerializer().deserialize(jp, mock(DeserializationContext.class));
    assertThat(reading.getValue(), is("55"));
    assertThat(reading.getNotRecorded(), is(false));
  }
}
