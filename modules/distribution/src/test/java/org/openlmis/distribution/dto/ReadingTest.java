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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@Category(UnitTests.class)
public class ReadingTest {
  private static final String[] DATE_FORMATS = {
      "MM/yy", "MM/yyyy", "yy/MM", "yyyy/MM", "dd/MM/yy", "dd/MM/yyyy", "MM/dd/yy", "MM/dd/yyyy", "yy/MM/dd", "yyyy/MM/dd",
      "MM-yy", "MM-yyyy", "yy-MM", "yyyy-MM", "dd-MM-yy", "dd-MM-yyyy", "MM-dd-yy", "MM-dd-yyyy", "yy-MM-dd", "yyyy-MM-dd",
      "MMyy", "MMyyyy", "yyMM", "yyyyMM", "ddMMyy", "ddMMyyyy", "MMddyy", "MMddyyyy", "yyMMdd", "yyyyMMdd"
  };

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

  @Test
  public void shouldReturnPassedReadingInstance() {
    Reading reading = new Reading(1);
    assertThat(Reading.safeRead(reading), is(reading));
  }

  @Test
  public void shouldReturnEmptyIfReadingParamIsNull() {
    assertThat(Reading.safeRead(null), is(Reading.EMPTY));
  }

  @Test
  public void shouldFormatDateByGivenFormat() {
    Calendar calendar = Calendar.getInstance();
    calendar.set(2016, Calendar.JUNE, 23);

    for (String format : DATE_FORMATS) {
      Date time = calendar.getTime();
      Reading date = new Reading(time, format);
      SimpleDateFormat formatter = new SimpleDateFormat(format);

      assertThat(date.getValue(), is(notNullValue()));
      assertThat(date.getNotRecorded(), is(false));
      assertThat(date.getEffectiveValue(), is(formatter.format(time)));
    }
  }

  @Test
  public void shouldParseBooleanValue() {
    assertThat(new Reading(null).parseBoolean(), is(nullValue()));
    assertThat(new Reading(true).parseBoolean(), is(true));
    assertThat(new Reading(false).parseBoolean(), is(false));
  }

  @Test
  public void shouldParseDateValue() {
    Calendar calendar = Calendar.getInstance();
    calendar.set(2016, Calendar.JUNE, 23);
    Date time = calendar.getTime();

    assertThat(new Reading(null).parseDate(), is(nullValue()));

    Date first = new Reading(time.getTime()).parseDate();
    Date second = new Reading("23/06/2016").parseDate();
    Date third = new Reading("2016-06-23").parseDate();

    assertThat(getField(first, Calendar.YEAR), is(2016));
    assertThat(getField(second, Calendar.YEAR), is(2016));
    assertThat(getField(third, Calendar.YEAR), is(2016));

    assertThat(getField(first, Calendar.MONTH), is(Calendar.JUNE));
    assertThat(getField(second, Calendar.MONTH), is(Calendar.JUNE));
    assertThat(getField(third, Calendar.MONTH), is(Calendar.JUNE));

    assertThat(getField(first, Calendar.DAY_OF_MONTH), is(23));
    assertThat(getField(second, Calendar.DAY_OF_MONTH), is(23));
    assertThat(getField(third, Calendar.DAY_OF_MONTH), is(23));
  }

  private int getField(Date time, int field) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(time);

    return calendar.get(field);
  }

}
