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
import org.openlmis.core.builder.*;
import org.openlmis.core.domain.*;
import org.openlmis.db.categories.IntegrationTests;
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
import static org.openlmis.core.builder.FacilityBuilder.*;
import static org.openlmis.core.builder.UserBuilder.defaultUser;
import static org.openlmis.core.builder.UserBuilder.restrictLogin;

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

  @Autowired
  private UserMapper userMapper;
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
    Refrigerator disabledRefrigerator = new Refrigerator("SAM1", "AUO1", "SAM1", facility.getId(), false);
    disabledRefrigerator.setCreatedBy(1L);
    disabledRefrigerator.setModifiedBy(1L);

    mapper.insert(disabledRefrigerator);

    Refrigerator enabledRefrigerator1 = new Refrigerator("SAM3", "AUO3", "SAM3", facility.getId(), true);
    enabledRefrigerator1.setCreatedBy(1L);
    enabledRefrigerator1.setModifiedBy(1L);
    mapper.insert(enabledRefrigerator1);

    Refrigerator enabledRefrigerator2 = new Refrigerator("SAM2", "AUO2", "SAM2", facility.getId(), true);
    enabledRefrigerator2.setCreatedBy(1L);
    enabledRefrigerator2.setModifiedBy(1L);
    mapper.insert(enabledRefrigerator2);

    Facility inactiveFacility = insertMemberFacility(deliveryZone, program, "F10B", "facility", 10l, false);
    Refrigerator refrigeratorForInactiveFacility = new Refrigerator("SAM4", "AUO4", "SAM4", inactiveFacility.getId(), true);
    refrigeratorForInactiveFacility.setCreatedBy(1L);
    refrigeratorForInactiveFacility.setModifiedBy(1L);
    mapper.insert(refrigeratorForInactiveFacility);

    Program unsupportedProgram = new Program();
    unsupportedProgram.setId(2l);

    List<Refrigerator> refrigerators = mapper.getRefrigeratorsForADeliveryZoneAndProgram(deliveryZone.getId(), program.getId());

    assertThat(refrigerators.size(), is(3));
    assertThat(refrigerators.get(0).getSerialNumber(), is(enabledRefrigerator2.getSerialNumber()));
    assertThat(refrigerators.get(1).getSerialNumber(), is(enabledRefrigerator1.getSerialNumber()));
    assertThat(refrigerators.get(0).getEnabled(), is(true));
  }

  @Test
  public void shouldUpdateRefrigerator() throws Exception {
    Long createdBy = 1L;
    User someUser = make(a(defaultUser, with(UserBuilder.facilityId, facility.getId()), with(UserBuilder.active, true), with(restrictLogin, true)));
    userMapper.insert(someUser);
    Refrigerator refrigerator = new Refrigerator("SAM", "AUO", "SAM1", facility.getId(), true);
    refrigerator.setCreatedBy(createdBy);
    refrigerator.setModifiedBy(createdBy);

    mapper.insert(refrigerator);

    refrigerator.setBrand("LG");
    refrigerator.setModifiedBy(someUser.getId());
    refrigerator.setEnabled(false);
    mapper.update(refrigerator);

    List<Refrigerator> refrigerators = mapper.getAllBy(facility.getId());

    assertThat(refrigerators.get(0).getBrand(), is(refrigerator.getBrand()));
    assertThat(refrigerators.get(0).getEnabled(), is(false));
    assertThat(refrigerators.get(0).getModifiedBy(), is(someUser.getId()));
  }

  @Test
  public void shouldGetRefrigeratorsByFacilityId() {
    Refrigerator refrigerator = new Refrigerator("SAM", "AUO", "SAM1", facility.getId(), true);
    refrigerator.setCreatedBy(1L);
    refrigerator.setModifiedBy(1L);

    mapper.insert(refrigerator);

    List<Refrigerator> refrigeratorsForFacility = mapper.getAllBy(facility.getId());

    assertThat(refrigeratorsForFacility, is(asList(refrigerator)));
  }

  @Test
  public void shouldDisableAllRefrigeratorsForAFacility() throws Exception {
    Refrigerator refrigerator = new Refrigerator("SAM", "AUO", "SAM1", facility.getId(), true);
    refrigerator.setCreatedBy(1L);
    refrigerator.setModifiedBy(1L);
    mapper.insert(refrigerator);

    mapper.disableAllFor(facility.getId());

    List<Refrigerator> refrigeratorsForFacility = mapper.getAllBy(facility.getId());
    assertThat(refrigeratorsForFacility.get(0).getEnabled(), is(false));
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
