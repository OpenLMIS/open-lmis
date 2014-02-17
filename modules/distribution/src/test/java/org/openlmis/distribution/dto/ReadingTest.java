/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.distribution.dto;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.openlmis.core.exception.DataException;
import org.openlmis.db.categories.UnitTests;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@Category(UnitTests.class)
public class ReadingTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void shouldReturnValueIfNRisFalse() throws Exception {
    Reading reading = new Reading("145", false);
    assertThat(reading.getEffectiveValue(), is("145"));
  }

  @Test
  public void shouldReturnNullIfNRisTrue() throws Exception {
    Reading reading = new Reading("", true);
    assertThat(reading.getEffectiveValue(), is(nullValue()));
  }

  @Test
  public void shouldSetNRToTrueIfValueIsNullAndNRIsFalse() throws Exception {
    Reading reading = new Reading(null, false);

    assertTrue(reading.getNotRecorded());
  }

  @Test
  public void shouldSetNRToTrueIfValueIsEmptyAndNRIsFalse() throws Exception {
    Reading reading = new Reading("", false);

    assertTrue(reading.getNotRecorded());
  }

  @Test
  public void shouldParseStringToInteger() throws Exception {
    Reading reading = new Reading("345", false);

    assertThat(reading.parsePositiveInt(), is(345));
  }

  @Test
  public void shouldThrowErrorIfValueIsNegative() throws Exception {
    Reading reading = new Reading("-345", false);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.epi.use.line.item.invalid");

    reading.parsePositiveInt();
  }
}
