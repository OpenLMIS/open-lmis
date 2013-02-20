package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.*;
import org.openlmis.core.repository.SupervisoryNodeRepository;
import org.openlmis.core.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.openlmis.core.domain.Right.APPROVE_REQUISITION;

@Service
@NoArgsConstructor
public class SupervisoryNodeService {
  private SupervisoryNodeRepository supervisoryNodeRepository;
  private UserRepository userRepository;


  @Autowired
  public SupervisoryNodeService(SupervisoryNodeRepository supervisoryNodeRepository, UserRepository userRepository) {
    this.supervisoryNodeRepository = supervisoryNodeRepository;
    this.userRepository = userRepository;
  }

  public void save(SupervisoryNode supervisoryNode) {
    supervisoryNodeRepository.save(supervisoryNode);
  }

  public List<SupervisoryNode> getAllSupervisoryNodesInHierarchyBy(Integer userId, Integer programId, Right... rights) {
    return supervisoryNodeRepository.getAllSupervisoryNodesInHierarchyBy(userId, programId, rights);
  }

  public List<SupervisoryNode> getAllSupervisoryNodesInHierarchyBy(Integer userId, Right... rights) {
    return supervisoryNodeRepository.getAllSupervisoryNodesInHierarchyBy(userId, rights);
  }

  public SupervisoryNode getFor(Facility facility, Program program) {
    return supervisoryNodeRepository.getFor(facility, program);
  }

  public User getApproverFor(Facility facility, Program program) {
    SupervisoryNode supervisoryNode = supervisoryNodeRepository.getFor(facility, program);
    if (supervisoryNode == null) return null;

    List<User> users;
    while ((users = userRepository.getUsersWithRightInNodeForProgram(program, supervisoryNode, APPROVE_REQUISITION)).size() == 0) {
      Integer supervisoryNodeId = supervisoryNodeRepository.getSupervisoryNodeParentId(supervisoryNode.getId());
      if (supervisoryNodeId == null) return null;
      supervisoryNode = new SupervisoryNode(supervisoryNodeId);
    }

    return users.get(0);
  }


  public SupervisoryNode getParent(Integer id) {
    return supervisoryNodeRepository.getParent(id);
  }

  public User getApproverForGivenSupervisoryNodeAndProgram(SupervisoryNode supervisoryNode, Program program) {
    List<User> users = userRepository.getUsersWithRightInNodeForProgram(program, supervisoryNode, APPROVE_REQUISITION);
    if (users.size() == 0) return null;
    return users.get(0);
  }

  public List<SupervisoryNode> getAll() {
    return supervisoryNodeRepository.getAll();
  }
}
