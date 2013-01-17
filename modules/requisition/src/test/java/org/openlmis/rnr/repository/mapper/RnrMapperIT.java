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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.*;
import static org.openlmis.rnr.domain.RnrStatus.INITIATED;
import static org.openlmis.rnr.domain.RnrStatus.SUBMITTED;

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
  private RnrMapper rnrMapper;
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
    rnrMapper.insert(requisition);
    assertThat(requisition.getId(), is(notNullValue()));
  }

  @Test
  public void shouldReturnRequisitionById() {
    rnrMapper.insert(requisition);
    Rnr fetchedRequisition = rnrMapper.getRequisitionById(requisition.getId());
    assertThat(fetchedRequisition.getId(), is(requisition.getId()));
    assertThat(fetchedRequisition.getProgramId(), is(equalTo(HIV)));
    assertThat(fetchedRequisition.getFacilityId(), is(equalTo(facility.getId())));
    assertThat(fetchedRequisition.getPeriodId(), is(equalTo(processingPeriod1.getId())));
    assertThat(fetchedRequisition.getModifiedBy(), is(equalTo(MODIFIED_BY)));
    assertThat(fetchedRequisition.getStatus(), is(equalTo(INITIATED)));
  }

  @Test
  public void shouldUpdateRequisition() {
    rnrMapper.insert(requisition);
    requisition.setModifiedBy(USER_2);
//    requisition.setFullSupplyItemsSubmittedCost(100.5F);
//    requisition.setTotalSubmittedCost(100.5F);

    rnrMapper.update(requisition);

    Rnr updatedRequisition = rnrMapper.getRequisitionById(requisition.getId());

    assertThat(updatedRequisition.getId(), is(requisition.getId()));
    assertThat(updatedRequisition.getModifiedBy(), is(equalTo(USER_2)));
//    assertThat(updatedRequisition.getFullSupplyItemsSubmittedCost(), is(100.5F));
//    assertThat(updatedRequisition.getTotalSubmittedCost(), is(100.5F));
  }

  @Test
  public void shouldReturnRequisitionIfExists() {
    rnrMapper.insert(requisition);

    Rnr anotherRequisition = new Rnr(facility.getId(), HIV, processingPeriod2.getId(), MODIFIED_BY);
    anotherRequisition.setStatus(INITIATED);
    rnrMapper.insert(anotherRequisition);

    Rnr rnr = rnrMapper.getRequisition(facility.getId(), HIV, processingPeriod1.getId());

    assertThat(rnr.getId(), is(requisition.getId()));
    assertThat(rnr.getFacilityId(), is(facility.getId()));
    assertThat(rnr.getProgramId(), is(HIV));
    assertThat(rnr.getPeriodId(), is(processingPeriod1.getId()));
  }

  @Test
  public void shouldGetRnrById() throws Exception {
    rnrMapper.insert(requisition);

    Rnr returnedRequisition = rnrMapper.getById(requisition.getId());

    assertThat(returnedRequisition.getFacilityId(), CoreMatchers.is(requisition.getFacilityId()));
    assertThat(returnedRequisition.getStatus(), CoreMatchers.is(requisition.getStatus()));
    assertThat(returnedRequisition.getId(), CoreMatchers.is(requisition.getId()));
  }

  @Test
  public void shouldNotGetInitiatedRequisitionsForFacilitiesAndPrograms() throws Exception {
    Integer programId = 1;
    requisition.setProgramId(programId);
    rnrMapper.insert(requisition);

    List<Rnr> requisitions = rnrMapper.getSubmittedRequisitionsForFacilitiesAndPrograms("{" + facility.getId() + "}", "{" + programId + "}");

    assertThat(requisitions.size(), is(0));
  }

  @Test
  public void shouldGetRequisitionsInSubmittedStateForFacilitiesAndPrograms() throws Exception {
    Integer programId = 1;
    requisition.setProgramId(programId);
    requisition.setStatus(SUBMITTED);
    rnrMapper.insert(requisition);

    List<Rnr> requisitions = rnrMapper.getSubmittedRequisitionsForFacilitiesAndPrograms("{" + facility.getId() + "}", "{" + programId + "}");

    assertThat(requisitions.size(), is(1));
    assertThat(requisitions.get(0).getId(), is(requisition.getId()));
  }
}
