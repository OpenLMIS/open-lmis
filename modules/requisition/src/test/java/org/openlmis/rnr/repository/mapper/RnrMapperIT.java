package org.openlmis.rnr.repository.mapper;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProcessingScheduleBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.ProcessingSchedule;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.core.repository.mapper.ProcessingPeriodMapper;
import org.openlmis.core.repository.mapper.ProcessingScheduleMapper;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.dto.RnrDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.*;
import static org.openlmis.rnr.domain.RnrStatus.AUTHORIZED;
import static org.openlmis.rnr.domain.RnrStatus.INITIATED;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-requisition.xml")
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class RnrMapperIT {

  public static final int MODIFIED_BY = 1;
  public static final Integer HIV = MODIFIED_BY;
  public static final int USER_2 = 2;

  private Facility facility;
  private ProcessingPeriod processingPeriod1;
  private ProcessingPeriod processingPeriod2;
  private ProcessingSchedule processingSchedule;
  private Rnr requisition;

  @Autowired
  private FacilityMapper facilityMapper;
  @Autowired
  private RequisitionMapper requisitionMapper;
  @Autowired
  private ProcessingPeriodMapper processingPeriodMapper;
  @Autowired
  private ProcessingScheduleMapper processingScheduleMapper;

  @Before
  public void setUp() {
    facility = make(a(FacilityBuilder.defaultFacility));
    facilityMapper.insert(facility);

    processingSchedule = make(a(ProcessingScheduleBuilder.defaultProcessingSchedule));
    processingScheduleMapper.insert(processingSchedule);

    processingPeriod1 = make(a(defaultProcessingPeriod, with(scheduleId, processingSchedule.getId()), with(name, "Period 1")));
    processingPeriodMapper.insert(processingPeriod1);

    processingPeriod2 = make(a(defaultProcessingPeriod, with(scheduleId, processingSchedule.getId()), with(name, "Period 2")));
    processingPeriodMapper.insert(processingPeriod2);

    requisition = new Rnr(facility.getId(), HIV, processingPeriod1.getId(), MODIFIED_BY);
    requisition.setStatus(INITIATED);
  }

  @Test
  public void shouldSetRequisitionId() {
    requisitionMapper.insert(requisition);
    assertThat(requisition.getId(), is(notNullValue()));
  }

  @Test
  public void shouldReturnRequisitionById() {
    requisitionMapper.insert(requisition);
    Rnr fetchedRequisition = requisitionMapper.getRequisitionById(requisition.getId());
    assertThat(fetchedRequisition.getId(), is(requisition.getId()));
    assertThat(fetchedRequisition.getProgramId(), is(equalTo(HIV)));
    assertThat(fetchedRequisition.getFacilityId(), is(equalTo(facility.getId())));
    assertThat(fetchedRequisition.getPeriodId(), is(equalTo(processingPeriod1.getId())));
    assertThat(fetchedRequisition.getModifiedBy(), is(equalTo(MODIFIED_BY)));
    assertThat(fetchedRequisition.getStatus(), is(equalTo(INITIATED)));
  }

  @Test
  public void shouldUpdateRequisition() {
    requisitionMapper.insert(requisition);
    requisition.setModifiedBy(USER_2);
    Date submittedDate = new Date();
    requisition.setSubmittedDate(submittedDate);
//    requisition.setFullSupplyItemsSubmittedCost(100.5F);
//    requisition.setTotalSubmittedCost(100.5F);

    requisitionMapper.update(requisition);

    Rnr updatedRequisition = requisitionMapper.getRequisitionById(requisition.getId());

    assertThat(updatedRequisition.getId(), is(requisition.getId()));
    assertThat(updatedRequisition.getModifiedBy(), is(equalTo(USER_2)));
    assertThat(updatedRequisition.getSubmittedDate(), is(submittedDate));
//    assertThat(updatedRequisition.getFullSupplyItemsSubmittedCost(), is(100.5F));
//    assertThat(updatedRequisition.getTotalSubmittedCost(), is(100.5F));
  }

  @Test
  public void shouldReturnRequisitionIfExists() {
    requisitionMapper.insert(requisition);

    Rnr anotherRequisition = new Rnr(facility.getId(), HIV, processingPeriod2.getId(), MODIFIED_BY);
    anotherRequisition.setStatus(INITIATED);
    requisitionMapper.insert(anotherRequisition);

    Rnr rnr = requisitionMapper.getRequisition(facility.getId(), HIV, processingPeriod1.getId());

    assertThat(rnr.getId(), is(requisition.getId()));
    assertThat(rnr.getFacilityId(), is(facility.getId()));
    assertThat(rnr.getProgramId(), is(HIV));
    assertThat(rnr.getPeriodId(), is(processingPeriod1.getId()));
  }

  @Test
  public void shouldGetRnrById() throws Exception {
    requisitionMapper.insert(requisition);

    Rnr returnedRequisition = requisitionMapper.getById(requisition.getId());

    assertThat(returnedRequisition.getFacilityId(), CoreMatchers.is(requisition.getFacilityId()));
    assertThat(returnedRequisition.getStatus(), CoreMatchers.is(requisition.getStatus()));
    assertThat(returnedRequisition.getId(), CoreMatchers.is(requisition.getId()));
  }

  @Test
  public void shouldNotGetInitiatedRequisitionsForFacilitiesAndPrograms() throws Exception {
    Integer programId = 1;
    requisition.setProgramId(programId);
    requisitionMapper.insert(requisition);

    List<RnrDTO> requisitions = requisitionMapper.getSubmittedRequisitionsForFacilitiesAndPrograms("{" + facility.getId() + "}", "{" + programId + "}");

    assertThat(requisitions.size(), is(0));
  }

  @Test
  public void shouldGetRequisitionsInSubmittedStateForFacilitiesAndPrograms() throws Exception {
    Integer programId = 1;
    requisition.setProgramId(programId);
    requisition.setStatus(AUTHORIZED);
    requisitionMapper.insert(requisition);

    List<RnrDTO> requisitions = requisitionMapper.getSubmittedRequisitionsForFacilitiesAndPrograms("{" + facility.getId() + "}", "{" + programId + "}");

    RnrDTO rnr = requisitions.get(0);
    assertThat(requisitions.size(), is(1));
    assertThat(rnr.getId(), is(requisition.getId()));
  }
}
