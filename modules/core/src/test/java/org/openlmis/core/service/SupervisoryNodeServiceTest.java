package org.openlmis.core.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations.Mock;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.domain.User;
import org.openlmis.core.repository.SupervisoryNodeRepository;
import org.openlmis.core.repository.UserRepository;

import java.util.ArrayList;
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
    when(supervisoryNodeRepository.getFor(1, 1)).thenReturn(expectedNode);
    final SupervisoryNode result = supervisoryNodeService.getFor(1, 1);
    verify(supervisoryNodeRepository).getFor(1, 1);
    assertThat(result, is(expectedNode));
  }

  @Test
  public void shouldGetApproverForProgramAndFacility() throws Exception {
    final Integer supervisoryNodeId = 1;
    final Integer facilityId = 1;
    final Integer programId = 1;
    final User user = new User();

    SupervisoryNode supervisoryNode = new SupervisoryNode();
    supervisoryNode.setId(supervisoryNodeId);

    when(supervisoryNodeRepository.getFor(facilityId, programId)).thenReturn(supervisoryNode);
    List<User> roleAssignments = new ArrayList<User>() {{
      add(user);
    }};
    when(userRepository.getUsersWithRightInNodeForProgram(programId, supervisoryNodeId, Right.APPROVE_REQUISITION)).thenReturn(roleAssignments);


    User result = supervisoryNodeService.getApproverFor(facilityId, programId);

    verify(supervisoryNodeRepository).getFor(facilityId, programId);
    assertThat(result, is(user));
  }

  @Test
  public void shouldGetApproverFromParentSupervisoryNodeIfNotFoundInImmediateSupervisoryNode() throws Exception {
    int facilityId = 1;
    final int programId = 2;
    int parentNodeId = 4;
    int nodeId = 1;
    final User approver = new User();
    List<User> users = new ArrayList<User>() {{
      add(approver);
    }};
    SupervisoryNode node = new SupervisoryNode();
    node.setId(nodeId);
    when(supervisoryNodeRepository.getFor(facilityId, programId)).thenReturn(node);
    when(userRepository.getUsersWithRightInNodeForProgram(programId, nodeId, APPROVE_REQUISITION)).thenReturn(new ArrayList<User>());
    when(supervisoryNodeRepository.getSupervisoryNodeParentId(nodeId)).thenReturn(parentNodeId);
    when(userRepository.getUsersWithRightInNodeForProgram(programId, parentNodeId, APPROVE_REQUISITION)).thenReturn(users);

    User user = supervisoryNodeService.getApproverFor(facilityId, programId);

    verify(supervisoryNodeRepository).getFor(facilityId, programId);
    verify(supervisoryNodeRepository).getSupervisoryNodeParentId(nodeId);
    assertThat(user, is(approver));
  }

  @Test
  public void shouldReturnNullIfNoApproverFound() throws Exception {
    int facilityId = 1;
    final int programId = 2;
    int nodeId = 1;
    SupervisoryNode node = new SupervisoryNode();
    node.setId(nodeId);
    when(supervisoryNodeRepository.getFor(facilityId, programId)).thenReturn(node);
    when(userRepository.getUsersWithRightInNodeForProgram(programId, nodeId, APPROVE_REQUISITION)).thenReturn(new ArrayList<User>());
    when(supervisoryNodeRepository.getSupervisoryNodeParentId(nodeId)).thenReturn(null);

    User user = supervisoryNodeService.getApproverFor(facilityId, programId);

    verify(supervisoryNodeRepository).getFor(facilityId, programId);
    verify(supervisoryNodeRepository).getSupervisoryNodeParentId(nodeId);
    assertThat(user, is(nullValue()));
  }

  @Test
  public void shouldReturnNullIfNoSupervisoryNodeFoundForFacilityAndProgram() throws Exception {
    int facilityId = 1;
    final int programId = 2;
    when(supervisoryNodeRepository.getFor(facilityId, programId)).thenReturn(null);
    User user = supervisoryNodeService.getApproverFor(facilityId, programId);

    verify(supervisoryNodeRepository).getFor(facilityId, programId);
    assertThat(user, is(nullValue()));
  }
}
