package org.openlmis.core.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.domain.User;
import org.openlmis.core.repository.SupervisoryNodeRepository;
import org.openlmis.core.repository.UserRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
  SupervisoryNodeService supervisoryNodeService;

  @Mock
  private UserRepository userRepository;


  @Before
  public void setUp() throws Exception {
    initMocks(this);
    supervisoryNodeService = new SupervisoryNodeService(supervisoryNodeRepository, userRepository);
  }

  @Test
  public void shouldSaveSupervisoryNode() throws Exception {
    SupervisoryNode supervisoryNode = new SupervisoryNode();
    supervisoryNodeService.save(supervisoryNode);

    verify(supervisoryNodeRepository).save(supervisoryNode);
  }

  @Test
  public void shouldGetUserSupervisedNodesInHierarchyByUserAndProgram() {
    Integer userId = 1;
    Integer programId = 1;
    supervisoryNodeService.getAllSupervisoryNodesInHierarchyBy(userId, programId, CREATE_REQUISITION);
    verify(supervisoryNodeRepository).getAllSupervisoryNodesInHierarchyBy(userId, programId, CREATE_REQUISITION);
  }

  @Test
  public void shouldGetSupervisoryNodeForProgramAndFacility() throws Exception {
    SupervisoryNode expectedNode = new SupervisoryNode();
    Facility facility = new Facility(1);
    Program program = new Program(1);
    when(supervisoryNodeRepository.getFor(facility, program)).thenReturn(expectedNode);
    final SupervisoryNode result = supervisoryNodeService.getFor(facility, program);
    verify(supervisoryNodeRepository).getFor(facility, program);
    assertThat(result, is(expectedNode));
  }

  @Test
  public void shouldGetApproverForProgramAndFacility() throws Exception {
    Integer supervisoryNodeId = 1;
    Facility facility = new Facility(1);
    Program program = new Program(1);
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
    Facility facility = new Facility(1);
    Program program = new Program(2);
    SupervisoryNode parentNode = new SupervisoryNode(4);
    int nodeId = 1;
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
    Facility facility = new Facility(1);
    Program program = new Program(2);
    int nodeId = 1;
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
    Facility facility = new Facility(1);
    Program program = new Program(2);
    when(supervisoryNodeRepository.getFor(facility, program)).thenReturn(null);
    User user = supervisoryNodeService.getApproverFor(facility, program);

    verify(supervisoryNodeRepository).getFor(facility, program);
    assertThat(user, is(nullValue()));
  }

  @Test
  public void shouldGetParentSupervisoryNodeForGivenSupervisoryNode() throws Exception {
    SupervisoryNode expected = new SupervisoryNode();
    when(supervisoryNodeRepository.getParent(1)).thenReturn(expected);
    final SupervisoryNode actual = supervisoryNodeService.getParent(1);
    verify(supervisoryNodeRepository).getParent(1);
    assertThat(actual, is(expected));
  }

  @Test
  public void shouldGetSupervisorForGivenSupervisoryNodeAndProgram() throws Exception {
    SupervisoryNode supervisoryNode = new SupervisoryNode(1);
    Program program = new Program(2);
    final User approver = new User();
    List<User> listOfUsers = Arrays.asList(approver);
    when(userRepository.getUsersWithRightInNodeForProgram(program, supervisoryNode, APPROVE_REQUISITION)).thenReturn(listOfUsers);

    User actual = supervisoryNodeService.getApproverForGivenSupervisoryNodeAndProgram(supervisoryNode, program);

    verify(userRepository).getUsersWithRightInNodeForProgram(program, supervisoryNode, APPROVE_REQUISITION);
    assertThat(actual, is(approver));

  }

  @Test
  public void shouldReturnNullIfNoSupervisorIsAssignedToGivenSupervisoryNodeAndProgram() throws Exception {
    SupervisoryNode supervisoryNode = new SupervisoryNode(1);
    Program program = new Program(1);

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
}
