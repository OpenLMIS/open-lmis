package org.openlmis.rnr.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.repository.mapper.ProgramMapper;
import org.openlmis.rnr.domain.RequisitionGroupProgramSchedule;
import org.openlmis.rnr.domain.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.rnr.builder.RequisitionGroupBuilder.defaultRequisitionGroup;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-requisition.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class RequisitionGroupProgramScheduleMapperTest {

    RequisitionGroupProgramSchedule requisitionGroupProgramSchedule;

    @Before
    public void setUp() throws Exception {
        requisitionGroupProgramSchedule = new RequisitionGroupProgramSchedule();
        requisitionGroupProgramSchedule.setModifiedBy("User");
        requisitionGroupProgramSchedule.setProgram(make(a(defaultProgram)));
        requisitionGroupProgramSchedule.setRequisitionGroup(make(a(defaultRequisitionGroup)));
        Schedule schedule = new Schedule();
        schedule.setCode("Q1stY");
        schedule.setName("QuarterYearly");
        requisitionGroupProgramSchedule.setSchedule(schedule);

    }

    @Autowired
    ProgramMapper programMapper;

    @Autowired
    RequisitionGroupMapper requisitionGroupMapper;

    @Autowired
    ScheduleMapper scheduleMapper;

    @Autowired
    RequisitionGroupProgramScheduleMapper requisitionGroupProgramScheduleMapper;

    @Test
    public void shouldInsertRGProgramSchedule() throws Exception {
        requisitionGroupProgramSchedule.getProgram().setId(programMapper.insert(requisitionGroupProgramSchedule.getProgram()));
        requisitionGroupProgramSchedule.getRequisitionGroup().setId(requisitionGroupMapper.insert(requisitionGroupProgramSchedule.getRequisitionGroup()));
        requisitionGroupProgramSchedule.getSchedule().setId(scheduleMapper.insert(requisitionGroupProgramSchedule.getSchedule()));

        Integer status = requisitionGroupProgramScheduleMapper.insert(requisitionGroupProgramSchedule);

        assertThat(status, is(1));
    }

    @Test
    public void shouldGetIdByCode() throws Exception {
        Schedule schedule = new Schedule();
        schedule.setCode("SC1");
        schedule.setName("Schedule 1");

        Long id = scheduleMapper.insert(schedule);
        assertThat(id, is(scheduleMapper.getIdForCode(schedule.getCode())));

    }
}
