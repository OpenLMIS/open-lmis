/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.domain;


import org.apache.commons.lang.time.DateUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.openlmis.db.categories.UnitTests;

import java.util.Calendar;
import java.util.Date;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static junit.framework.Assert.fail;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.defaultProcessingPeriod;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.endDate;
import static org.openlmis.core.matchers.Matchers.dataExceptionMatcher;

@Category(UnitTests.class)
public class ProcessingPeriodTest {

  @Rule
  public ExpectedException exException = ExpectedException.none();

  @Test
  public void shouldNotThrowErrorOnValidateForAValidPeriod() {

    ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod));
    try {
      processingPeriod.validate();
    } catch (Exception e) {
      fail("Processing Period Validation failed where unexpected");
    }
  }

  @Test
  public void shouldNotThrowErrorOnValidateForAPeriodWithNoName() {
    ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod));
    processingPeriod.setName(null);

    exException.expect(dataExceptionMatcher("error.period.without.name"));

    processingPeriod.validate();
  }

  @Test
  public void shouldNotThrowErrorOnValidateForAPeriodWithNoScheduleId() {
    ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod));
    processingPeriod.setScheduleId(null);

    exException.expect(dataExceptionMatcher("error.period.without.schedule"));

    processingPeriod.validate();
  }

  @Test
  public void shouldNotThrowErrorOnValidateForAPeriodWithNoStartDate() {
    ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod));
    processingPeriod.setStartDate(null);

    exException.expect(dataExceptionMatcher("error.period.without.start.date"));

    processingPeriod.validate();
  }

  @Test
  public void shouldNotThrowErrorOnValidateForAPeriodWithNoEndDate() {
    ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod));
    processingPeriod.setEndDate(null);

    exException.expect(dataExceptionMatcher("error.period.without.end.date"));

    processingPeriod.validate();
  }

  @Test
  public void shouldNotThrowErrorOnValidateForAPeriodWithEndDateEarlierToStartDate() {
    ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod));
    processingPeriod.setStartDate(oneMonthPast(processingPeriod.getEndDate()));

    exException.expect(dataExceptionMatcher("error.period.invalid.dates"));

    processingPeriod.validate();
  }

  @Test
  public void shouldIncludeEndDateInPeriod() throws Exception {
    ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod));
    processingPeriod.includeEntireDuration();

    assertThat(processingPeriod.getStartDate(), is(DateUtils.parseDate("01-01-12 00:00:00", new String[]{"dd-MM-yy HH:mm:ss"})));
    assertThat(processingPeriod.getEndDate(), is(DateUtils.parseDate("01-02-12 23:59:59", new String[]{"dd-MM-yy HH:mm:ss"})));
  }

  @Test
  public void shouldGetNextPeriodStartDate() throws Exception {
    ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod, with(endDate, DateUtils.parseDate("01-01-12 00:00:00", new String[]{"dd-MM-yy HH:mm:ss"}))));

    assertThat(processingPeriod.getNextStartDate(), is("2012-01-02"));
  }

  private Date oneMonthPast(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime((Date) date.clone());
    calendar.add(Calendar.MONTH, 1);
    return calendar.getTime();
  }
}
