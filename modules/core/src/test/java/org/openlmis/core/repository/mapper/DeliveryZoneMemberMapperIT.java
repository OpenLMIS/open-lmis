package org.openlmis.core.repository.mapper;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.*;
import org.openlmis.core.domain.DeliveryZone;
import org.openlmis.core.domain.DeliveryZoneMember;
import org.openlmis.core.domain.Facility;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.junit.Assert.assertThat;

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
    deliveryZone = make(a(DeliveryZoneBuilder.defaultDeliveryZone));
    zoneMapper.insert(deliveryZone);
    facility = make(a(FacilityBuilder.defaultFacility));
    facilityMapper.insert(facility);
    member = new DeliveryZoneMember(deliveryZone, facility);
  }


  @Test
  public void shouldInsertDeliveryZoneMember() throws Exception {
    mapper.insert(member);

    DeliveryZoneMember returned = mapper.getByDeliveryZoneCodeAndFacilityCode(deliveryZone.getCode(), facility.getCode());

    assertThat(returned.getDeliveryZone().getId(), CoreMatchers.is(member.getDeliveryZone().getId()));
    assertThat(returned.getFacility().getId(), CoreMatchers.is(member.getFacility().getId()));
  }

  @Test
  public void shouldUpdateDeliveryZoneMember() throws Exception {
    mapper.insert(member);

    Date modifiedDate = new Date();
    member.setModifiedDate(modifiedDate);
    mapper.update(member);

    DeliveryZoneMember updatedMember = mapper.getByDeliveryZoneCodeAndFacilityCode(deliveryZone.getCode(), facility.getCode());

    assertThat(updatedMember.getModifiedDate(), CoreMatchers.is(modifiedDate));
  }
}
