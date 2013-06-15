package org.openlmis.distribution.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.distribution.builder.DeliveryZoneProgramScheduleBuilder;
import org.openlmis.distribution.domain.DeliveryZone;
import org.openlmis.distribution.domain.DeliveryZoneMember;
import org.openlmis.distribution.domain.DeliveryZoneProgramSchedule;
import org.openlmis.core.builder.ProcessingScheduleBuilder;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingSchedule;
import org.openlmis.core.domain.Program;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.core.repository.mapper.ProcessingScheduleMapper;
import org.openlmis.core.repository.mapper.ProgramMapper;
import org.openlmis.db.categories.IntegrationTests;
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
import static org.openlmis.distribution.builder.DeliveryZoneBuilder.defaultDeliveryZone;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-distribution.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class DeliveryZoneMemberMapperIT {

  @Autowired
  DeliveryZoneMemberMapper mapper;

  @Autowired
  DeliveryZoneMapper zoneMapper;

  @Autowired
  FacilityMapper facilityMapper;

  @Autowired
  DeliveryZoneProgramScheduleMapper deliveryZoneProgramScheduleMapper;

  @Autowired
  ProcessingScheduleMapper processingScheduleMapper;

  @Autowired
  ProgramMapper programMapper;

  private DeliveryZoneMember member;
  private DeliveryZone deliveryZone;
  private Facility facility;


  @Before
  public void setUp() throws Exception {
    deliveryZone = make(a(defaultDeliveryZone));
    zoneMapper.insert(deliveryZone);
    facility = make(a(defaultFacility));
    facilityMapper.insert(facility);
    member = new DeliveryZoneMember(deliveryZone, facility);
  }

  @Test
  public void shouldInsertDeliveryZoneMember() throws Exception {
    mapper.insert(member);

    DeliveryZoneMember returned = mapper.getByDeliveryZoneCodeAndFacilityCode(deliveryZone.getCode(), facility.getCode());

    assertThat(returned.getDeliveryZone().getId(), is(member.getDeliveryZone().getId()));
    assertThat(returned.getFacility().getId(), is(member.getFacility().getId()));
  }

  @Test
  public void shouldUpdateDeliveryZoneMember() throws Exception {
    mapper.insert(member);

    Date modifiedDate = new Date();
    member.setModifiedDate(modifiedDate);
    mapper.update(member);

    DeliveryZoneMember updatedMember = mapper.getByDeliveryZoneCodeAndFacilityCode(deliveryZone.getCode(), facility.getCode());

    assertThat(updatedMember.getModifiedDate(), is(modifiedDate));
  }

  @Test
  public void shouldGetDeliveryZoneProgramIdsForFacility() throws Exception {
    ProcessingSchedule processingSchedule = make(a(ProcessingScheduleBuilder.defaultProcessingSchedule));
    processingScheduleMapper.insert(processingSchedule);

    Program program = make(a(ProgramBuilder.defaultProgram));
    programMapper.insert(program);

    DeliveryZoneProgramSchedule deliveryZoneProgramSchedule = make(a(DeliveryZoneProgramScheduleBuilder.defaultDZProgramSchedule));
    deliveryZoneProgramSchedule.setDeliveryZone(deliveryZone);
    deliveryZoneProgramSchedule.setSchedule(processingSchedule);
    deliveryZoneProgramSchedule.setProgram(program);

    deliveryZoneProgramScheduleMapper.insert(deliveryZoneProgramSchedule);

    mapper.insert(member);

    List<Long> deliveryZoneProgramIdsForFacility = mapper.getDeliveryZoneProgramIdsForFacility(member.getFacility().getId());

    assertThat(deliveryZoneProgramIdsForFacility.contains(deliveryZoneProgramSchedule.getProgram().getId()), is(true));
  }
}
