package org.openlmis.distribution.repository.mapper;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.repository.mapper.*;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.distribution.domain.Distribution;
import org.openlmis.distribution.domain.Refrigerator;
import org.openlmis.distribution.domain.RefrigeratorReading;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.builder.DistributionBuilder.*;
import static org.openlmis.core.builder.DeliveryZoneBuilder.defaultDeliveryZone;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.defaultProcessingPeriod;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.scheduleId;
import static org.openlmis.core.builder.ProcessingScheduleBuilder.defaultProcessingSchedule;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;

@Category(IntegrationTests.class)
@Transactional
@ContextConfiguration(locations = "classpath*:test-applicationContext-distribution.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class RefrigeratorReadingMapperIT {

  @Autowired
  RefrigeratorMapper refrigeratorMapper;

  @Autowired
  RefrigeratorReadingMapper mapper;

  @Autowired
  DistributionMapper distributionMapper;

  @Autowired
  FacilityMapper facilityMapper;

  @Autowired
  private ProcessingScheduleMapper scheduleMapper;

  @Autowired
  private ProgramMapper programMapper;

  @Autowired
  private ProcessingPeriodMapper periodMapper;

  @Autowired
  private DeliveryZoneMapper deliveryZoneMapper;

  @Test
  public void shouldGetRefrigeratorReading() throws Exception {
    Facility facility = make(a(FacilityBuilder.defaultFacility));

    facilityMapper.insert(facility);

    Refrigerator refrigerator = getRefrigerator(facility);

    refrigeratorMapper.insert(refrigerator);

    Distribution distribution = getDistribution();

    distributionMapper.insert(distribution);

    RefrigeratorReading reading = getRefrigeratorReading(refrigerator, distribution);

    mapper.insert(reading);

    RefrigeratorReading dbReading = mapper.getByDistribution(refrigerator.getId(), distribution.getId());

    assertThat(dbReading, is(reading));
  }

  private Refrigerator getRefrigerator(Facility facility) {
    Refrigerator refrigerator = new Refrigerator("SAM2", "SER11", "SAM1", facility.getId(),null);
    refrigerator.setCreatedBy(1L);
    refrigerator.setModifiedBy(1L);
    return refrigerator;
  }

  private RefrigeratorReading getRefrigeratorReading(Refrigerator refrigerator, Distribution distribution) {
    RefrigeratorReading reading = new RefrigeratorReading();
    reading.setHighAlarmEvents(12);
    reading.setTemperature(12.2f);
    reading.setRefrigeratorId(refrigerator.getId());
    reading.setDistributionId(distribution.getId());
    reading.setCreatedBy(1L);
    reading.setModifiedBy(1L);
    return reading;
  }

  private Distribution getDistribution() {
    DeliveryZone zone;
    Program program1;
    ProcessingPeriod processingPeriod;

    zone = make(a(defaultDeliveryZone));

    deliveryZoneMapper.insert(zone);

    program1 = make(a(defaultProgram));

    ProcessingSchedule schedule = make(a(defaultProcessingSchedule));
    scheduleMapper.insert(schedule);
    processingPeriod = make(a(defaultProcessingPeriod, with(scheduleId, schedule.getId())));

    programMapper.insert(program1);
    periodMapper.insert(processingPeriod);

    return make(a(defaultDistribution,
      with(deliveryZone, zone),
      with(period, processingPeriod),
      with(program, program1)));
  }
}
