/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.builder.SupervisoryNodeBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.FacilityRepository;
import org.openlmis.core.repository.SupervisoryNodeRepository;
import org.openlmis.core.repository.UserRepository;
import org.openlmis.db.categories.UnitTests;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.core.domain.RightName.APPROVE_REQUISITION;
import static org.openlmis.core.domain.RightName.CREATE_REQUISITION;
import static org.openlmis.core.matchers.Matchers.dataExceptionMatcher;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@Category(UnitTests.class)
@PrepareForTest(SupervisoryNodeService.class)
public class SupervisoryNodeServiceTest {

  @Mock
  SupervisoryNodeRepository supervisoryNodeRepository;

  @Mock
  private FacilityRepository facilityRepository;

  @Mock
  private UserRepository userRepository;

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @InjectMocks
  SupervisoryNodeService supervisoryNodeService;

  private SupervisoryNode supervisoryNodeWithParent;

  private Integer pageSize = 100;

  @Before
  public void setUp() throws Exception {
    supervisoryNodeService.setPageSize(String.valueOf(pageSize));
    supervisoryNodeWithParent = new SupervisoryNode();
    supervisoryNodeWithParent.setId(10L);
    supervisoryNodeWithParent.setFacility(new Facility());
    SupervisoryNode parent = new SupervisoryNode();
    parent.setCode("PSN");
    parent.setId(20L);
    supervisoryNodeWithParent.setParent(parent);

  }

  @Test
  public void shouldGetUserSupervisedNodesInHierarchyByUserAndProgram() {
    Long userId = 1L;
    Long programId = 1L;
    supervisoryNodeService.getAllSupervisoryNodesInHierarchyBy(userId, programId, CREATE_REQUISITION);
    verify(supervisoryNodeRepository).getAllSupervisoryNodesInHierarchyBy(userId, programId, CREATE_REQUISITION);
  }

  @Test
  public void shouldGetSupervisoryNodeForProgramAndFacility() throws Exception {
    SupervisoryNode expectedNode = new SupervisoryNode();
    Facility facility = new Facility(1L);
    Program program = new Program(1L);
    when(supervisoryNodeRepository.getFor(facility, program)).thenReturn(expectedNode);
    final SupervisoryNode result = supervisoryNodeService.getFor(facility, program);
    verify(supervisoryNodeRepository).getFor(facility, program);
    assertThat(result, is(expectedNode));
  }

  @Test
  public void shouldGetApproverForProgramAndFacility() throws Exception {
    Long supervisoryNodeId = 1L;
    Facility facility = new Facility(1L);
    Program program = new Program(1L);
    User user = new User();

    SupervisoryNode supervisoryNode = new SupervisoryNode();
    supervisoryNode.setId(supervisoryNodeId);

    when(supervisoryNodeRepository.getFor(facility, program)).thenReturn(supervisoryNode);
    List<User> roleAssignments = Arrays.asList(user);
    when(userRepository.getUsersWithRightInNodeForProgram(program, supervisoryNode, APPROVE_REQUISITION)).thenReturn(roleAssignments);

    User result = supervisoryNodeService.getApproverFor(facility, program);

    verify(supervisoryNodeRepository).getFor(facility, program);
    assertThat(result, is(user));
  }

  @Test
  public void shouldGetApproverFromParentSupervisoryNodeIfNotFoundInImmediateSupervisoryNode() throws Exception {
    Facility facility = new Facility(1L);
    Program program = new Program(2L);
    SupervisoryNode parentNode = new SupervisoryNode(4L);
    Long nodeId = 1L;
    final User approver = new User();
    List<User> users = Arrays.asList(approver);
    SupervisoryNode node = new SupervisoryNode(nodeId);
    when(supervisoryNodeRepository.getFor(facility, program)).thenReturn(node);
    when(userRepository.getUsersWithRightInNodeForProgram(program, node, APPROVE_REQUISITION)).thenReturn(new ArrayList<User>());
    when(supervisoryNodeRepository.getSupervisoryNodeParentId(node.getId())).thenReturn(parentNode.getId());
    when(userRepository.getUsersWithRightInNodeForProgram(program, parentNode, APPROVE_REQUISITION)).thenReturn(users);

    User user = supervisoryNodeService.getApproverFor(facility, program);

    verify(supervisoryNodeRepository).getFor(facility, program);
    verify(supervisoryNodeRepository).getSupervisoryNodeParentId(nodeId);
    assertThat(user, is(approver));
  }

