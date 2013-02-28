package org.openlmis.core.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingSchedule;
import org.openlmis.core.domain.RequisitionGroupProgramSchedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.ProcessingScheduleBuilder.defaultProcessingSchedule;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.core.builder.RequisitionGroupBuilder.defaultRequisitionGroup;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class RequisitionGroupProgramScheduleMapperIT {

  @Autowired
  ProgramMapper programMapper;
  @Autowired
  ProcessingScheduleMapper processingScheduleMapper;
  @Autowired
  FacilityMapper facilityMapper;

  @Autowired
  RequisitionGroupMapper requisitionGroupMapper;

  @Autowired
  RequisitionGroupProgramScheduleMapper requisitionGroupProgramScheduleMapper;

  RequisitionGroupProgramSchedule requisitionGroupProgramSchedule;

  @Before
  public void setUp() throws Exception {
    requisitionGroupProgramSchedule = new RequisitionGroupProgramSchedule();
    requisitionGroupProgramSchedule.setModifiedBy(1);
    requisitionGroupProgramSchedule.setModifiedDate(new Date(0));
    requisitionGroupProgramSchedule.setProgram(make(a(defaultProgram)));
    requisitionGroupProgramSchedule.setRequisitionGroup(make(a(defaultRequisitionGroup)));
    requisitionGroupProgramSchedule.setDirectDelivery(true);

    Facility facility = make(a(FacilityBuilder.defaultFacility));
    facilityMapper.insert(facility);
    requisitionGroupProgramSchedule.setDropOffFacility(facility);

    ProcessingSchedule schedule = make(a(defaultProcessingSchedule));
    processingScheduleMapper.insert(schedule);

    requisitionGroupProgramSchedule.setSchedule(schedule);
  }

  @Test
  public void shouldInsertRGProgramSchedule() throws Exception {
    programMapper.insert(requisitionGroupProgramSchedule.getProgram());
    requisitionGroupMapper.insert(requisitionGroupProgramSchedule.getRequisitionGroup());

    Integer recordCount = requisitionGroupProgramScheduleMapper.insert(requisitionGroupProgramSchedule);

    assertThat(recordCount, is(1));
  }

  @Test
  public void shouldGetProgramIdsForRGById() throws Exception {
    programMapper.insert(requisitionGroupProgramSchedule.getProgram());
    requisitionGroupMapper.insert(requisitionGroupProgramSchedule.getRequisitionGroup());

    requisitionGroupProgramScheduleMapper.insert(requisitionGroupProgramSchedule);

    List<Integer> resultProgramId = requisitionGroupProgramScheduleMapper.getProgramIDsById(requisitionGroupProgramSchedule.getRequisitionGroup().getId());

    assertThat(resultProgramId.size(), is(1));
    assertThat(resultProgramId.get(0), is(requisitionGroupProgramSchedule.getProgram().getId()));
  }

  @Test
  public void shouldGetAllSchedulesForRequisitionGroupAndProgram() throws Exception {
    programMapper.insert(requisitionGroupProgramSchedule.getProgram());
    requisitionGroupMapper.insert(requisitionGroupProgramSchedule.getRequisitionGroup());

    requisitionGroupProgramScheduleMapper.insert(requisitionGroupProgramSchedule);

    List<Integer> resultScheduleId = requisitionGroupProgramScheduleMapper.getScheduleIDsForRequisitionGroupAndProgram(requisitionGroupProgramSchedule.getRequisitionGroup().getId(), requisitionGroupProgramSchedule.getProgram().getId());

    assertThat(resultScheduleId.size(), is(1));
    assertThat(resultScheduleId.get(0), is(requisitionGroupProgramSchedule.getSchedule().getId()));
  }
}
