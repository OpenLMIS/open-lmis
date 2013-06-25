/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.domain;


import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.openlmis.db.categories.UnitTests;

import java.util.Calendar;
import java.util.Date;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static junit.framework.Assert.fail;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.defaultProcessingPeriod;
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

  private Date oneMonthPast(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime((Date) date.clone());
    calendar.add(Calendar.MONTH, 1);
    return calendar.getTime();
  }
}