  @Test
  public void shouldReturnNullIfNoApproverFound() throws Exception {
    Facility facility = new Facility(1L);
    Program program = new Program(2L);
    Long nodeId = 1L;
    SupervisoryNode node = new SupervisoryNode(nodeId);
    when(supervisoryNodeRepository.getFor(facility, program)).thenReturn(node);
    when(userRepository.getUsersWithRightInNodeForProgram(program, node, APPROVE_REQUISITION)).thenReturn(new ArrayList<User>());
    when(supervisoryNodeRepository.getSupervisoryNodeParentId(nodeId)).thenReturn(null);

    User user = supervisoryNodeService.getApproverFor(facility, program);

    verify(supervisoryNodeRepository).getFor(facility, program);
    verify(supervisoryNodeRepository).getSupervisoryNodeParentId(nodeId);
    assertThat(user, is(nullValue()));
  }

  @Test
  public void shouldReturnNullIfNoSupervisoryNodeFoundForFacilityAndProgram() throws Exception {
    Facility facility = new Facility(1L);
    Program program = new Program(2L);
    when(supervisoryNodeRepository.getFor(facility, program)).thenReturn(null);
    User user = supervisoryNodeService.getApproverFor(facility, program);

    verify(supervisoryNodeRepository).getFor(facility, program);
    assertThat(user, is(nullValue()));
  }

  @Test
  public void shouldGetParentSupervisoryNodeForGivenSupervisoryNode() throws Exception {
    SupervisoryNode expected = new SupervisoryNode();
    when(supervisoryNodeRepository.getParent(1L)).thenReturn(expected);
    final SupervisoryNode actual = supervisoryNodeService.getParent(1L);
    verify(supervisoryNodeRepository).getParent(1L);
    assertThat(actual, is(expected));
  }

  @Test
  public void shouldGetSupervisorForGivenSupervisoryNodeAndProgram() throws Exception {
    SupervisoryNode supervisoryNode = new SupervisoryNode(1L);
    Program program = new Program(2L);
    final User approver = new User();
    List<User> listOfUsers = Arrays.asList(approver);
    when(userRepository.getUsersWithRightInNodeForProgram(program, supervisoryNode, APPROVE_REQUISITION)).thenReturn(listOfUsers);

    User actual = supervisoryNodeService.getApproverForGivenSupervisoryNodeAndProgram(supervisoryNode, program);

    verify(userRepository).getUsersWithRightInNodeForProgram(program, supervisoryNode, APPROVE_REQUISITION);
    assertThat(actual, is(approver));

  }

  @Test
  public void shouldReturnNullIfNoSupervisorIsAssignedToGivenSupervisoryNodeAndProgram() throws Exception {
    SupervisoryNode supervisoryNode = new SupervisoryNode(1L);
    Program program = new Program(1L);

    List<User> listOfUsers = new ArrayList<>();
    when(userRepository.getUsersWithRightInNodeForProgram(program, supervisoryNode, APPROVE_REQUISITION)).thenReturn(listOfUsers);

    User actual = supervisoryNodeService.getApproverForGivenSupervisoryNodeAndProgram(supervisoryNode, program);

    verify(userRepository).getUsersWithRightInNodeForProgram(program, supervisoryNode, APPROVE_REQUISITION);
    assertThat(actual, is(nullValue()));

  }

  @Test
  public void shouldGetAllSupervisoryNodes() throws Exception {
    List<SupervisoryNode> expected = new ArrayList<>();
    when(supervisoryNodeRepository.getAll()).thenReturn(expected);

    List<SupervisoryNode> actual = supervisoryNodeService.getAll();

    verify(supervisoryNodeRepository).getAll();
    assertThat(actual, is(expected));
  }

