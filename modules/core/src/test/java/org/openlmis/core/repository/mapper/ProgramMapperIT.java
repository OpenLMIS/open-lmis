/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository.mapper;

import org.junit.Test;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.builder.ProgramSupportedBuilder;
import org.openlmis.core.builder.SupervisoryNodeBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.service.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.lang.Boolean.FALSE;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.ProgramBuilder.*;
import static org.openlmis.core.builder.ProgramSupportedBuilder.*;
import static org.openlmis.core.builder.UserBuilder.defaultUser;
import static org.openlmis.core.builder.UserBuilder.facilityId;

@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class ProgramMapperIT extends SpringIntegrationTest {

  public static final String PROGRAM_CODE = "HIV";
  public static final Integer PROGRAM_ID = 1;

  @Autowired
  ProgramSupportedMapper programSupportedMapper;

  @Autowired
  FacilityMapper facilityMapper;

  @Autowired
  ProgramMapper programMapper;

  @Autowired
  RoleRightsMapper roleRightsMapper;

  @Autowired
  RoleAssignmentMapper roleAssignmentMapper;

  @Autowired
  UserMapper userMapper;

  @Autowired
  SupervisoryNodeMapper supervisoryNodeMapper;

  @Test
  public void shouldGetProgramsWhichAreActiveByFacilityCode() {
    Facility facility = make(a(FacilityBuilder.defaultFacility));
    facilityMapper.insert(facility);
    Program program = make(a(defaultProgram, with(programId, 1)));
    programMapper.insert(program);
    ProgramSupported programSupported = make(a(defaultProgramSupported, with(supportedFacilityId, facility.getId()), with(supportedProgram, program)));
    programSupportedMapper.addSupportedProgram(programSupported);

    List<Program> programs = programMapper.getActiveByFacility(facility.getId());

    assertThat(programs.size(), is(1));
    assertThat(programs.get(0).getCode(), is(ProgramBuilder.PROGRAM_CODE));
  }

  @Test
  public void shouldGetAllPrograms() throws Exception {
    List<Program> programs = programMapper.getAll();
    assertEquals(4, programs.size());
    assertThat(programs.get(0).getCode(), is("ESS_MEDS"));
  }

  @Test
  public void shouldGetProgramsSupportedByFacility() throws Exception {
    Facility facility = make(a(defaultFacility));
    facilityMapper.insert(facility);
    Program program = make(a(defaultProgram, with(programId, 1)));
    programMapper.insert(program);
    ProgramSupported programSupported = make(a(defaultProgramSupported, with(supportedFacilityId, facility.getId()),
      with(supportedProgram, program)));
    programSupportedMapper.addSupportedProgram(programSupported);
    List<Program> supportedPrograms = programMapper.getByFacilityId(facility.getId());
    assertThat(supportedPrograms.get(0).getCode(), is(ProgramBuilder.PROGRAM_CODE));
  }

  @Test
  public void shouldGetIdByCode() throws Exception {
    Program program = make(a(defaultProgram));
    programMapper.insert(program);
    assertThat(program.getId(), is(programMapper.getIdForCode(ProgramBuilder.PROGRAM_CODE)));
  }

  @Test
  public void shouldReturnProgramById() throws Exception {
    Program program = make(a(defaultProgram));
    programMapper.insert(program);
    assertThat(programMapper.getById(program.getId()), is(program));
  }

  @Test
  public void shouldGetAllProgramsForUserSupervisedFacilitiesForWhichHeHasCreateRnrRight() {
    Facility facility = make(a(defaultFacility));
    facilityMapper.insert(facility);
    SupervisoryNode node = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode));
    node.setFacility(facility);
    SupervisoryNode supervisoryNode = insertSupervisoryNode(node);

    Program activeProgramWithCreateRight = insertProgram(make(a(defaultProgram, with(programCode, "P1"))));
    Program inactiveProgram = insertProgram(make(a(defaultProgram, with(programCode, "P2"), with(programStatus, false))));
    Program activeProgramWithConfigureRight = insertProgram(make(a(defaultProgram, with(programCode, "P3"))));
    Program activeProgramForHomeFacility = insertProgram(make(a(defaultProgram, with(programCode, "P4"))));

    User user = insertUser(facility);

    Role createRnrRole = new Role("R1", FALSE, "Create Requisition");
    roleRightsMapper.insertRole(createRnrRole);
    roleRightsMapper.createRoleRight(createRnrRole.getId(), Right.CREATE_REQUISITION);
    insertRoleAssignments(activeProgramWithCreateRight, user, createRnrRole, supervisoryNode);
    insertRoleAssignments(inactiveProgram, user, createRnrRole, supervisoryNode);
    insertRoleAssignments(activeProgramForHomeFacility, user, createRnrRole, null);

    Role configureRnrRole = new Role("R2", FALSE, "View Rnr Role");
    roleRightsMapper.insertRole(configureRnrRole);
    roleRightsMapper.createRoleRight(configureRnrRole.getId(), Right.CONFIGURE_RNR);
    insertRoleAssignments(activeProgramWithConfigureRight, user, configureRnrRole, supervisoryNode);

    List<Program> programs = programMapper.getUserSupervisedActivePrograms(user.getId(), "{CREATE_REQUISITION, CONFIGURE_RNR}");

    assertThat(programs.size(), is(2));
    assertThat(programs.contains(activeProgramWithCreateRight), is(true));
    assertThat(programs.contains(activeProgramWithConfigureRight), is(true));
  }

  @Test
  public void shouldFetchActiveProgramsSupportedByAFacilityForAUserWithRight() {
    Program activeProgram = insertProgram(make(a(defaultProgram, with(programCode, "p1"))));
    Program anotherActiveProgram = insertProgram(make(a(defaultProgram, with(programCode, "p2"))));
    Program inactiveProgram = insertProgram(make(a(defaultProgram, with(programCode, "p3"), with(programStatus, false))));

    Facility facility = insertFacility(make(a(defaultFacility)));
    User user = insertUser(facility);

    Role r1 = new Role("r1", FALSE, "random description");
    roleRightsMapper.insertRole(r1);
    roleRightsMapper.createRoleRight(r1.getId(), Right.CREATE_REQUISITION);

    Role r2 = new Role("r2", FALSE, "authorize role");
    roleRightsMapper.insertRole(r2);
    roleRightsMapper.createRoleRight(r2.getId(), Right.AUTHORIZE_REQUISITION);

    insertRoleAssignments(activeProgram, user, r1, null);
    insertRoleAssignments(anotherActiveProgram, user, r2, null);
    insertRoleAssignments(inactiveProgram, user, r1, null);


    insertProgramSupportedForFacility(activeProgram, facility, true);
    insertProgramSupportedForFacility(anotherActiveProgram, facility, true);
    insertProgramSupportedForFacility(inactiveProgram, facility, true);

    final String rights = "{CREATE_REQUISITION, AUTHORIZE_REQUISITION}";

    List<Program> programs = programMapper.getProgramsSupportedByFacilityForUserWithRights(facility.getId(), user.getId(), rights);
    assertThat(programs.size(), is(2));
    assertTrue(programs.contains(activeProgram));
    assertTrue(programs.contains(anotherActiveProgram));
  }


  @Test
  public void shouldGetActiveProgramsForUserWithGivenRights() throws Exception {
    Program activeProgram = insertProgram(make(a(defaultProgram, with(programCode, "p1"))));
    Program inactiveProgram = insertProgram(make(a(defaultProgram, with(programCode, "p3"), with(programStatus, false))));

    Facility facility = insertFacility(make(a(defaultFacility)));
    User user = insertUser(facility);

    Role r1 = new Role("r1", FALSE, "random description");
    roleRightsMapper.insertRole(r1);
    roleRightsMapper.createRoleRight(r1.getId(), Right.APPROVE_REQUISITION);

    insertRoleAssignments(activeProgram, user, r1, null);
    insertRoleAssignments(inactiveProgram, user, r1, null);


    final String rights = "{APPROVE_REQUISITION}";

    List<Program> programs = programMapper.getActiveProgramsForUserWithRights(user.getId(), rights);
    assertThat(programs.size(), is(1));
    assertTrue(programs.contains(activeProgram));
  }

  @Test
  public void shouldSetTemplateConfiguredFlag() {
    Program program = insertProgram(make(a(defaultProgram, with(programCode, "p1"), with(templateStatus, false))));
    programMapper.setTemplateConfigured(program.getId());

    Program returnedProgram = programMapper.getById(program.getId());

    assertThat(returnedProgram.isTemplateConfigured(), is(true));
  }

  private SupervisoryNode insertSupervisoryNode(SupervisoryNode supervisoryNode) {
    supervisoryNodeMapper.insert(supervisoryNode);
    return supervisoryNode;
  }

  private Program insertProgram(Program program) {
    programMapper.insert(program);
    return program;
  }

  private void insertProgramSupportedForFacility(Program program, Facility facility, boolean isActive) {
    ProgramSupported defaultProgram = make(a(defaultProgramSupported,
      with(supportedFacilityId, facility.getId()),
      with(supportedProgram, program),
      with(ProgramSupportedBuilder.isActive, isActive)));
    programSupportedMapper.addSupportedProgram(defaultProgram);
  }

  private Facility insertFacility(Facility facility) {
    facilityMapper.insert(facility);
    return facility;
  }

  private Role insertRoleAssignments(Program program, User user, Role role, SupervisoryNode supervisoryNode) {
    Integer supervisoryNodeId = supervisoryNode == null ? null : supervisoryNode.getId();
    roleAssignmentMapper.insertRoleAssignment(user.getId(), program.getId(), supervisoryNodeId, role.getId());
    return role;
  }

  private User insertUser(Facility facility) {
    User user = make(a(defaultUser, with(facilityId, facility.getId())));
    userMapper.insert(user);
    return user;
  }
}
