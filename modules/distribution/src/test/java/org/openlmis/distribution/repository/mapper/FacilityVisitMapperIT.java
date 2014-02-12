package org.openlmis.distribution.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.*;
import org.openlmis.core.repository.mapper.*;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.distribution.domain.Distribution;
import org.openlmis.distribution.domain.Facilitator;
import org.openlmis.distribution.domain.FacilityVisit;
import org.openlmis.distribution.domain.ReasonForNotVisiting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.DeliveryZoneBuilder.defaultDeliveryZone;
import static org.openlmis.core.builder.FacilityBuilder.code;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.defaultProcessingPeriod;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.scheduleId;
import static org.openlmis.core.builder.ProcessingScheduleBuilder.defaultProcessingSchedule;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.distribution.builder.DistributionBuilder.*;


@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-distribution.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
@Transactional
public class FacilityVisitMapperIT {

  @Autowired
  FacilityVisitMapper mapper;

  @Autowired
  FacilityMapper facilityMapper;

  @Autowired
  DistributionMapper distributionMapper;

  @Autowired
  private ProcessingScheduleMapper scheduleMapper;
  @Autowired
  DeliveryZoneMapper deliveryZoneMapper;

  @Autowired
  ProgramMapper programMapper;

  @Autowired
  ProcessingPeriodMapper periodMapper;

  DeliveryZone zone;
  Program program1;
  ProcessingPeriod processingPeriod;

  Facility facility;
  Distribution distribution;

  @Before
  public void setUp() throws Exception {
    zone = make(a(defaultDeliveryZone));
    program1 = make(a(defaultProgram));

    ProcessingSchedule schedule = make(a(defaultProcessingSchedule));
    scheduleMapper.insert(schedule);

    processingPeriod = make(a(defaultProcessingPeriod, with(scheduleId, schedule.getId())));

    deliveryZoneMapper.insert(zone);
    programMapper.insert(program1);
    periodMapper.insert(processingPeriod);

    facility = make(a(defaultFacility));
    facilityMapper.insert(facility);

    distribution = make(a(initiatedDistribution,
      with(deliveryZone, zone),
      with(period, processingPeriod),
      with(program, program1)));

    distributionMapper.insert(distribution);
  }

  @Test
  public void shouldInsertFacilityVisit() {
    FacilityVisit facilityVisit = new FacilityVisit(facility, distribution);
    mapper.insert(facilityVisit);

    FacilityVisit actualFacilityVisit = mapper.getBy(facilityVisit.getFacilityId(), facilityVisit.getDistributionId());

    facilityVisit.setSynced(false);
    assertThat(actualFacilityVisit, is(facilityVisit));
    assertThat(actualFacilityVisit.getCreatedBy(), is(1l));
  }

  @Test
  public void shouldUpdateFacilityVisit() {
    FacilityVisit facilityVisit = new FacilityVisit(facility, distribution);
    Facilitator confirmedBy = new Facilitator("Barack", "President");
    Facilitator verifiedBy = new Facilitator("ManMohan", "Spectator");
    facilityVisit.setReasonForNotVisiting(ReasonForNotVisiting.OTHER);
    facilityVisit.setOtherReasonDescription("Manmohan will not be PM again");
    mapper.insert(facilityVisit);

    facilityVisit.setConfirmedBy(confirmedBy);
    facilityVisit.setVerifiedBy(verifiedBy);
    facilityVisit.setObservations("I observed something");

    mapper.update(facilityVisit);

    FacilityVisit actualFacilityVisit = mapper.getBy(facility.getId(), distribution.getId());
    assertThat(actualFacilityVisit, is(facilityVisit));
  }

  @Test
  public void shouldGetFacilityVisitById() {
    FacilityVisit facilityVisit = new FacilityVisit(facility, distribution);

    mapper.insert(facilityVisit);

    FacilityVisit savedFacilityVisit = mapper.getById(facilityVisit.getId());

    facilityVisit.setSynced(false);
    assertThat(savedFacilityVisit, is(facilityVisit));
  }

  @Test
  public void shouldGetFacilityVisitsWhichAreNotSyncedYet() {
    FacilityVisit facilityVisit1 = new FacilityVisit(facility, distribution);
    mapper.insert(facilityVisit1);
    facilityVisit1.setSynced(true);
    mapper.update(facilityVisit1);

    Facility facility1 = make(a(defaultFacility, with(code, "F999")));
    facilityMapper.insert(facility1);
    FacilityVisit facilityVisit2 = new FacilityVisit(facility1, distribution);
    facilityVisit2.setSynced(false);
    mapper.insert(facilityVisit2);

    List<FacilityVisit> unSyncedFacilities = mapper.getUnSyncedFacilities(distribution.getId());

    assertThat(unSyncedFacilities, is(asList(facilityVisit2)));
  }

  @Test
  public void shouldGetCountOfUnsyncedFacilities() {
    FacilityVisit facilityVisit1 = new FacilityVisit(facility, distribution);
    mapper.insert(facilityVisit1);
    facilityVisit1.setSynced(true);
    mapper.update(facilityVisit1);

    Facility facility1 = make(a(defaultFacility, with(code, "F999")));
    facilityMapper.insert(facility1);
    FacilityVisit facilityVisit2 = new FacilityVisit(facility1, distribution);
    facilityVisit2.setSynced(false);
    mapper.insert(facilityVisit2);

    Integer unsyncedFacilityCountForDistribution = mapper.getUnsyncedFacilityCountForDistribution(distribution.getId());

    assertThat(unsyncedFacilityCountForDistribution, is(1));
  }
}
