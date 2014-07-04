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
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.SupervisoryNodeBuilder;
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
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.core.builder.ProgramBuilder.programCode;
import static org.openlmis.core.builder.RequisitionGroupBuilder.defaultRequisitionGroup;
import static org.openlmis.core.builder.SupervisoryNodeBuilder.*;
import static org.openlmis.core.builder.UserBuilder.defaultUser;
import static org.openlmis.core.builder.UserBuilder.facilityId;
import static org.openlmis.core.domain.RightName.CONFIGURE_RNR;
import static org.openlmis.core.domain.RightName.CREATE_REQUISITION;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class SupervisoryNodeMapperIT {

  SupervisoryNode supervisoryNode;
  Facility facility;

  @Autowired
  SupervisoryNodeMapper supervisoryNodeMapper;
  @Autowired
  RoleAssignmentMapper roleAssignmentMapper;
  @Autowired
  FacilityMapper facilityMapper;
  @Autowired
  RoleRightsMapper roleRightsMapper;
  @Autowired
  ProgramMapper programMapper;
  @Autowired
  UserMapper userMapper;
  @Autowired
  RequisitionGroupMapper requisitionGroupMapper;

  @Before
  public void setUp() throws Exception {
    supervisoryNode = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode));
    facility = make(a(FacilityBuilder.defaultFacility));
    facilityMapper.insert(facility);
    supervisoryNode.setFacility(facility);
  }

  @Test
  public void shouldInsertSupervisoryNode() throws Exception {
    supervisoryNodeMapper.insert(supervisoryNode);

    SupervisoryNode resultSupervisoryNode = supervisoryNodeMapper.getSupervisoryNode(supervisoryNode.getId());

    assertThat(resultSupervisoryNode, is(notNullValue()));
    assertThat(resultSupervisoryNode.getCode(), CoreMatchers.is(SUPERVISORY_NODE_CODE));
    assertThat(resultSupervisoryNode.getName(), CoreMatchers.is(SupervisoryNodeBuilder.SUPERVISORY_NODE_NAME));
    assertThat(resultSupervisoryNode.getFacility().getId(), is(facility.getId()));
  }

  @Test
  public void shouldUpdateSupervisoryNode() throws Exception {
    supervisoryNodeMapper.insert(supervisoryNode);

    SupervisoryNode supervisoryNodeParent = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode, with(SupervisoryNodeBuilder.code, "PN")));
    supervisoryNodeParent.setFacility(facility);
    supervisoryNodeMapper.insert(supervisoryNodeParent);

    supervisoryNode.setCode("updated code");
    supervisoryNode.setName("updated name");
    supervisoryNode.setDescription("updated description");
    supervisoryNode.setParent(supervisoryNodeParent);

    supervisoryNodeMapper.update(supervisoryNode);

    SupervisoryNode resultSupervisoryNode = supervisoryNodeMapper.getSupervisoryNode(supervisoryNode.getId());
    assertThat(resultSupervisoryNode, is(notNullValue()));
    assertThat(resultSupervisoryNode.getCode(), is("updated code"));
    assertThat(resultSupervisoryNode.getName(), is("updated name"));
    assertThat(resultSupervisoryNode.getDescription(), is("updated description"));
    assertThat(resultSupervisoryNode.getParent().getId(), is(supervisoryNodeParent.getId()));
  }

  @Test
  public void shouldGetSupervisoryNodeIdByCode() throws Exception {
    supervisoryNodeMapper.insert(supervisoryNode);

    Long fetchedId = supervisoryNodeMapper.getIdForCode(supervisoryNode.getCode());

    assertThat(fetchedId, is(supervisoryNode.getId()));
  }

  @Test
  public void shouldGetSupervisoryNodeByCode() throws Exception {
    supervisoryNodeMapper.insert(supervisoryNode);

    SupervisoryNode result = supervisoryNodeMapper.getByCode(supervisoryNode);

    assertThat(result, is(supervisoryNode));
  }

  @Test
  public void shouldGetAllSupervisoryNodesInTheHierarchyForAUserAndProgramWithAppropriateRight() {
    Program program1 = insertProgram(make(a(defaultProgram, with(programCode, "p1"))));
    Program program2 = insertProgram(make(a(defaultProgram, with(programCode, "p2"))));

    User user = insertUser();

    Role createRole = new Role("create role", "random description");
    roleRightsMapper.insertRole(createRole);

    Role configureRnrRole = new Role("configure rnr", "random description");
    roleRightsMapper.insertRole(configureRnrRole);

    roleRightsMapper.createRoleRight(createRole, CREATE_REQUISITION);
    roleRightsMapper.createRoleRight(configureRnrRole, CONFIGURE_RNR);

    supervisoryNodeMapper.insert(supervisoryNode);

    SupervisoryNode supervisoryNode1 = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode, with(code, "SN1")));
    supervisoryNode1.setFacility(facility);
    supervisoryNode1 = insertSupervisoryNode(supervisoryNode1);

    SupervisoryNode childNode = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode, with(code, "CN1")));
    childNode.setFacility(facility);
    childNode.setParent(supervisoryNode);
    childNode = insertSupervisoryNode(childNode);

    insertRoleAssignments(program1, user, createRole, supervisoryNode);
    insertRoleAssignments(program1, user, configureRnrRole, supervisoryNode1);
    insertRoleAssignments(program2, user, createRole, supervisoryNode);
    insertRoleAssignments(program1, user, createRole, null);

    List<SupervisoryNode> userSupervisoryNodes = supervisoryNodeMapper.getAllSupervisoryNodesInHierarchyBy(user.getId(), program1.getId(), "{CREATE_REQUISITION}");

    assertThat(userSupervisoryNodes.size(), is(2));
    assertTrue(userSupervisoryNodes.contains(supervisoryNode));
    assertTrue(userSupervisoryNodes.contains(childNode));
  }

  @Test
  public void shouldGetAllSupervisoryNodesInTheHierarchyForAUserWithAppropriateRight() {
    Program program1 = insertProgram(make(a(defaultProgram, with(programCode, "p1"))));
    Program program2 = insertProgram(make(a(defaultProgram, with(programCode, "p2"))));

    User user = insertUser();

    Role createRole = new Role("create role", "random description");
    roleRightsMapper.insertRole(createRole);

    Role configureRnrRole = new Role("configure rnr", "random description");
    roleRightsMapper.insertRole(configureRnrRole);

    roleRightsMapper.createRoleRight(createRole, CREATE_REQUISITION);
    roleRightsMapper.createRoleRight(configureRnrRole, CONFIGURE_RNR);

    supervisoryNodeMapper.insert(supervisoryNode);

    SupervisoryNode supervisoryNode1 = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode, with(code, "SN1")));
    supervisoryNode1.setFacility(facility);
    supervisoryNode1 = insertSupervisoryNode(supervisoryNode1);

    SupervisoryNode childNode = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode, with(code, "CN1")));
    childNode.setFacility(facility);
    childNode.setParent(supervisoryNode);
    childNode = insertSupervisoryNode(childNode);

    insertRoleAssignments(program1, user, createRole, supervisoryNode);
    insertRoleAssignments(program1, user, configureRnrRole, supervisoryNode1);
    insertRoleAssignments(program2, user, createRole, supervisoryNode);
    insertRoleAssignments(program1, user, createRole, null);

    List<SupervisoryNode> userSupervisoryNodes = supervisoryNodeMapper.getAllSupervisoryNodesInHierarchyByUserAndRights(user.getId(), "{CREATE_REQUISITION}");

    assertThat(userSupervisoryNodes.size(), is(2));
    assertTrue(userSupervisoryNodes.contains(supervisoryNode));
    assertTrue(userSupervisoryNodes.contains(childNode));
  }

  @Test
  public void shouldGetSupervisoryNodeForRG() {
    supervisoryNodeMapper.insert(supervisoryNode);
    RequisitionGroup requisitionGroup = make(a(defaultRequisitionGroup));
    requisitionGroup.setSupervisoryNode(supervisoryNode);
    requisitionGroupMapper.insert(requisitionGroup);

    SupervisoryNode actualSupervisoryNode = supervisoryNodeMapper.getFor(requisitionGroup.getCode());

    assertThat(actualSupervisoryNode, is(supervisoryNode));
  }

  @Test
  public void shouldGetParentForAGiveSupervisoryNode() throws Exception {
    SupervisoryNode parentNode = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode, with(code, "SN1")));
    parentNode.setFacility(facility);
    insertSupervisoryNode(parentNode);

    SupervisoryNode childNode = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode, with(code, "CN1")));
    childNode.setFacility(facility);
    childNode.setParent(parentNode);
    insertSupervisoryNode(childNode);

    final SupervisoryNode parent = supervisoryNodeMapper.getParent(childNode.getId());

    assertThat(parent, is(parentNode));
  }

  @Test
  public void shouldGetAllSupervisoryNodes() throws Exception {
    supervisoryNodeMapper.insert(supervisoryNode);
    List<SupervisoryNode> fetchedSupervisoryNodes = supervisoryNodeMapper.getAll();

    assertThat(fetchedSupervisoryNodes.size(), is(1));
    assertThat(fetchedSupervisoryNodes.get(0).getCode(), is("N1"));
  }

  @Test
  public void shouldGetAllSupervisoryNodesInHierarchy() throws Exception {
    SupervisoryNode supervisoryNode1 = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode, with(code, "SN1")));
    supervisoryNode1.setFacility(facility);
    insertSupervisoryNode(supervisoryNode1);

    SupervisoryNode supervisoryNode2 = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode, with(code, "CN1")));
    supervisoryNode2.setFacility(facility);
    supervisoryNode2.setParent(supervisoryNode1);
    insertSupervisoryNode(supervisoryNode2);

    SupervisoryNode supervisoryNode3 = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode, with(code, "CN2")));
    supervisoryNode3.setFacility(facility);
    supervisoryNode3.setParent(supervisoryNode2);
    insertSupervisoryNode(supervisoryNode3);

    List<SupervisoryNode> result = supervisoryNodeMapper.getAllParentSupervisoryNodesInHierarchy(supervisoryNode3);
    assertThat(result.size(), is(3));
    assertThat(result.get(0).getId(), is(supervisoryNode3.getId()));
    assertThat(result.get(1).getId(), is(supervisoryNode2.getId()));
    assertThat(result.get(2).getId(), is(supervisoryNode1.getId()));
  }

  @Test
  public void shouldGetPaginatedSupervisoryNodesByNameSearch() {
    insertSupervisoryNode(supervisoryNode);

    SupervisoryNode supervisoryNode1 = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode, with(code, "SN1"), with(name, "Approval Point 2")));
    supervisoryNode1.setFacility(facility);
    insertSupervisoryNode(supervisoryNode1);

    SupervisoryNode supervisoryNode2 = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode, with(code, "CN1"), with(name, "Not Matching Search")));
    supervisoryNode2.setFacility(facility);
    insertSupervisoryNode(supervisoryNode2);

    Pagination pagination = new Pagination(1, 10);
    List<SupervisoryNode> searchResults = supervisoryNodeMapper.getSupervisoryNodesBy("Approval", pagination);

    assertThat(searchResults.size(), is(2));
  }

  @Test
  public void shouldGetPaginatedSupervisoryNodesByParentNameSearch() {
    SupervisoryNode supervisoryNode1 = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode, with(code, "SN1"), with(name, "Parent")));
    supervisoryNode1.setFacility(facility);
    insertSupervisoryNode(supervisoryNode1);

    supervisoryNode.setParent(supervisoryNode1);
    insertSupervisoryNode(supervisoryNode);

    SupervisoryNode supervisoryNode2 = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode, with(code, "CN1"), with(name, "Another")));
    supervisoryNode2.setFacility(facility);
    insertSupervisoryNode(supervisoryNode2);

    SupervisoryNode supervisoryNode3 = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode, with(code, "CN2"), with(name, "Child with not matching")));
    supervisoryNode3.setFacility(facility);
    supervisoryNode3.setParent(supervisoryNode2);
    insertSupervisoryNode(supervisoryNode3);

    Pagination pagination = new Pagination(1, 10);
    List<SupervisoryNode> searchResults = supervisoryNodeMapper.getSupervisoryNodesBy("Parent", pagination);

    assertThat(searchResults.size(), is(1));
  }

  @Test
  public void shouldGetSupervisoryNodesCountByNameSearch() {
    insertSupervisoryNode(supervisoryNode);

    SupervisoryNode supervisoryNode1 = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode, with(code, "SN1"), with(name, "Approval Point 2")));
    supervisoryNode1.setFacility(facility);
    insertSupervisoryNode(supervisoryNode1);

    SupervisoryNode supervisoryNode2 = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode, with(code, "CN1"), with(name, "Not Matching Search")));
    supervisoryNode2.setFacility(facility);
    insertSupervisoryNode(supervisoryNode2);

    Integer resultCount = supervisoryNodeMapper.getTotalSearchResultCount("Approval");

    assertThat(resultCount, is(2));
  }

  @Test
  public void shouldGetSupervisoryNodesCountByParentNameSearch() {
    SupervisoryNode supervisoryNode1 = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode, with(code, "SN1"), with(name, "Parent")));
    supervisoryNode1.setFacility(facility);
    insertSupervisoryNode(supervisoryNode1);

    supervisoryNode.setParent(supervisoryNode1);
    insertSupervisoryNode(supervisoryNode);

    SupervisoryNode supervisoryNode2 = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode, with(code, "CN1"), with(name, "Another")));
    supervisoryNode2.setFacility(facility);
    insertSupervisoryNode(supervisoryNode2);

    SupervisoryNode supervisoryNode3 = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode, with(code, "CN2"), with(name, "Child with not matching")));
    supervisoryNode3.setFacility(facility);
    supervisoryNode3.setParent(supervisoryNode2);
    insertSupervisoryNode(supervisoryNode3);

    Integer resultCount = supervisoryNodeMapper.getTotalParentSearchResultCount("Parent");

    assertThat(resultCount, is(1));

  }

  @Test
  public void shouldGetSupervisoryNodeWithParentAndAssociatedFacility() {
    SupervisoryNode supervisoryNode1 = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode, with(code, "SN1"), with(name, "Parent")));
    supervisoryNode1.setFacility(facility);
    insertSupervisoryNode(supervisoryNode1);

    supervisoryNode.setParent(supervisoryNode1);
    insertSupervisoryNode(supervisoryNode);

    SupervisoryNode savedNode = supervisoryNodeMapper.getSupervisoryNode(supervisoryNode.getId());

    assertThat(savedNode.getParent(), is(supervisoryNode1));
  }

  @Test
  public void shouldFilterSupervisoryNodesByName() {
    SupervisoryNode supervisoryNode1 = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode, with(code, "SN1"), with(name, "gillAge Dispensary")));
    supervisoryNode1.setFacility(facility);
    insertSupervisoryNode(supervisoryNode1);

    SupervisoryNode supervisoryNode2 = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode, with(code, "SN2"), with(name, "Village 2 Dispensary")));
    supervisoryNode2.setFacility(facility);
    insertSupervisoryNode(supervisoryNode2);

    SupervisoryNode supervisoryNode3 = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode, with(code, "SN3"), with(name, "City Dispensary")));
    supervisoryNode3.setFacility(facility);
    insertSupervisoryNode(supervisoryNode3);

    SupervisoryNode supervisoryNode4 = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode, with(code, "SN4"), with(name, "Village 1 Dispensary")));
    supervisoryNode4.setFacility(facility);
    insertSupervisoryNode(supervisoryNode4);

    SupervisoryNode supervisoryNode5 = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode, with(code, "SN5"), with(name, "Central Hospital")));
    supervisoryNode5.setFacility(facility);
    insertSupervisoryNode(supervisoryNode5);

    String param = "age";

    List<SupervisoryNode> supervisoryNodeList = supervisoryNodeMapper.getFilteredSupervisoryNodesByName(param);

    assertThat(supervisoryNodeList.size(), is(3));
    assertThat(supervisoryNodeList.get(0), is(supervisoryNode1));
    assertThat(supervisoryNodeList.get(1), is(supervisoryNode4));
    assertThat(supervisoryNodeList.get(2), is(supervisoryNode2));
  }

  @Test
  public void shouldSearchTopLevelSupervisoryNodesByName() {
    SupervisoryNode supervisoryNode1 = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode, with(code, "SN1"), with(name, "gillAge Dispensary")));
    supervisoryNode1.setFacility(facility);
    insertSupervisoryNode(supervisoryNode1);

    SupervisoryNode supervisoryNode2 = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode, with(code, "SN2"), with(name, "Village 2 Dispensary")));
    supervisoryNode2.setFacility(facility);
    insertSupervisoryNode(supervisoryNode2);

    SupervisoryNode supervisoryNode3 = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode, with(code, "SN3"), with(name, "City Dispensary")));
    supervisoryNode3.setFacility(facility);
    supervisoryNode3.setParent(supervisoryNode1);
    insertSupervisoryNode(supervisoryNode3);

    SupervisoryNode supervisoryNode4 = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode, with(code, "SN4"), with(name, "Village 1 Dispensary")));
    supervisoryNode4.setFacility(facility);
    insertSupervisoryNode(supervisoryNode4);

    SupervisoryNode supervisoryNode5 = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode, with(code, "SN5"), with(name, "Central Hospital")));
    supervisoryNode5.setFacility(facility);
    supervisoryNode5.setParent(supervisoryNode1);
    insertSupervisoryNode(supervisoryNode5);

    String param = "age";

    List<SupervisoryNode> supervisoryNodeList = supervisoryNodeMapper.searchTopLevelSupervisoryNodesByName(param);

    assertThat(supervisoryNodeList.size(), is(3));
    assertThat(supervisoryNodeList.get(0), is(supervisoryNode1));
    assertThat(supervisoryNodeList.get(1), is(supervisoryNode4));
    assertThat(supervisoryNodeList.get(2), is(supervisoryNode2));
  }

  private SupervisoryNode insertSupervisoryNode(SupervisoryNode supervisoryNode) {
    supervisoryNodeMapper.insert(supervisoryNode);
    return supervisoryNode;
  }

  private Program insertProgram(Program program) {
    programMapper.insert(program);
    return program;
  }

  private Role insertRoleAssignments(Program program, User user, Role role, SupervisoryNode supervisoryNode) {
    Long supervisoryNodeId = supervisoryNode == null ? null : supervisoryNode.getId();
    roleAssignmentMapper.insertRoleAssignment(user.getId(), program.getId(), supervisoryNodeId, role.getId());
    return role;
  }

  private User insertUser() {
    User user = make(a(defaultUser, with(facilityId, facility.getId())));
    userMapper.insert(user);
    return user;
  }
}
