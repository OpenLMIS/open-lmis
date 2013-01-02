package org.openlmis.core.repository.mapper;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.ProcessingSchedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class ProcessingScheduleMapperIT {

    @Autowired
    ProcessingScheduleMapper processingScheduleMapper;

    @Test
    public void shouldGetIdByCode() throws Exception {
        assertThat(processingScheduleMapper.getIdForCode("Q1stM"), is(1));
    }

    @Test
    public void shouldInsertASchedule() throws Exception {
        ProcessingSchedule schedule = new ProcessingSchedule("testCode", "testName");

        Integer insertionCount = processingScheduleMapper.insert(schedule);
        assertThat(insertionCount, is(1));
        assertThat(schedule.getId(), is(notNullValue()));
    }

    @Test
    public void shouldGetAllSchedules() throws Exception {
        assertThat(processingScheduleMapper.getAll().size(), is(2));
    }

    @Ignore
    @Test
    public void shouldGetScheduleById() throws Exception {
        ProcessingSchedule processingSchedule = processingScheduleMapper.get(new Integer(1));

        assertThat(processingSchedule.getId(), is(1));
        assertThat(processingSchedule.getCode(), is("Q1stM"));
        assertThat(processingSchedule.getName(), is("QuarterMonthly"));
        assertThat(processingSchedule.getDescription(), is("QuarterMonth"));
    }

    @Ignore
    @Test
    public void shouldUpdateAnExistingSchedule() throws Exception {
        ProcessingSchedule processingSchedule = new ProcessingSchedule(
                1, "Q1stM_updated", "QuarterMonthly_Updated", "QuarterMonthDesc_Updated", null, null);
        Integer updateCount = processingScheduleMapper.update(processingSchedule);
        assertThat(updateCount, is(1));
    }
}
