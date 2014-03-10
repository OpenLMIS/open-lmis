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

/**
 * Exposes the services for handling SupervisoryNode entity.
 */

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
        throw new DataException("error.supervisory.node.parent.not.exist");
      }
    }
  }

  public List<SupervisoryNode> getAllSupervisoryNodesInHierarchyBy(Long userId, Long programId, Right... rights) {
    return supervisoryNodeRepository.getAllSupervisoryNodesInHierarchyBy(userId, programId, rights);
  }

  public List<SupervisoryNode> getAllSupervisoryNodesInHierarchyBy(Long userId, Right... rights) {
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
      Long supervisoryNodeId = supervisoryNodeRepository.getSupervisoryNodeParentId(supervisoryNode.getId());
      if (supervisoryNodeId == null) return null;
      supervisoryNode = new SupervisoryNode(supervisoryNodeId);
    }

    return users.get(0);
  }

  public SupervisoryNode getParent(Long id) {
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
