package org.openlmis.core.repository.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class ScheduleMapperIT {

    @Autowired
    ScheduleMapper scheduleMapper;

    @Test
    public void shouldGetIdByCode() throws Exception {
        assertThat(scheduleMapper.getIdForCode("Q1stM"), is(1));
    }

    @Test
    public void shouldInsertASchedule() throws Exception {
        Schedule schedule = new Schedule("testCode", "testName");

        Integer insertionCount = scheduleMapper.insert(schedule);
        assertThat(insertionCount, is(1));
        assertThat(schedule.getId(), is(notNullValue()));
    }

    @Test
    public void shouldGetAllSchedules() throws Exception {
        assertThat(scheduleMapper.getAll().size(), is(2));
    }
}
