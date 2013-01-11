package org.openlmis.core.domain;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openlmis.core.builder.ProcessingPeriodBuilder;
import org.openlmis.core.exception.DataException;

import java.util.Calendar;
import java.util.Date;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static junit.framework.Assert.*;

public class ProcessingPeriodTest {

  @Rule
  public ExpectedException exException = ExpectedException.none();

  @Test
  public void shouldNotThrowErrorOnValidateForAValidPeriod() {

    ProcessingPeriod processingPeriod = make(a(org.openlmis.core.builder.ProcessingPeriodBuilder.defaultProcessingPeriod));
    try {
      processingPeriod.validate();
    } catch (Exception e) {
      fail("Processing Period Validation failed where unexpected");
    }
  }

  @Test
  public void shouldNotThrowErrorOnValidateForAPeriodWithNoName() {
    ProcessingPeriod processingPeriod = make(a(org.openlmis.core.builder.ProcessingPeriodBuilder.defaultProcessingPeriod));
    processingPeriod.setName(null);

    exException.expect(DataException.class);
    exException.expectMessage("Period can not be saved without its Name.");
    processingPeriod.validate();
  }

  @Test
  public void shouldNotThrowErrorOnValidateForAPeriodWithNoScheduleId() {
    ProcessingPeriod processingPeriod = make(a(org.openlmis.core.builder.ProcessingPeriodBuilder.defaultProcessingPeriod));
    processingPeriod.setScheduleId(null);

    exException.expect(DataException.class);
    exException.expectMessage("Period can not be saved without its parent Schedule");
    processingPeriod.validate();
  }

  @Test
  public void shouldNotThrowErrorOnValidateForAPeriodWithNoStartDate() {
    ProcessingPeriod processingPeriod = make(a(org.openlmis.core.builder.ProcessingPeriodBuilder.defaultProcessingPeriod));
    processingPeriod.setStartDate(null);

    exException.expect(DataException.class);
    exException.expectMessage("Period can not be saved without its Start Date.");
    processingPeriod.validate();
  }

  @Test
  public void shouldNotThrowErrorOnValidateForAPeriodWithNoEndDate() {
    ProcessingPeriod processingPeriod = make(a(org.openlmis.core.builder.ProcessingPeriodBuilder.defaultProcessingPeriod));
    processingPeriod.setEndDate(null);

    exException.expect(DataException.class);
    exException.expectMessage("Period can not be saved without its End Date.");
    processingPeriod.validate();
  }

  @Test
  public void shouldNotThrowErrorOnValidateForAPeriodWithEndDateEarlierToStartDate() {
    ProcessingPeriod processingPeriod = make(a(ProcessingPeriodBuilder.defaultProcessingPeriod));
    processingPeriod.setStartDate(oneMonthPast(processingPeriod.getEndDate()));

    exException.expect(DataException.class);
    exException.expectMessage("Period End Date can not be earlier than Start Date.");
    processingPeriod.validate();
  }

  private Date oneMonthPast(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime((Date) date.clone());
    calendar.add(Calendar.MONTH, 1);
    return calendar.getTime();
  }
}
