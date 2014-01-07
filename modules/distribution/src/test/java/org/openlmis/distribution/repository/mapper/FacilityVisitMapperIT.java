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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.DeliveryZoneBuilder.defaultDeliveryZone;
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
  }

  @Test
  public void shouldInsertFacilityVisit() {

    Facility facility = make(a(defaultFacility));
    facilityMapper.insert(facility);

    Distribution distribution = make(a(initiatedDistribution,
      with(deliveryZone, zone),
      with(period, processingPeriod),
      with(program, program1)));

    distributionMapper.insert(distribution);

    FacilityVisit facilityVisit = new FacilityVisit();
    Facilitator confirmedBy = new Facilitator("Barack", "President");
    Facilitator verifiedBy = new Facilitator("ManMohan", "Spectator");

    facilityVisit.setConfirmedBy(confirmedBy);
    facilityVisit.setVerifiedBy(verifiedBy);

    facilityVisit.setObservations("I observed something");

    facilityVisit.setDistributionId(distribution.getId());
    facilityVisit.setFacilityId(facility.getId());
    facilityVisit.setCreatedBy(1l);

    mapper.insert(facilityVisit);

    FacilityVisit actualFacilityVisit = mapper.getByDistributionAndFacility(facilityVisit.getDistributionId(), facilityVisit.getFacilityId());

    assertThat(actualFacilityVisit, is(facilityVisit));
    assertThat(actualFacilityVisit.getCreatedBy(), is(1l));
  }

  @Test
  public void shouldUpdateFacilityVisit() {

    Facility facility = make(a(defaultFacility));
    facilityMapper.insert(facility);

    Distribution distribution = make(a(initiatedDistribution,
      with(deliveryZone, zone),
      with(period, processingPeriod),
      with(program, program1)));

    distributionMapper.insert(distribution);

    FacilityVisit facilityVisit = new FacilityVisit();
    Facilitator confirmedBy = new Facilitator("Barack", "President");
    Facilitator verifiedBy = new Facilitator("ManMohan", "Spectator");


    facilityVisit.setDistributionId(distribution.getId());
    facilityVisit.setFacilityId(facility.getId());
    facilityVisit.setCreatedBy(1l);

    mapper.insert(facilityVisit);

    facilityVisit.setConfirmedBy(confirmedBy);
    facilityVisit.setVerifiedBy(verifiedBy);

    facilityVisit.setObservations("I observed something");

    mapper.update(facilityVisit);

    FacilityVisit actualFacilityVisit = mapper.getByDistributionAndFacility(distribution.getId(), facility.getId());

    assertThat(actualFacilityVisit, is(facilityVisit));
  }
}
