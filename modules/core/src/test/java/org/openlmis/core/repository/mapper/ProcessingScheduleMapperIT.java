package org.openlmis.core.repository.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.ProcessingSchedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
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
}
