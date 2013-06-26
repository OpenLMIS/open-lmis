package org.openlmis.core.repository.mapper;

import com.natpryce.makeiteasy.MakeItEasy;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.*;
import org.openlmis.core.domain.DeliveryZone;
import org.openlmis.core.domain.DeliveryZoneMember;
import org.openlmis.core.domain.DeliveryZoneProgramSchedule;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingSchedule;
import org.openlmis.core.domain.Program;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-core.xml")
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
    deliveryZone = MakeItEasy.make(MakeItEasy.a(DeliveryZoneBuilder.defaultDeliveryZone));
    zoneMapper.insert(deliveryZone);
    facility = MakeItEasy.make(MakeItEasy.a(FacilityBuilder.defaultFacility));
    facilityMapper.insert(facility);
    member = new DeliveryZoneMember(deliveryZone, facility);
  }

  @Test
  public void shouldInsertDeliveryZoneMember() throws Exception {
    mapper.insert(member);

    DeliveryZoneMember returned = mapper.getByDeliveryZoneCodeAndFacilityCode(deliveryZone.getCode(), facility.getCode());

    Assert.assertThat(returned.getDeliveryZone().getId(), CoreMatchers.is(member.getDeliveryZone().getId()));
    Assert.assertThat(returned.getFacility().getId(), CoreMatchers.is(member.getFacility().getId()));
  }

  @Test
  public void shouldUpdateDeliveryZoneMember() throws Exception {
    mapper.insert(member);

    Date modifiedDate = new Date();
    member.setModifiedDate(modifiedDate);
    mapper.update(member);

    DeliveryZoneMember updatedMember = mapper.getByDeliveryZoneCodeAndFacilityCode(deliveryZone.getCode(), facility.getCode());

    Assert.assertThat(updatedMember.getModifiedDate(), CoreMatchers.is(modifiedDate));
  }

  @Test
  public void shouldGetDeliveryZoneProgramIdsForFacility() throws Exception {
    ProcessingSchedule processingSchedule = MakeItEasy.make(MakeItEasy.a(ProcessingScheduleBuilder.defaultProcessingSchedule));
    processingScheduleMapper.insert(processingSchedule);

    Program program = MakeItEasy.make(MakeItEasy.a(ProgramBuilder.defaultProgram));
    programMapper.insert(program);

    DeliveryZoneProgramSchedule deliveryZoneProgramSchedule = MakeItEasy.make(MakeItEasy.a(DeliveryZoneProgramScheduleBuilder.defaultDZProgramSchedule));
    deliveryZoneProgramSchedule.setDeliveryZone(deliveryZone);
    deliveryZoneProgramSchedule.setSchedule(processingSchedule);
    deliveryZoneProgramSchedule.setProgram(program);

    deliveryZoneProgramScheduleMapper.insert(deliveryZoneProgramSchedule);

    mapper.insert(member);

    List<Long> deliveryZoneProgramIdsForFacility = mapper.getDeliveryZoneProgramIdsForFacility(member.getFacility().getId());

    Assert.assertThat(deliveryZoneProgramIdsForFacility.contains(deliveryZoneProgramSchedule.getProgram().getId()), CoreMatchers.is(true));
  }
}
