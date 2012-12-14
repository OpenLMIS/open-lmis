package org.openlmis.rnr.repository.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.rnr.domain.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-requisition.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class ScheduleMapperIT {

    @Autowired
    ScheduleMapper scheduleMapper;

    @Test
    public void shouldGetIdByCode() throws Exception {
        Schedule schedule = new Schedule();
        schedule.setCode("SC1");
        schedule.setName("Schedule 1");

        Integer id = scheduleMapper.insert(schedule);
        assertThat(id, is(scheduleMapper.getIdForCode(schedule.getCode())));

    }
}
