/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

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
