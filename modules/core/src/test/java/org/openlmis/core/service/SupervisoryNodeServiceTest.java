/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.openlmis.core.builder.SupervisoryNodeBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.domain.User;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.FacilityRepository;
import org.openlmis.core.repository.SupervisoryNodeRepository;
import org.openlmis.core.repository.UserRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.core.domain.Right.APPROVE_REQUISITION;
import static org.openlmis.core.domain.Right.CREATE_REQUISITION;

public class SupervisoryNodeServiceTest {

  @Mock
  SupervisoryNodeRepository supervisoryNodeRepository;
  @Mock
  private FacilityRepository facilityRepository;

  SupervisoryNodeService supervisoryNodeService;
  @Mock
  private UserRepository userRepository;
  @Rule
  public ExpectedException expectedEx = ExpectedException.none();
  SupervisoryNode supervisoryNodeWithParent;


  @Before
  public void setUp() throws Exception {
    initMocks(this);
    supervisoryNodeWithParent = new SupervisoryNode();
    supervisoryNodeWithParent.setId(10L);
    supervisoryNodeWithParent.setFacility(new Facility());
    SupervisoryNode parent = new SupervisoryNode();
    parent.setCode("PSN");
    parent.setId(20L);
    supervisoryNodeWithParent.setParent(parent);

    supervisoryNodeService = new SupervisoryNodeService(supervisoryNodeRepository, userRepository, facilityRepository);
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

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Supervisory Node Parent does not exist");

    supervisoryNodeService.save(supervisoryNodeWithParent);

    verify(supervisoryNodeRepository).getIdForCode(supervisoryNodeWithParent.getParent().getCode());
  }

  @Test
  public void shouldGiveErrorIfFacilityCodeDoesNotExist() throws Exception {
    when(supervisoryNodeRepository.getIdForCode(supervisoryNodeWithParent.getParent().getCode())).thenReturn(1L);
    when(facilityRepository.getIdForCode(supervisoryNodeWithParent.getFacility().getCode())).thenThrow(new DataException("Invalid Facility Code"));

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Invalid Facility Code");

    supervisoryNodeService.save(supervisoryNodeWithParent);

    verify(facilityRepository).getIdForCode(supervisoryNodeWithParent.getFacility().getCode());
    verify(supervisoryNodeRepository).getIdForCode(supervisoryNodeWithParent.getParent().getCode());
  }


}
