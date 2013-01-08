package org.openlmis.core.repository.mapper;

import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.ProcessingSchedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.*;
import static org.openlmis.core.builder.ProcessingScheduleBuilder.code;
import static org.openlmis.core.builder.ProcessingScheduleBuilder.defaultProcessingSchedule;

@ContextConfiguration(locations = "classpath*:applicationContext-core.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class ProcessingPeriodMapperIT {
  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Autowired
  ProcessingPeriodMapper periodMapper;

  @Autowired
  ProcessingScheduleMapper scheduleMapper;
  private ProcessingSchedule schedule;


  @Before
  public void setUp() throws Exception {
    schedule = make(a(defaultProcessingSchedule));
    scheduleMapper.insert(schedule);
  }

  @Test
  public void shouldReturnAllPeriodsForASchedule() throws Exception {

    DateTime date1 = new DateTime();
    DateTime date2 = date1.plusMonths(3);

    ProcessingPeriod period1 = make(a(defaultProcessingPeriod, with(startDate, date1.toDate()), with(scheduleId, schedule.getId())));
    ProcessingPeriod period2 = make(a(defaultProcessingPeriod, with(startDate, date2.toDate() ), with(scheduleId, schedule.getId()), with(name, "Month2")));

    periodMapper.insert(period1);
    periodMapper.insert(period2);

    List<ProcessingPeriod> fetchedPeriods = periodMapper.getAll(schedule.getId());
    assertThat(fetchedPeriods.size(), is(2));
    assertThat(fetchedPeriods.get(0).getId(), is(period2.getId()));
  }

  @Test
  public void shouldReturnEmptyListIfNoPeriodFoundForGivenSchedule() throws Exception {

    ProcessingPeriod period1 = make(a(defaultProcessingPeriod, with(scheduleId, schedule.getId())));
    periodMapper.insert(period1);

    List<ProcessingPeriod> fetchedPeriods = periodMapper.getAll(1233);
    assertTrue(fetchedPeriods.isEmpty());
  }

  @Test
  public void shouldInsertPeriodWithAllData() throws Exception {
    Date date1 = new Date();
    Date date2 = new Date(date1.getTime() + 90 * 24 * 60 * 60 * 1000);

    ProcessingPeriod period1 = make(a(defaultProcessingPeriod,
        with(scheduleId, schedule.getId()),
        with(startDate, date1), with(endDate, date2), with(numberOfMonths, 3)));

    Integer insertCount = periodMapper.insert(period1);

    assertThat(insertCount, is(1));
    ProcessingPeriod insertedPeriod = periodMapper.getAll(schedule.getId()).get(0);
    assertThat(insertedPeriod.getName(), is("Month1"));
    assertThat(insertedPeriod.getDescription(), is("first month"));
    assertThat(insertedPeriod.getStartDate(), is(date1));
    assertThat(insertedPeriod.getEndDate(), is(date2));
    assertThat(insertedPeriod.getNumberOfMonths(), is(3));
    assertThat(insertedPeriod.getModifiedBy(), is(1));
    assertThat(insertedPeriod.getModifiedDate(), is(notNullValue()));
  }

  @Test
  public void shouldNotInsertDuplicatePeriodName() throws Exception {
    ProcessingSchedule schedule2 = make(a(defaultProcessingSchedule, with(code, "XXX")));
    scheduleMapper.insert(schedule2);

    ProcessingPeriod period1 = make(a(defaultProcessingPeriod, with(scheduleId, schedule.getId()), with(name, "Month1")));
    ProcessingPeriod period2 = make(a(defaultProcessingPeriod, with(scheduleId, schedule2.getId()), with(name, "Month1")));
    ProcessingPeriod period3 = make(a(defaultProcessingPeriod, with(scheduleId, schedule2.getId()), with(name, "Month1")));
    expectedException.expect(DuplicateKeyException.class);
    expectedException.expectMessage("duplicate key value violates unique constraint");
    periodMapper.insert(period1);
    periodMapper.insert(period2);
    periodMapper.insert(period3);
  }

  @Test
  public void shouldReturnLastAddedPeriodForASchedule() throws Exception {

    DateTime date1 = new DateTime();
    DateTime date2 = date1.plusMonths(3);

    ProcessingPeriod period1 = make(a(defaultProcessingPeriod, with(startDate, date1.toDate()), with(scheduleId, schedule.getId())));
    ProcessingPeriod period2 = make(a(defaultProcessingPeriod, with(startDate, date2.toDate() ), with(scheduleId, schedule.getId()), with(name, "Month2")));

    periodMapper.insert(period1);
    periodMapper.insert(period2);

    ProcessingPeriod fetchedPeriod = periodMapper.getLastAddedProcessingPeriod(schedule.getId());
    assertThat(fetchedPeriod.getId(), is(period2.getId()));
  }


}
