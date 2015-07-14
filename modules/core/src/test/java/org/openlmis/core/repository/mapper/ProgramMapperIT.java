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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.collection.IsIn.isIn;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.builder.ProgramSupportedBuilder;
import org.openlmis.core.builder.SupervisoryNodeBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.ProgramBuilder.*;
import static org.openlmis.core.builder.ProgramSupportedBuilder.*;
import static org.openlmis.core.builder.UserBuilder.defaultUser;
import static org.openlmis.core.builder.UserBuilder.facilityId;

@Category(IntegrationTests.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
@RunWith(SpringJUnit4ClassRunner.class)
public class ProgramMapperIT {

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

  @Autowired
  QueryExecutor queryExecutor;

  @Test
  public void shouldGetProgramsWhichAreActiveByFacilityCode() {
    Facility facility = make(a(FacilityBuilder.defaultFacility));
    facilityMapper.insert(facility);
    Program program = make(a(defaultProgram, with(programId, 1L)));
    programMapper.insert(program);
    ProgramSupported programSupported = make(a(defaultProgramSupported, with(supportedFacilityId, facility.getId()), with(supportedProgram, program)));
    programSupportedMapper.insert(programSupported);

    List<Program> programs = programMapper.getActiveByFacility(facility.getId());

    assertThat(programs.size(), is(1));
    assertThat(programs.get(0).getCode(), is(ProgramBuilder.PROGRAM_CODE));
  }

  @Test
  public void shouldGetAllPullPrograms() throws Exception {
    List<Program> programs = programMapper.getAllPullPrograms();
    assertThat(4, is(programs.size()));
    assertThat(programs.get(0).getCode(), is("ESS_MEDS"));
    assertThat(programs.get(1).getCode(), is("HIV"));
    assertThat(programs.get(2).getCode(), is("MALARIA"));
    assertThat(programs.get(3).getCode(), is("TB"));
  }

  @Test
  public void shouldGetAllPushPrograms() throws Exception {
    List<Program> programs = programMapper.getAllPushPrograms();
    assertThat(1, is( programs.size()));
    assertThat(programs.get(0).getCode(), is("VACCINES"));
  }

  @Test
  public void shouldGetAllIvdPrograms() throws Exception {
    Program p = programMapper.getByCode("HIV");
    p.setEnableIvdForm(true);
    programMapper.update(p);

    List<Program> programs = programMapper.getAllIvdPrograms();
    assertThat(1, is( programs.size()));
    assertThat(programs.get(0).getCode(), is("HIV"));
  }

  @Test
  public void shouldGetAllPrograms() throws Exception {
    List<Program> programs = programMapper.getAll();
    assertThat(5, is(programs.size()));
    assertThat(programs.get(0).getCode(), is("ESS_MEDS"));
  }

  @Test
  public void shouldGetProgramsSupportedByFacility() throws Exception {
    Facility facility = make(a(defaultFacility));
    facilityMapper.insert(facility);
    Program program = make(a(defaultProgram, with(programId, 1L)));
    programMapper.insert(program);
    ProgramSupported programSupported = make(a(defaultProgramSupported, with(supportedFacilityId, facility.getId()),
      with(supportedProgram, program)));
    programSupportedMapper.insert(programSupported);
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
  public void shouldGetByCode() throws Exception {
    Program program = make(a(defaultProgram));
    programMapper.insert(program);
    assertThat(program, is(programMapper.getByCode(ProgramBuilder.PROGRAM_CODE)));
  }

  @Test
  public void shouldReturnProgramById() throws Exception {
    Program program = make(a(defaultProgram));
    programMapper.insert(program);
    assertThat(programMapper.getById(program.getId()), is(program));
  }

  @Test
  public void shouldGetAllPullProgramsForUserSupervisedFacilitiesForWhichHeHasCreateRnrRight() {
    Facility facility = make(a(defaultFacility));
    facilityMapper.insert(facility);
    SupervisoryNode node = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode));
    node.setFacility(facility);
    SupervisoryNode supervisoryNode = insertSupervisoryNode(node);

    Program activeProgramWithCreateRight = insertProgram(make(a(defaultProgram, with(programCode, "P1"))));
    Program inactiveProgram = insertProgram(make(a(defaultProgram, with(programCode, "P2"), with(programActive, false))));
    Program activeProgramWithConfigureRight = insertProgram(make(a(defaultProgram, with(programCode, "P3"))));
    Program activeProgramForHomeFacility = insertProgram(make(a(defaultProgram, with(programCode, "P4"))));
    Program activePushProgramWithCreateRight = insertProgram(make(a(defaultProgram, with(programCode, "P5"), with(push, true))));

    User user = insertUser(facility);

    Role createRnrRole = new Role("R1", "Create Requisition");
    roleRightsMapper.insertRole(createRnrRole);
    roleRightsMapper.createRoleRight(createRnrRole, RightName.CREATE_REQUISITION);
    insertRoleAssignments(activeProgramWithCreateRight, user, createRnrRole, supervisoryNode);
    insertRoleAssignments(inactiveProgram, user, createRnrRole, supervisoryNode);
    insertRoleAssignments(activeProgramForHomeFacility, user, createRnrRole, null);
    insertRoleAssignments(activePushProgramWithCreateRight, user, createRnrRole, supervisoryNode);

    Role configureRnrRole = new Role("R2", "View Rnr Role");
    roleRightsMapper.insertRole(configureRnrRole);
    roleRightsMapper.createRoleRight(configureRnrRole, RightName.CONFIGURE_RNR);
    insertRoleAssignments(activeProgramWithConfigureRight, user, configureRnrRole, supervisoryNode);

    List<Program> programs = programMapper.getUserSupervisedActivePrograms(user.getId(), "{CREATE_REQUISITION, CONFIGURE_RNR}");

    assertThat(programs.size(), is(2));
    assertThat(activeProgramWithCreateRight, isIn(programs));
    assertThat(activeProgramWithConfigureRight, isIn(programs));
    assertThat(activePushProgramWithCreateRight, not(isIn(programs)));
  }

  @Test
  public void shouldFetchActiveProgramsSupportedByAFacilityForAUserWithRight() {
    Program activeProgram = insertProgram(make(a(defaultProgram, with(programCode, "p1"))));
    Program anotherActiveProgram = insertProgram(make(a(defaultProgram, with(programCode, "p2"))));
    Program inactiveProgram = insertProgram(make(a(defaultProgram, with(programCode, "p3"), with(programActive, false))));
    Program activePushProgram = insertProgram(make(a(defaultProgram, with(programCode, "p4"), with(push, true))));

    Facility facility = insertFacility(make(a(defaultFacility)));
    User user = insertUser(facility);

    Role r1 = new Role("r1", "random description");
    roleRightsMapper.insertRole(r1);
    roleRightsMapper.createRoleRight(r1, RightName.CREATE_REQUISITION);

    Role r2 = new Role("r2", "authorize role");
    roleRightsMapper.insertRole(r2);
    roleRightsMapper.createRoleRight(r2, RightName.AUTHORIZE_REQUISITION);

    insertRoleAssignments(activeProgram, user, r1, null);
    insertRoleAssignments(anotherActiveProgram, user, r2, null);
    insertRoleAssignments(inactiveProgram, user, r1, null);
    insertRoleAssignments(activePushProgram, user, r1, null);


    insertProgramSupportedForFacility(activeProgram, facility, true);
    insertProgramSupportedForFacility(anotherActiveProgram, facility, true);
    insertProgramSupportedForFacility(inactiveProgram, facility, true);
    insertProgramSupportedForFacility(activePushProgram, facility, true);

    final String rights = "{CREATE_REQUISITION, AUTHORIZE_REQUISITION}";

    List<Program> programs = programMapper.getProgramsSupportedByUserHomeFacilityWithRights(facility.getId(), user.getId(), rights);
    assertThat(programs.size(), is(2));
    assertThat(activeProgram, isIn(programs));
    assertThat(anotherActiveProgram, isIn(programs));
    assertThat(activePushProgram, not(isIn(programs)));
  }

  @Test
  public void shouldGetActiveProgramsForUserWithGivenRights() throws Exception {
    Program activeProgram = insertProgram(make(a(defaultProgram, with(programCode, "p1"))));
    Program inactiveProgram = insertProgram(make(a(defaultProgram, with(programCode, "p3"), with(programActive, false))));

    Facility facility = insertFacility(make(a(defaultFacility)));
    User user = insertUser(facility);

    Role r1 = new Role("r1", "random description");
    roleRightsMapper.insertRole(r1);
    roleRightsMapper.createRoleRight(r1, RightName.APPROVE_REQUISITION);

    insertRoleAssignments(activeProgram, user, r1, null);
    insertRoleAssignments(inactiveProgram, user, r1, null);


    final String rights = "{APPROVE_REQUISITION}";

    List<Program> programs = programMapper.getActiveProgramsForUserWithRights(user.getId(), rights);
    assertThat(programs.size(), is(1));
    assertThat(programs, hasItem(activeProgram));
  }

  @Test
  public void shouldSetTemplateConfiguredFlag() {
    Program program = insertProgram(make(a(defaultProgram, with(programCode, "p1"), with(templateStatus, false))));
    programMapper.setTemplateConfigured(program.getId());

    Program returnedProgram = programMapper.getById(program.getId());

    assertThat(returnedProgram.getTemplateConfigured(), is(true));
  }

  @Test
  public void shouldSetRegimenTemplateConfiguredFlag() {
    Program program = insertProgram(make(a(defaultProgram, with(programCode, "p1"), with(templateStatus, false), with(regimenTemplateConfigured, false))));
    programMapper.setRegimenTemplateConfigured(program.getId());

    Program returnedProgram = programMapper.getById(program.getId());

    assertThat(returnedProgram.getRegimenTemplateConfigured(), is(true));
  }

  @Test
  public void shouldGetProgramsForAUserByFacilityAndRights() throws Exception {
    Program activeProgram = insertProgram(make(a(defaultProgram, with(programCode, "p1"))));
    Program inactiveProgram = insertProgram(make(a(defaultProgram, with(programCode, "p3"), with(programActive, false))));

    Facility facility = insertFacility(make(a(defaultFacility)));
    User user = insertUser(facility);

    Role r1 = new Role("r1", "random description");
    roleRightsMapper.insertRole(r1);
    roleRightsMapper.createRoleRight(r1, RightName.VIEW_REQUISITION);

    insertRoleAssignments(activeProgram, user, r1, null);
    insertRoleAssignments(inactiveProgram, user, r1, null);

    insertProgramSupportedForFacility(activeProgram, facility, true);
    insertProgramSupportedForFacility(inactiveProgram, facility, true);

    final String rights = "{VIEW_REQUISITION}";

    List<Program> programs = programMapper.getProgramsForUserByFacilityAndRights(facility.getId(), user.getId(), rights);
    assertThat(programs.size(), is(1));
    assertThat(programs, hasItem(activeProgram));
  }


  @Test
  public void shouldGetAllProgramsInOrderByRegimentTemplateConfiguredAndName() {
    insertProgram(make(a(defaultProgram, with(regimenTemplateConfigured, true))));
    List<Program> programs = programMapper.getAllByRegimenTemplate();
    assertThat(programs.size(), is(6));
    //assertThat(programs.get(0).getCode(), is(ProgramBuilder.PROGRAM_CODE));
  }

  @Test
  public void shouldSetRegimenTemplateConfigured() {
    Program program = insertProgram(make(a(defaultProgram, with(programCode, "p1"), with(regimenTemplateConfigured, false))));

    programMapper.setRegimenTemplateConfigured(program.getId());
    Program returnedProgram = programMapper.getById(program.getId());

    assertThat(returnedProgram.getRegimenTemplateConfigured(), is(true));

  }

  @Test
  public void shouldSetSendFeedFlag() throws SQLException {
    Program program = new Program();
    program.setCode("HIV");
    programMapper.setFeedSendFlag(program, true);
    boolean flag;

    try (ResultSet set = queryExecutor.execute("SELECT sendFeed from programs WHERE code = ?", "HIV")) {
      set.next();
      flag = set.getBoolean(1);
    }

    assertThat(flag, is(true));
  }

  @Test
  public void shouldResetSendFeedFlag() throws SQLException {
    Program program = new Program();
    program.setCode("HIV");
    programMapper.setFeedSendFlag(program, false);
    boolean flag = true;

    try (ResultSet set = queryExecutor.execute("SELECT sendFeed from programs WHERE code = ?", "HIV")) {
      set.next();
      flag = set.getBoolean(1);
    }

    assertThat(flag, is(false));
  }

  @Test
  public void shouldGetAllProgramsWithSendFeedFlagTrue() throws Exception {
    Program program = new Program();
    program.setCode("HIV");
    programMapper.setFeedSendFlag(program, true);

    List<Program> programsForNotification = programMapper.getProgramsForNotification();

    assertThat(programsForNotification.size(), is(1));
    assertThat(programsForNotification.get(0).getCode(), is("HIV"));
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
    programSupportedMapper.insert(defaultProgram);
  }

  private Facility insertFacility(Facility facility) {
    facilityMapper.insert(facility);
    return facility;
  }

  private Role insertRoleAssignments(Program program, User user, Role role, SupervisoryNode supervisoryNode) {
    Long supervisoryNodeId = supervisoryNode == null ? null : supervisoryNode.getId();
    roleAssignmentMapper.insertRoleAssignment(user.getId(), program.getId(), supervisoryNodeId, role.getId());
    return role;
  }

  private User insertUser(Facility facility) {
    User user = make(a(defaultUser, with(facilityId, facility.getId())));
    userMapper.insert(user);
    return user;
  }
}
