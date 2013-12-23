/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.core.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.DeliveryZoneBuilder;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProcessingScheduleBuilder;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.FacilityBuilder.*;

@Category(IntegrationTests.class)
@Transactional
@ContextConfiguration(locations = "classpath*:test-applicationContext-core.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class RefrigeratorMapperIT {

  @Autowired
  FacilityMapper facilityMapper;

  @Autowired
  RefrigeratorMapper mapper;

  @Autowired
  RequisitionGroupMapper requisitionGroupMapper;

  @Autowired
  ProgramSupportedMapper programSupportedMapper;

  @Autowired
  ProgramMapper programMapper;

  @Autowired
  RoleAssignmentMapper roleAssignmentMapper;

  @Autowired
  RequisitionGroupMemberMapper requisitionGroupMemberMapper;

  @Autowired
  RoleRightsMapper roleRightsMapper;

  @Autowired
  SupervisoryNodeMapper supervisoryNodeMapper;

  @Autowired
  ProcessingScheduleMapper processingScheduleMapper;

  @Autowired
  DeliveryZoneMemberMapper deliveryZoneMemberMapper;

  @Autowired
  DeliveryZoneProgramScheduleMapper deliveryZoneProgramScheduleMapper;

  @Autowired
  DeliveryZoneMapper deliveryZoneMapper;

  ProcessingSchedule processingSchedule;
  Program program;
  DeliveryZone deliveryZone;
  Facility facility;

  @Before
  public void setUp() throws Exception {
    processingSchedule = make(a(ProcessingScheduleBuilder.defaultProcessingSchedule));
    processingScheduleMapper.insert(processingSchedule);

    program = make(a(ProgramBuilder.defaultProgram));
    programMapper.insert(program);

    deliveryZone = make(a(DeliveryZoneBuilder.defaultDeliveryZone));
    deliveryZoneMapper.insert(deliveryZone);

    deliveryZoneProgramScheduleMapper.insert(new DeliveryZoneProgramSchedule(deliveryZone.getId(),
      program.getId(), processingSchedule.getId()));

    facility = insertMemberFacility(deliveryZone, program, "F10A", "facility", 10l, true);
  }

  @Test
  public void shouldGetAllInDeliveryZoneAndOrderByGeographicZoneParentAndFacilityName() {
    Refrigerator refrigerator = new Refrigerator("SAM", "AUO", "SAM1", facility.getId());
    refrigerator.setCreatedBy(1L);
    refrigerator.setModifiedBy(1L);

    mapper.insert(refrigerator);

    Program unsupportedProgram = new Program();
    unsupportedProgram.setId(2l);

    List<Refrigerator> refrigerators = mapper.getRefrigeratorsForADeliveryZoneAndProgram(deliveryZone.getId(), program.getId());

    assertThat(refrigerators.get(0).getSerialNumber(), is(refrigerator.getSerialNumber()));
  }

  @Test
  public void shouldUpdateRefrigerator() throws Exception {
    Refrigerator refrigerator = new Refrigerator("SAM", "AUO", "SAM1", facility.getId());
    refrigerator.setCreatedBy(1L);
    refrigerator.setModifiedBy(1L);

    mapper.insert(refrigerator);

    refrigerator.setBrand("LG");
    mapper.update(refrigerator);

    List<Refrigerator> refrigerators = mapper.getRefrigeratorsForADeliveryZoneAndProgram(deliveryZone.getId(), program.getId());

    assertThat(refrigerators.get(0).getBrand(), is(refrigerator.getBrand()));
  }

  private Facility insertMemberFacility(DeliveryZone zone, Program program, String facilityCode, String facilityName,
                                        Long geoZoneId, Boolean facilityActive) {
    Facility facility = make(a(FacilityBuilder.defaultFacility, with(code, facilityCode), with(name, facilityName),
      with(geographicZoneId, geoZoneId), with(active, facilityActive)));
    facilityMapper.insert(facility);
    ProgramSupported programSupported = new ProgramSupported();
    programSupported.setFacilityId(facility.getId());
    programSupported.setProgram(program);
    programSupportedMapper.insert(programSupported);
    DeliveryZoneMember member1 = new DeliveryZoneMember(zone, facility);
    deliveryZoneMemberMapper.insert(member1);
    return facility;
  }
}