  @Test
  public void shouldGetAllParentSupervisoryNodesInHierarchy() throws Exception {
    List<SupervisoryNode> expected = new ArrayList<>();
    SupervisoryNode supervisoryNode = new SupervisoryNode(1L);
    when(supervisoryNodeRepository.getAllParentSupervisoryNodesInHierarchy(supervisoryNode)).thenReturn(expected);

    List<SupervisoryNode> actual = supervisoryNodeService.getAllParentSupervisoryNodesInHierarchy(supervisoryNode);

    verify(supervisoryNodeRepository).getAllParentSupervisoryNodesInHierarchy(supervisoryNode);
    assertThat(actual, is(expected));
  }

  @Test
  public void shouldSaveSupervisoryNode() throws Exception {
    SupervisoryNode supervisoryNode = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode));

    supervisoryNodeService.save(supervisoryNode);

    verify(supervisoryNodeRepository).insert(supervisoryNode);
  }

  @Test
  public void shouldUpdateExistingSupervisoryNode() throws Exception {
    SupervisoryNode supervisoryNode = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode));
    supervisoryNode.setId(1L);

    supervisoryNodeService.save(supervisoryNode);

    verify(supervisoryNodeRepository).update(supervisoryNode);
  }

  @Test
  public void shouldGiveErrorIfParentNodeCodeDoesNotExist() throws Exception {
    when(supervisoryNodeRepository.getIdForCode(supervisoryNodeWithParent.getParent().getCode())).thenThrow(new DataException("Invalid SupervisoryNode Code"));

    expectedEx.expect(dataExceptionMatcher("error.supervisory.node.parent.not.exist"));

    supervisoryNodeService.save(supervisoryNodeWithParent);

    verify(supervisoryNodeRepository).getIdForCode(supervisoryNodeWithParent.getParent().getCode());
  }

  @Test
  public void shouldGiveErrorIfFacilityCodeDoesNotExist() throws Exception {
    when(supervisoryNodeRepository.getIdForCode(supervisoryNodeWithParent.getParent().getCode())).thenReturn(1L);
    when(facilityRepository.getIdForCode(supervisoryNodeWithParent.getFacility().getCode())).thenThrow(new DataException("error.facility.code.invalid"));

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("error.facility.code.invalid");

    supervisoryNodeService.save(supervisoryNodeWithParent);

    verify(facilityRepository).getIdForCode(supervisoryNodeWithParent.getFacility().getCode());
    verify(supervisoryNodeRepository).getIdForCode(supervisoryNodeWithParent.getParent().getCode());
  }

  @Test
  public void shouldGetSupervisoryNodesForParentSearchCriteria() throws Exception {
    String searchCriteria = "parentName";
    int page = 10;
    Pagination pagination = new Pagination(0, 0);
    whenNew(Pagination.class).withArguments(page, pageSize).thenReturn(pagination);
    when(supervisoryNodeRepository.getSupervisoryNodesByParent(pagination, searchCriteria)).thenReturn(Collections.EMPTY_LIST);
    List<SupervisoryNode> searchResult = supervisoryNodeService.getSupervisoryNodesBy(page, searchCriteria, true);
    verify(supervisoryNodeRepository).getSupervisoryNodesByParent(pagination, searchCriteria);
    assertThat(searchResult, is(Collections.EMPTY_LIST));
  }

  @Test
  public void shouldGetSupervisoryNodesSearchCriteria() throws Exception {
    String searchCriteria = "nodeName";
    int page = 10;
    Pagination pagination = new Pagination(0, 0);
    whenNew(Pagination.class).withArguments(page, pageSize).thenReturn(pagination);

    when(supervisoryNodeRepository.getSupervisoryNodesBy(pagination, searchCriteria)).thenReturn(Collections.EMPTY_LIST);
    List<SupervisoryNode> searchResult = supervisoryNodeService.getSupervisoryNodesBy(page, searchCriteria, true);
    verify(supervisoryNodeRepository).getSupervisoryNodesByParent(pagination, searchCriteria);
    assertThat(searchResult, is(Collections.EMPTY_LIST));
  }

}
