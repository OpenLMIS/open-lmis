package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.domain.User;
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

  public SupervisoryNode getFor(int facilityId, int programId) {
    return supervisoryNodeRepository.getFor(facilityId, programId);
  }

  public User getApproverFor(Integer facilityId, Integer programId) {
    SupervisoryNode supervisoryNode = supervisoryNodeRepository.getFor(facilityId, programId);
    if(supervisoryNode == null ) return  null;
    Integer supervisoryNodeId = supervisoryNode.getId();

    List<User> users;
    while ((users = userRepository.getUsersWithRightInNodeForProgram(programId, supervisoryNodeId, APPROVE_REQUISITION)).size() == 0) {
      supervisoryNodeId = supervisoryNodeRepository.getSupervisoryNodeParentId(supervisoryNodeId);
      if(supervisoryNodeId == null) return null;
    }

    return users.get(0);
  }

  public List<SupervisoryNode> getForUserWithRight(Integer userId, Right... rights) {
    return null;  //To change body of created methods use File | Settings | File Templates.
  }
}
