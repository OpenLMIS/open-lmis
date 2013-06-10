package org.openlmis.allocation.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.allocation.domain.DeliveryZone;
import org.openlmis.allocation.domain.DeliveryZoneMember;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.allocation.builder.DeliveryZoneBuilder.defaultDeliveryZone;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-allocation.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class DeliveryZoneMemberMapperIT {

  @Autowired
  DeliveryZoneMemberMapper mapper;

  @Autowired
  DeliveryZoneMapper zoneMapper;

  @Autowired
  FacilityMapper facilityMapper;

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
}
