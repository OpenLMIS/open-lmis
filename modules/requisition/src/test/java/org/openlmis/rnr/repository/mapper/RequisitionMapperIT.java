package org.openlmis.rnr.repository.mapper;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProcessingPeriodBuilder;
import org.openlmis.core.builder.ProcessingScheduleBuilder;
import org.openlmis.core.builder.SupervisoryNodeBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.core.repository.mapper.ProcessingPeriodMapper;
import org.openlmis.core.repository.mapper.ProcessingScheduleMapper;
import org.openlmis.core.repository.mapper.SupervisoryNodeMapper;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.*;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.defaultProcessingPeriod;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.scheduleId;
import static org.openlmis.rnr.domain.RnrStatus.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-requisition.xml")
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class RequisitionMapperIT {
  public static final int MODIFIED_BY = 1;
  public static final Integer PROGRAM_ID = 1;
  public static final int USER_ID = 2;

  private Facility facility;
  private ProcessingSchedule processingSchedule;
  private ProcessingPeriod processingPeriod1;
  private ProcessingPeriod processingPeriod2;

  @Autowired
  private FacilityMapper facilityMapper;
  @Autowired
  private RequisitionMapper mapper;
  @Autowired
  private ProcessingPeriodMapper processingPeriodMapper;
  @Autowired
  private ProcessingScheduleMapper processingScheduleMapper;
  private ProcessingPeriod processingPeriod3;
  @Autowired
  SupervisoryNodeMapper supervisoryNodeMapper;
  private SupervisoryNode supervisoryNode;

  @Before
  public void setUp() {
    facility = make(a(FacilityBuilder.defaultFacility));
    facilityMapper.insert(facility);

    processingSchedule = make(a(ProcessingScheduleBuilder.defaultProcessingSchedule));
    processingScheduleMapper.insert(processingSchedule);

    processingPeriod1 = insertPeriod("Period 1");
    processingPeriod2 = insertPeriod("Period 2");
    processingPeriod3 = insertPeriod("Period 3");
    supervisoryNode = insertSupervisoryNode();
  }

  @Test
  public void shouldSetRequisitionId() {
    Rnr requisition = insertRequisition(processingPeriod1, INITIATED);
    assertThat(requisition.getId(), is(notNullValue()));
  }

  @Test
  public void shouldReturnRequisitionById() {
    Rnr requisition = insertRequisition(processingPeriod1, INITIATED);
    Rnr fetchedRequisition = mapper.getById(requisition.getId());
    assertThat(fetchedRequisition.getId(), is(requisition.getId()));
    assertThat(fetchedRequisition.getProgramId(), is(equalTo(PROGRAM_ID)));
    assertThat(fetchedRequisition.getFacilityId(), is(equalTo(facility.getId())));
    assertThat(fetchedRequisition.getPeriodId(), is(equalTo(processingPeriod1.getId())));
    assertThat(fetchedRequisition.getModifiedBy(), is(equalTo(MODIFIED_BY)));
    assertThat(fetchedRequisition.getStatus(), is(equalTo(INITIATED)));
  }

  @Test
  public void shouldUpdateRequisition() {
    Rnr requisition = insertRequisition(processingPeriod1, INITIATED);
    requisition.setModifiedBy(USER_ID);
    Date submittedDate = new Date();
    requisition.setSubmittedDate(submittedDate);
    requisition.setSupervisoryNodeId(supervisoryNode.getId());

    mapper.update(requisition);

    Rnr updatedRequisition = mapper.getById(requisition.getId());

    assertThat(updatedRequisition.getId(), is(requisition.getId()));
    assertThat(updatedRequisition.getSupervisoryNodeId(), is(requisition.getSupervisoryNodeId()));
    assertThat(updatedRequisition.getModifiedBy(), is(equalTo(USER_ID)));
    assertThat(updatedRequisition.getSubmittedDate(), is(submittedDate));
  }

  @Test
  public void shouldReturnRequisitionIfExists() {
    Rnr requisition = insertRequisition(processingPeriod1, INITIATED);
    insertRequisition(processingPeriod2, INITIATED);

    Rnr rnr = mapper.getRequisition(facility.getId(), PROGRAM_ID, processingPeriod1.getId());

    assertThat(rnr.getId(), is(requisition.getId()));
    assertThat(rnr.getFacilityId(), is(facility.getId()));
    assertThat(rnr.getProgramId(), is(PROGRAM_ID));
    assertThat(rnr.getPeriodId(), is(processingPeriod1.getId()));
  }

  @Test
  public void shouldGetRnrById() throws Exception {
    Rnr requisition = insertRequisition(processingPeriod1, INITIATED);

    Rnr returnedRequisition = mapper.getById(requisition.getId());

    assertThat(returnedRequisition.getFacilityId(),is(requisition.getFacilityId()));
    assertThat(returnedRequisition.getStatus(),is(requisition.getStatus()));
    assertThat(returnedRequisition.getId(),is(requisition.getId()));
  }

  @Test
  public void shouldNotGetInitiatedRequisitionsForFacilitiesAndPrograms() throws Exception {
    Rnr requisition = insertRequisition(processingPeriod1, INITIATED);
    requisition.setSupervisoryNodeId(supervisoryNode.getId());
    mapper.update(requisition);

    List<Rnr> requisitions = mapper.getAuthorizedRequisitions(null);

    assertThat(requisitions.size(), is(0));
  }

  @Test
  public void shouldGetRequisitionsInSubmittedStateForRoleAssignment() throws Exception {
    Rnr requisition = insertRequisition(processingPeriod1, AUTHORIZED);
    requisition.setSupervisoryNodeId(supervisoryNode.getId());
    mapper.update(requisition);
    RoleAssignment roleAssignment = new RoleAssignment(USER_ID, 1, PROGRAM_ID, supervisoryNode);

    List<Rnr> requisitions = mapper.getAuthorizedRequisitions(roleAssignment);

    Rnr rnr = requisitions.get(0);
    assertThat(requisitions.size(), is(1));
    assertThat(rnr.getFacility().getId(), is(facility.getId()));
    assertThat(rnr.getProgram().getId(), is(PROGRAM_ID));
    assertThat(rnr.getPeriod().getId(), is(processingPeriod1.getId()));
    assertThat(rnr.getId(), is(requisition.getId()));
    assertThat(rnr.getModifiedDate(), is(notNullValue()));
    assertThat(rnr.getSubmittedDate(), is(requisition.getSubmittedDate()));
  }

  @Test
  public void shouldGetTheLastRequisitionToEnterThePostSubmitFlow() throws Exception {
    DateTime date1 = now();
    DateTime date2 = date1.plusMonths(1);

    Rnr rnr1 = insertRequisition(processingPeriod1, AUTHORIZED);
    rnr1.setSubmittedDate(date1.toDate());
    mapper.update(rnr1);

    Rnr rnr2 = insertRequisition(processingPeriod2, APPROVED);
    rnr2.setSubmittedDate(date2.toDate());
    mapper.update(rnr2);

    insertRequisition(processingPeriod3, INITIATED);

    Rnr lastRequisitionToEnterThePostSubmitFlow = mapper.getLastRequisitionToEnterThePostSubmitFlow(facility.getId(), PROGRAM_ID);

    assertThat(lastRequisitionToEnterThePostSubmitFlow.getId(), is(rnr2.getId()));
  }

  private Rnr insertRequisition(ProcessingPeriod period, RnrStatus status) {
    Rnr rnr = new Rnr(facility.getId(), PROGRAM_ID, period.getId(), MODIFIED_BY);
    rnr.setStatus(status);
    rnr.setModifiedDate(new Date());
    rnr.setSubmittedDate(new Date(111111L));
    mapper.insert(rnr);
    return rnr;
  }

  private ProcessingPeriod insertPeriod(String name) {
    ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod,
        with(scheduleId, processingSchedule.getId()),
        with(ProcessingPeriodBuilder.name, name)));

    processingPeriodMapper.insert(processingPeriod);

    return processingPeriod;
  }

  private SupervisoryNode insertSupervisoryNode() {
    supervisoryNode = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode));
    supervisoryNode.setFacility(facility);

    supervisoryNodeMapper.insert(supervisoryNode);
    return supervisoryNode;
  }
}
