/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.FacilityRepository;
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
  private FacilityRepository facilityRepository;

  @Autowired
  public SupervisoryNodeService(SupervisoryNodeRepository supervisoryNodeRepository, UserRepository userRepository, FacilityRepository facilityRepository) {
    this.supervisoryNodeRepository = supervisoryNodeRepository;
    this.userRepository = userRepository;
    this.facilityRepository = facilityRepository;
  }

  public void save(SupervisoryNode supervisoryNode) {
    supervisoryNode.getFacility().setId(facilityRepository.getIdForCode(supervisoryNode.getFacility().getCode()));
    validateParentNode(supervisoryNode);
    if (supervisoryNode.getId() == null)
      supervisoryNodeRepository.insert(supervisoryNode);
    else
      supervisoryNodeRepository.update(supervisoryNode);
  }

  private void validateParentNode(SupervisoryNode supervisoryNode) {
    SupervisoryNode parentNode = supervisoryNode.getParent();
    if (parentNode != null) {
      try {
        parentNode.setId(supervisoryNodeRepository.getIdForCode(parentNode.getCode()));
      } catch (DataException e) {
        throw new DataException("Supervisory Node Parent does not exist");
      }
    }
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

  public List<SupervisoryNode> getAllParentSupervisoryNodesInHierarchy(SupervisoryNode supervisoryNode) {
    return supervisoryNodeRepository.getAllParentSupervisoryNodesInHierarchy(supervisoryNode);
  }

  public SupervisoryNode getByCode(SupervisoryNode supervisoryNode) {
    return supervisoryNodeRepository.getByCode(supervisoryNode);
  }
}
