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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.openlmis.core.builder.FacilityBuilder.*;
import static org.openlmis.core.builder.ProcessingScheduleBuilder.defaultProcessingSchedule;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.core.builder.ProgramBuilder.programCode;
import static org.openlmis.core.builder.ProgramSupportedBuilder.*;
import static org.openlmis.core.builder.RequisitionGroupBuilder.defaultRequisitionGroup;
import static org.openlmis.core.builder.UserBuilder.defaultUser;
import static org.openlmis.core.builder.UserBuilder.facilityId;
import static org.openlmis.core.domain.Right.CONFIGURE_RNR;
import static org.openlmis.core.domain.Right.CREATE_REQUISITION;

@Category(IntegrationTests.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class FacilityMapperIT {

  public static final String OPERATED_BY_MOH = "MoH";
  @Autowired
  private UserMapper userMapper;

  @Autowired
  FacilityMapper mapper;

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
  RequisitionGroupProgramScheduleMapper requisitionGroupProgramScheduleMapper;

  @Autowired
  SupplyLineMapper supplyLineMapper;

  @Autowired
  GeographicZoneMapper geographicZoneMapper;

  @Test
  public void shouldFetchAllFacilitiesAvailable() throws Exception {
    Facility trz001 = make(a(defaultFacility,
      with(code, "TRZ001"),
      with(name, "Ngorongoro Hospital"),
      with(type, "warehouse"),
      with(geographicZoneId, 1L)));
    Facility trz002 = make(a(defaultFacility,
      with(code, "TRZ002"),
      with(name, "Rural Clinic"),
      with(type, "lvl3_hospital"),
      with(geographicZoneId, 2L)));
    mapper.insert(trz001);
    mapper.insert(trz002);

    List<Facility> facilities = mapper.getAll();

    assertEquals(facilities.get(0).getCode(), trz001.getCode());
    assertEquals(facilities.get(1).getCode(), trz002.getCode());
  }

  @Test
  public void shouldGetAllFacilityTypes() throws Exception {
    List<FacilityType> facilityTypes = mapper.getAllTypes();

    assertThat(facilityTypes.size(), is(11));
    FacilityType facilityType = facilityTypes.get(0);
    assertThat(facilityType.getCode(), is("lvl3_hospital"));
    assertThat(facilityType.getName(), is("Lvl3 Hospital"));
    assertThat(facilityType.getDescription(), is("State Hospital"));
    assertThat(facilityType.getLevelId(), is(nullValue()));
    assertThat(facilityType.getNominalMaxMonth(), is(3));
    assertThat(facilityType.getNominalEop(), is(0.5));
    assertThat(facilityType.getDisplayOrder(), is(1));
    assertThat(facilityType.isActive(), is(true));
  }

  @Test
  public void shouldGetAllParentFacilitiesByModifiedDate() {
    Facility facility1 = make(a(defaultFacility));
    Facility facility2 = make(a(defaultFacility, with(code, "FFF111")));
    mapper.insert(facility1);
    mapper.insert(facility2);

    Date modifiedDate = new Date();
    facility1.setName("New Facility");
    facility1.setModifiedDate(modifiedDate);
    facility1.setParentFacilityId(facility2.getId());
    mapper.update(facility1);

    facility2.setModifiedDate(modifiedDate);
    mapper.update(facility2);

    List<Facility> facilities = mapper.getAllParentsByModifiedDate(modifiedDate);

    assertThat(facilities.size(), is(1));
    assertThat(facilities.get(0).getId(), is(facility2.getId()));
  }

  @Test
  public void shouldGetAllOperators() throws Exception {
    List<FacilityOperator> allOperators = mapper.getAllOperators();
    assertThat(allOperators.size(), is(4));
    FacilityOperator facilityOperator = allOperators.get(0);
    assertThat(facilityOperator.getCode(), is("MoH"));
    assertThat(facilityOperator.getText(), is("MoH"));
    assertThat(facilityOperator.getDisplayOrder(), is(1));
  }

  @Test
  public void shouldReturnFacilityForAUser() throws Exception {
    mapper.insert(make(a(defaultFacility)));
    Facility facility = mapper.getAll().get(0);

    User user = make(a(defaultUser, with(facilityId, facility.getId())));
    userMapper.insert(user);

    Facility userFacility = mapper.getHomeFacility(user.getId());

    assertEquals(facility.getCode(), userFacility.getCode());
    assertEquals(facility.getName(), userFacility.getName());
    assertEquals(facility.getId(), userFacility.getId());
  }

  @Test
  public void shouldReturnNullIfHomeFacilityForAUserIsVirtual() throws Exception {
    mapper.insert(make(a(defaultFacility, with(virtualFacility, true))));
    Facility facility = mapper.getAll().get(0);

    User user = make(a(defaultUser, with(facilityId, facility.getId())));
    userMapper.insert(user);

    Facility userFacility = mapper.getHomeFacility(user.getId());
    assertThat(userFacility, is(nullValue()));
  }


  @Test
  public void shouldGetFacilityById() throws Exception {
    Facility facility = make(a(defaultFacility));
    facility.setLatitude(123.45678);
    facility.setLongitude(-321.87654);
    mapper.insert(facility);
    Facility resultFacility = mapper.getById(facility.getId());
    assertThat(resultFacility.getCode(), is("F10010"));
    assertThat(resultFacility.getId(), is(facility.getId()));
    assertThat(resultFacility.getName(), is("Apollo Hospital"));
    assertThat(resultFacility.getGeographicZone().getName(), is("Arusha"));
    assertThat(resultFacility.getGeographicZone().getParent().getName(), is("Root"));
    assertThat(resultFacility.getLatitude(), is(123.45678));
    assertThat(resultFacility.getLongitude(), is(-321.87654));
  }

  @Test
  public void shouldInsertFacilityWithSuppliedModifiedDateIfNotNull() throws Exception {
    Facility facility = make(a(defaultFacility));
    facility.setLatitude(123.45678);
    facility.setLongitude(-321.87654);

    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.MONTH, Calendar.JANUARY);
    facility.setModifiedDate(calendar.getTime());
    mapper.insert(facility);
    Facility resultFacility = mapper.getById(facility.getId());
    assertThat(resultFacility.getCode(), is("F10010"));
    assertThat(resultFacility.getModifiedDate(), is(calendar.getTime()));
  }

  @Test
  public void shouldInsertFacilityWithDbDefalutDateIfSuppliedDateIsNull() throws Exception {
    Facility facility = make(a(defaultFacility));
    facility.setLatitude(123.45678);
    facility.setLongitude(-321.87654);

    facility.setModifiedDate(null);
    mapper.insert(facility);
    Facility resultFacility = mapper.getById(facility.getId());
    assertThat(resultFacility.getCode(), is("F10010"));
    assertThat(resultFacility.getModifiedDate(), is(notNullValue()));
  }

  @Test
  public void shouldUpdateFacilityWithDefaultDbTimeWhenModifiedDateIsNull() throws Exception {
    Facility facility = make(a(defaultFacility));
    mapper.insert(facility);
    facility.setCode("NewTestCode");
    facility.setModifiedDate(null);

    mapper.update(facility);

    Facility updatedFacility = mapper.getById(facility.getId());
    assertThat(updatedFacility.getCode(), is(facility.getCode()));
    assertThat(updatedFacility.getModifiedDate(), is(notNullValue()));
  }

  @Test
  public void shouldUpdateFacilityWithSuppliedModifiedTime() throws Exception {
    Facility facility = make(a(defaultFacility));
    mapper.insert(facility);
    facility.setCode("NewTestCode");
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.MONTH, Calendar.JANUARY);
    facility.setModifiedDate(calendar.getTime());

    mapper.update(facility);

    Facility updatedFacility = mapper.getById(facility.getId());
    assertThat(updatedFacility.getCode(), is(facility.getCode()));
    assertThat(updatedFacility.getModifiedDate(), is(calendar.getTime()));
  }

  @Test
  public void shouldReturnFacilityOperatorIdForCode() {
    Long id = mapper.getOperatedByIdForCode(OPERATED_BY_MOH);
    assertThat(id, is(1L));

    id = mapper.getOperatedByIdForCode("InValid");
    assertThat(id, is(nullValue()));
  }

  @Test
  public void shouldReturnFacilityTypeForCode() {
    FacilityType facilityType = mapper.getFacilityTypeForCode(FACILITY_TYPE_CODE);
    assertThat(facilityType.getId(), is(1L));

    facilityType = mapper.getFacilityTypeForCode("InValid");
    assertThat(facilityType, is(nullValue()));
  }

  @Test
  public void shouldReturnFacilityTypeById() {
    FacilityType facilityTypeWithId = mapper.getFacilityTypeForCode(FACILITY_TYPE_CODE);

    FacilityType facilityType = mapper.getFacilityTypeById(facilityTypeWithId.getId());
    assertThat(facilityType, is(notNullValue()));
    assertThat(facilityType.getId(), is(facilityTypeWithId.getId()));
    assertThat(facilityType.getCode(), is(FACILITY_TYPE_CODE));
  }

  @Test
  public void shouldReturnFacilityOperatorById() throws Exception {
    Long id = mapper.getOperatedByIdForCode(OPERATED_BY_MOH);

    FacilityOperator operator = mapper.getFacilityOperatorById(id);
    assertThat(operator.getId(), is(id));
    assertThat(operator.getCode(), is(OPERATED_BY_MOH));
  }

  @Test
  public void shouldUpdateEnabledAndActiveForAFacility() throws Exception {
    Facility facility = make(a(defaultFacility));
    mapper.insert(facility);
    facility.setEnabled(false);
    facility.setActive(false);
    facility.setModifiedBy(1L);
    mapper.updateEnabledAndActiveFor(facility);

    Facility updatedFacility = mapper.getById(facility.getId());

    assertThat(updatedFacility.getEnabled(), is(false));
    assertThat(updatedFacility.getActive(), is(false));
    assertThat(updatedFacility.getModifiedBy(), is(1L));
  }

  @Test
  public void shouldGetIdByCode() throws Exception {
    Facility facility = make(a(defaultFacility));
    mapper.insert(facility);
    assertThat(mapper.getIdForCode(facility.getCode()), is(facility.getId()));
  }

  @Test
  public void shouldGetAllNonVirtualFacilitiesForRequisitionGroupsWhichSupportGivenProgram() {
    RequisitionGroup rg1 = make(a(defaultRequisitionGroup, with(RequisitionGroupBuilder.code, "RG1")));
    RequisitionGroup rg2 = make(a(defaultRequisitionGroup, with(RequisitionGroupBuilder.code, "RG2")));
    requisitionGroupMapper.insert(rg1);
    requisitionGroupMapper.insert(rg2);

    Facility facilitySupportingProgramInRG1 = make(a(FacilityBuilder.defaultFacility, with(FacilityBuilder.code, "F1")));
    Facility virtualFacilitySupportingProgramInRG1 = make(a(FacilityBuilder.defaultFacility, with(FacilityBuilder.code, "V1"), with(virtualFacility, true)));
    Facility facilityNotSupportingProgramInRG2 = make(a(FacilityBuilder.defaultFacility, with(FacilityBuilder.code, "F2")));
    Facility facilitySupportingProgramNotInAnyRG = make(a(FacilityBuilder.defaultFacility, with(FacilityBuilder.code, "F3")));
    mapper.insert(facilitySupportingProgramInRG1);
    mapper.insert(virtualFacilitySupportingProgramInRG1);
    mapper.insert(facilityNotSupportingProgramInRG2);
    mapper.insert(facilitySupportingProgramNotInAnyRG);


    requisitionGroupMemberMapper.insert(new RequisitionGroupMember(rg1, facilitySupportingProgramInRG1));
    requisitionGroupMemberMapper.insert(new RequisitionGroupMember(rg1, virtualFacilitySupportingProgramInRG1));
    requisitionGroupMemberMapper.insert(new RequisitionGroupMember(rg2, facilitySupportingProgramInRG1));
    requisitionGroupMemberMapper.insert(new RequisitionGroupMember(rg2, facilityNotSupportingProgramInRG2));

    programMapper.insert(make(a(defaultProgram, with(programCode, "Random"))));


    programSupportedMapper.insert(make(a(defaultProgramSupported,
      with(supportedFacilityId, facilitySupportingProgramInRG1.getId()),
      with(supportedProgram, make(a(defaultProgram, with(programCode, "Random")))))));

    programSupportedMapper.insert(make(a(defaultProgramSupported,
      with(supportedFacilityId, virtualFacilitySupportingProgramInRG1.getId()),
      with(supportedProgram, make(a(defaultProgram, with(programCode, "Random")))))));

    programSupportedMapper.insert(make(a(defaultProgramSupported,
      with(supportedFacilityId, facilitySupportingProgramNotInAnyRG.getId()),
      with(supportedProgram, make(a(defaultProgram, with(programCode, "Random")))))));

    RequisitionGroupProgramSchedule requisitionGroupProgramSchedule = new RequisitionGroupProgramSchedule();

    requisitionGroupProgramSchedule.setModifiedBy(1L);
    requisitionGroupProgramSchedule.setModifiedDate(new Date(0));

    requisitionGroupProgramSchedule.setProgram(make(a(defaultProgram, with(programCode, "Random"))));

    requisitionGroupProgramSchedule.setRequisitionGroup(rg1);
    requisitionGroupProgramSchedule.setDirectDelivery(true);
    requisitionGroupProgramSchedule.setDropOffFacility(facilitySupportingProgramInRG1);

    ProcessingSchedule schedule = make(a(defaultProcessingSchedule));
    processingScheduleMapper.insert(schedule);

    requisitionGroupProgramSchedule.setProcessingSchedule(schedule);

    requisitionGroupProgramScheduleMapper.insert(requisitionGroupProgramSchedule);

    List<Facility> facilities = mapper.getFacilitiesBy(make(a(defaultProgram, with(programCode, "Random"))).getId(), "{" + rg1.getId() + "," + rg2.getId() + " }");

    assertThat(facilities.size(), is(1));
    assertThat(facilities.get(0).getCode(), is("F1"));
  }

  @Test
  public void shouldSearchAllFacilitiesByCodeOrName() throws Exception {
    Facility facility1 = make(a(FacilityBuilder.defaultFacility, with(code, "FF110"), with(name, "D1100")));
    Facility facility2 = make(a(FacilityBuilder.defaultFacility, with(code, "D00"), with(name, "F110")));
    Facility facility3 = make(a(FacilityBuilder.defaultFacility, with(code, "FF1100"), with(name, "F1100")));
    Facility facility4 = make(a(FacilityBuilder.defaultFacility, with(code, "FF130")));
    Facility facility5 = make(a(FacilityBuilder.defaultFacility, with(code, "FF1101"), with(virtualFacility, true)));

    mapper.insert(facility1);
    mapper.insert(facility2);
    mapper.insert(facility3);
    mapper.insert(facility4);
    mapper.insert(facility5);

    List<Facility> returnedFacilityList = mapper.searchFacilitiesByCodeOrName("f11");

    assertThat(returnedFacilityList.size(), is(4));

    for (Facility facility : returnedFacilityList) {
      assertThat(facility.getCode().equals(facility1.getCode())
        || facility.getCode().equals(facility2.getCode())
        || facility.getCode().equals(facility3.getCode())
        || facility.getCode().equals(facility5.getCode()), is(true));
    }
  }

  @Test
  public void shouldSearchVirtualFacilitiesByCodeOrName() throws Exception {
    Facility facility1 = make(a(FacilityBuilder.defaultFacility, with(code, "FF110"), with(name, "D1100")));
    Facility facility2 = make(a(FacilityBuilder.defaultFacility, with(code, "D00"), with(name, "F110")));
    Facility facility3 = make(a(FacilityBuilder.defaultFacility, with(code, "FF1100"), with(name, "F1100")));
    Facility facility4 = make(a(FacilityBuilder.defaultFacility, with(code, "FF130")));
    Facility facility5 = make(a(FacilityBuilder.defaultFacility, with(code, "FF1101"), with(virtualFacility, true)));

    mapper.insert(facility1);
    mapper.insert(facility2);
    mapper.insert(facility3);
    mapper.insert(facility4);
    mapper.insert(facility5);

    List<Facility> returnedFacilityList = mapper.searchFacilitiesByCodeOrNameAndVirtualFacilityFlag("f11", true);

    assertThat(returnedFacilityList.size(), is(1));
    assertThat(returnedFacilityList.get(0).getCode(), is("FF1101"));

  }

  @Test
  public void shouldSearchNonVirtualFacilitiesByCodeOrName() throws Exception {
    Facility facility1 = make(a(FacilityBuilder.defaultFacility, with(code, "FF110"), with(name, "D1100")));
    Facility facility2 = make(a(FacilityBuilder.defaultFacility, with(code, "D00"), with(name, "F110")));
    Facility facility3 = make(a(FacilityBuilder.defaultFacility, with(code, "FF1100"), with(name, "F1100")));
    Facility facility4 = make(a(FacilityBuilder.defaultFacility, with(code, "FF130")));
    Facility facility5 = make(a(FacilityBuilder.defaultFacility, with(code, "FF1101"), with(virtualFacility, true)));

    mapper.insert(facility1);
    mapper.insert(facility2);
    mapper.insert(facility3);
    mapper.insert(facility4);
    mapper.insert(facility5);

    List<Facility> returnedFacilityList = mapper.searchFacilitiesByCodeOrNameAndVirtualFacilityFlag("f11", false);

    assertThat(returnedFacilityList.size(), is(3));

    for (Facility facility : returnedFacilityList) {
      assertThat(facility.getCode().equals(facility1.getCode())
        || facility.getCode().equals(facility2.getCode())
        || facility.getCode().equals(facility3.getCode()), is(true));
    }

  }

  @Test
  public void shouldGetHomeFacilityIfUserHasRight() throws Exception {
    //Arrange
    Facility homeFacility = make(a(defaultFacility));
    mapper.insert(homeFacility);

    Role r1 = new Role("r1", "random description");
    roleRightsMapper.insertRole(r1);

    roleRightsMapper.createRoleRight(r1, CREATE_REQUISITION);
    roleRightsMapper.createRoleRight(r1, CONFIGURE_RNR);

    User user = make(a(defaultUser, with(facilityId, homeFacility.getId())));

    userMapper.insert(user);
    roleAssignmentMapper.insertRoleAssignment(user.getId(), 1L, null, r1.getId());

    //Act
    Facility returnedFacility = mapper.getHomeFacilityWithRights(user.getId(), "{CONFIGURE_RNR}");

    //Assert
    assertThat(returnedFacility.getId(), is(homeFacility.getId()));
  }

  @Test
  public void shouldGetDistinctFacilitiesInARequisitionGroup() throws Exception {
    //Arrange
    final RequisitionGroup rg1 = make(a(defaultRequisitionGroup, with(RequisitionGroupBuilder.code, "RG1")));
    final RequisitionGroup rg2 = make(a(defaultRequisitionGroup, with(RequisitionGroupBuilder.code, "RG2")));
    requisitionGroupMapper.insert(rg1);
    requisitionGroupMapper.insert(rg2);

    final Facility facilityInBothRG1AndRG2 = make(a(FacilityBuilder.defaultFacility, with(FacilityBuilder.code, "F1")));
    final Facility facilityInRG2 = make(a(FacilityBuilder.defaultFacility, with(FacilityBuilder.code, "F2")));
    Facility facilityNotInAny = make(a(FacilityBuilder.defaultFacility, with(FacilityBuilder.code, "F3")));
    mapper.insert(facilityInBothRG1AndRG2);
    mapper.insert(facilityInRG2);
    mapper.insert(facilityNotInAny);

    requisitionGroupMemberMapper.insert(new RequisitionGroupMember(rg1, facilityInBothRG1AndRG2));
    requisitionGroupMemberMapper.insert(new RequisitionGroupMember(rg2, facilityInBothRG1AndRG2));
    requisitionGroupMemberMapper.insert(new RequisitionGroupMember(rg2, facilityInRG2));

    //Act
    List<Facility> facilities = mapper.getAllInRequisitionGroups("{" + rg1.getId() + "," + rg2.getId() + " }");

    //Assert
    assertThat(facilities.size(), is(2));
    assertTrue(CollectionUtils.exists(facilities, new Predicate() {
      @Override
      public boolean evaluate(Object o) {
        Facility facility = (Facility) o;
        return facility.getCode().equals(facilityInBothRG1AndRG2.getCode());
      }
    }));
    assertTrue(CollectionUtils.exists(facilities, new Predicate() {
      @Override
      public boolean evaluate(Object o) {
        Facility facility = (Facility) o;
        return facility.getCode().equals(facilityInRG2.getCode());
      }
    }));
  }

  @Test
  public void shouldGetFacilityByCode() throws Exception {
    GeographicLevel level = new GeographicLevel(1L);
    GeographicZone zone0 = new GeographicZone(3000L, "Z0", "Z0", level, null);
    geographicZoneMapper.insert(zone0);
    GeographicZone zone1 = new GeographicZone(1000L, "Z1", "Z1", level, zone0);
    geographicZoneMapper.insert(zone1);

    List<FacilityType> allTypes = mapper.getAllTypes();
    FacilityType facilityType = allTypes.get(1);

    Facility facility = insertFacility("CODE123", facilityType, zone1, null);

    Facility facilityFromDatabase = mapper.getByCode("CODE123");

    assertThat(facilityFromDatabase.getId(), is(facility.getId()));
    assertThat(facilityFromDatabase.getCode(), is(facility.getCode()));
    assertThat(facilityFromDatabase.getName(), is(facility.getName()));
    assertThat(facilityFromDatabase.getFacilityType().getCode(), is(facilityType.getCode()));
    assertThat(facilityFromDatabase.getOperatedBy().getCode(), is(facility.getOperatedBy().getCode()));
    assertThat(facilityFromDatabase.getGeographicZone().getName(), is(zone1.getName()));
  }

  @Test
  public void shouldGetFacilityByCodeIgnoringCase() throws Exception {
    mapper.insert(make(a(defaultFacility, with(code, "F_CODE_111"))));

    Facility facilityFromDatabase = mapper.getByCode("f_code_111");

    assertThat(facilityFromDatabase.getCode(), is("F_CODE_111"));
  }

  @Test
  public void shouldGetAllInDeliveryZoneAndOrderByGeographicZoneParentAndFacilityName() {
    ProcessingSchedule processingSchedule = make(a(ProcessingScheduleBuilder.defaultProcessingSchedule));
    processingScheduleMapper.insert(processingSchedule);

    Program program = make(a(ProgramBuilder.defaultProgram));
    programMapper.insert(program);

    DeliveryZone deliveryZone = make(a(DeliveryZoneBuilder.defaultDeliveryZone));
    deliveryZoneMapper.insert(deliveryZone);

    deliveryZoneProgramScheduleMapper.insert(new DeliveryZoneProgramSchedule(deliveryZone.getId(),
      program.getId(), processingSchedule.getId()));

    Facility facility1 = insertMemberFacility(deliveryZone, program, "F10A", "facility", 10l, true);

    insertMemberFacility(deliveryZone, program, "F10011", "facility2", 9l, true);
    insertMemberFacility(deliveryZone, program, "F10010", "facility3", 9L, true);
    insertMemberFacility(deliveryZone, program, "F10012", "facility4", 9L, false);

    Program unsupportedProgram = new Program();
    unsupportedProgram.setId(2l);

    insertMemberFacility(deliveryZone, unsupportedProgram, "F10013", "facility5", 9L, true);

    List<Facility> memberFacilities = mapper.getAllInDeliveryZoneFor(deliveryZone.getId(), program.getId());

    assertThat(memberFacilities.size(), is(3));
    assertThat(memberFacilities.get(0).getCode(), is("F10A"));
    assertThat(memberFacilities.get(0).getId(), is(facility1.getId()));
    assertThat(memberFacilities.get(0).getGeographicZone().getId(), is(10L));
    assertThat(memberFacilities.get(1).getCode(), is("F10011"));
    assertThat(memberFacilities.get(2).getCode(), is("F10010"));
  }

  @Test
  public void shouldReturnAllFacilitiesMatchingModifiedDate() throws Exception {

    String facilityCode1 = "fc1";
    String facilityCode2 = "fc2";
    Date date1 = new Date();
    Date date2 = new Date(date1.getTime() + 123123);

    Facility facility1 = make(a(defaultFacility, with(code, facilityCode1), with(FacilityBuilder.modifiedDate, date1)));
    Facility facility2 = make(a(defaultFacility, with(code, facilityCode2), with(FacilityBuilder.modifiedDate, date1)));

    mapper.insert(facility1);
    mapper.insert(facility2);
    Program program1 = new Program(1L);
    Program program2 = new Program(2L);

    insertProgramSupported(program1, facility1, date1);
    insertProgramSupported(program1, facility2, date2);
    insertProgramSupported(program2, facility1, date2);

    List<Facility> allByDateModified = mapper.getAllByProgramSupportedModifiedDate(date1);

    assertThat(allByDateModified.size(), is(1));
    assertThat(allByDateModified.get(0).getId(), is(facility1.getId()));
    assertThat(allByDateModified.get(0).getCode(), is(facilityCode1));
    assertThat(allByDateModified.get(0).getSupportedPrograms().size(), is(2));
  }

  @Test
  public void getAllFacilitiesByRequisitionGroupMembersModifiedDate() throws Exception {
    Date modifiedDate = new Date();
    Facility facility = make(a(defaultFacility));
    mapper.insert(facility);

    RequisitionGroup requisitionGroup = make(a(defaultRequisitionGroup));
    requisitionGroupMapper.insert(requisitionGroup);

    RequisitionGroupMember requisitionGroupMember = new RequisitionGroupMember(requisitionGroup, facility);
    requisitionGroupMember.setModifiedDate(modifiedDate);
    requisitionGroupMemberMapper.insert(requisitionGroupMember);

    List<Facility> facilities = mapper.getAllByRequisitionGroupMemberModifiedDate(modifiedDate);

    assertThat(facilities.size(), is(1));
    assertThat(facilities.get(0).getId(), is(facility.getId()));
    assertThat(facilities.get(0).getCode(), is(facility.getCode()));
  }

  @Test
  public void shouldGetEnabledWarehouses() throws Exception {

    Facility enabledFacility = make(a(defaultFacility));
    mapper.insert(enabledFacility);

    Facility disabledFacility = make(a(FacilityBuilder.defaultFacility, with(code, "FF110"), with(name, "D1100"), with(enabled, false)));
    mapper.insert(disabledFacility);

    Program program = make(a(defaultProgram));
    programMapper.insert(program);

    SupervisoryNode supervisoryNode = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode, with(SupervisoryNodeBuilder.facility, enabledFacility)));
    supervisoryNodeMapper.insert(supervisoryNode);

    SupplyLine supplyLine = make(a(SupplyLineBuilder.defaultSupplyLine, with(SupplyLineBuilder.defaultProgram, program), with(SupplyLineBuilder.facility, enabledFacility),
      with(SupplyLineBuilder.supervisoryNode, supervisoryNode)));
    supplyLineMapper.insert(supplyLine);

    List<Facility> warehouses = mapper.getEnabledWarehouses();

    assertThat(warehouses.size(), is(1));
    assertThat(warehouses.get(0).getId(), is(not(disabledFacility.getId())));
    assertThat(warehouses.get(0).getId(), is(enabledFacility.getId()));
    assertThat(warehouses.get(0).getCode(), is(enabledFacility.getCode()));

  }

  @Test
  public void shouldGetAllChildFacilities() throws Exception {
    Facility facility1 = make(a(FacilityBuilder.defaultFacility, with(code, "FF110"), with(name, "D1100")));
    mapper.insert(facility1);
    Facility facility2 = make(a(FacilityBuilder.defaultFacility, with(code, "D00"), with(name, "F110"), with(parentFacilityId, facility1.getId())));
    Facility facility3 = make(a(FacilityBuilder.defaultFacility, with(code, "FF1100"), with(name, "F1100"), with(parentFacilityId, facility1.getId())));

    mapper.insert(facility2);
    mapper.insert(facility3);

    List<Facility> expectedFacilities = asList(facility2, facility3);

    List<Facility> actualChildFacilities = mapper.getChildFacilities(facility1);

    assertThat(actualChildFacilities.size(), is(expectedFacilities.size()));
    assertThat(actualChildFacilities.get(0).getId(), is(expectedFacilities.get(0).getId()));
    assertThat(actualChildFacilities.get(1).getId(), is(expectedFacilities.get(1).getId()));
  }

  @Test
  public void shouldUpdateOnlyTypeAndGeoZoneInVirtualFacilities() throws Exception {

    GeographicLevel level = new GeographicLevel(1L);
    GeographicZone zone0 = new GeographicZone(3000L, "Z0", "Z0", level, null);
    geographicZoneMapper.insert(zone0);
    GeographicZone zone1 = new GeographicZone(1000L, "Z1", "Z1", level, zone0);
    geographicZoneMapper.insert(zone1);
    GeographicZone zone2 = new GeographicZone(2000L, "Z2", "Z2", level, zone1);
    geographicZoneMapper.insert(zone2);


    List<FacilityType> allTypes = mapper.getAllTypes();
    FacilityType facilityType1 = allTypes.get(1);
    FacilityType facilityType2 = allTypes.get(2);

    Facility parentFacility = insertFacility("FF110", facilityType1, zone1, null);

    Facility facility = insertFacility("F2222", facilityType1, zone1, null);

    Facility virtualFacility = insertFacility("F3333", facilityType1, zone1, parentFacility.getId());

    parentFacility.setFacilityType(facilityType2);
    parentFacility.setGeographicZone(zone2);
    mapper.update(parentFacility);

    mapper.updateVirtualFacilities(parentFacility);

    Facility fetchedVirtualFacility = mapper.getById(virtualFacility.getId());
    assertThat(fetchedVirtualFacility.getFacilityType().getCode(), is(facilityType2.getCode()));
    assertThat(fetchedVirtualFacility.getGeographicZone().getCode(), is(zone2.getCode()));

    Facility fetchedFacility = mapper.getById(facility.getId());
    assertThat(fetchedFacility.getFacilityType().getCode(), is(facilityType1.getCode()));
    assertThat(fetchedFacility.getGeographicZone().getCode(), is(zone1.getCode()));

  }

  private ProgramSupported insertProgramSupported(Program program, Facility supportedFacility, Date modifiedDate) {
    ProgramSupported programSupported = make(a(defaultProgramSupported,
      with(supportedProgram, program),
      with(supportedFacilityId, supportedFacility.getId()),
      with(dateModified, modifiedDate)));

    programSupportedMapper.insert(programSupported);

    return programSupported;
  }

  private Facility insertMemberFacility(DeliveryZone zone, Program program, String facilityCode, String facilityName,
                                        Long geoZoneId, Boolean facilityActive) {
    Facility facility = make(a(FacilityBuilder.defaultFacility,
      with(code, facilityCode),
      with(name, facilityName),
      with(geographicZoneId, geoZoneId),
      with(active, facilityActive)));
    mapper.insert(facility);

    ProgramSupported programSupported = new ProgramSupported();
    programSupported.setFacilityId(facility.getId());
    programSupported.setProgram(program);
    programSupportedMapper.insert(programSupported);
    DeliveryZoneMember member1 = new DeliveryZoneMember(zone, facility);
    deliveryZoneMemberMapper.insert(member1);
    return facility;
  }

  private Facility insertFacility(String facilityCode, FacilityType facilityType, GeographicZone zone, Long parentId) {
    Facility facility = make(a(defaultFacility,
      with(code, facilityCode),
      with(typeId, facilityType.getId()),
      with(parentFacilityId, parentId)));

    facility.setGeographicZone(zone);

    mapper.insert(facility);

    return facility;
  }


}
