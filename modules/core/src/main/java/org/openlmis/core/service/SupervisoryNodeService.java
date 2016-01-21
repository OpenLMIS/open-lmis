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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.openlmis.core.domain.RightName.APPROVE_REQUISITION;

/**
 * Exposes the services for handling SupervisoryNode entity.
 */

@Service
@NoArgsConstructor
public class SupervisoryNodeService {

  @Autowired
  private SupervisoryNodeRepository supervisoryNodeRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private FacilityRepository facilityRepository;

  private Integer pageSize;

  @Autowired
  public void setPageSize(@Value("${search.page.size}") String pageSize) {
    this.pageSize = Integer.parseInt(pageSize);
  }

  public void save(SupervisoryNode supervisoryNode) {
    supervisoryNode.getFacility().setId(facilityRepository.getIdForCode(supervisoryNode.getFacility().getCode()));
    validateParentNode(supervisoryNode);
    if (supervisoryNode.getId() == null)
      supervisoryNodeRepository.insert(supervisoryNode);
    else
      supervisoryNodeRepository.update(supervisoryNode);
  }

  public List<SupervisoryNode> getSupervisoryNodesBy(Integer page, String nameSearchCriteria, Boolean parent) {
    if (parent) {
      return supervisoryNodeRepository.getSupervisoryNodesByParent(getPagination(page), nameSearchCriteria);
    }
    return supervisoryNodeRepository.getSupervisoryNodesBy(getPagination(page), nameSearchCriteria);
  }

  private void validateParentNode(SupervisoryNode supervisoryNode) {
    SupervisoryNode parentNode = supervisoryNode.getParent();
    if (parentNode != null) {
      try {
        parentNode.setId(supervisoryNodeRepository.getIdForCode(parentNode.getCode()));
      } catch (DataException e) {
        throw new DataException("error.supervisory.node.parent.not.exist");
      }
      supervisoryNode.validateParent();
    }
  }
  public List<SupervisoryNode> getAllSupervisoryNodesInHierarchyBy(Long userId, Long programId, String... rightNames) {
    return supervisoryNodeRepository.getAllSupervisoryNodesInHierarchyBy(userId, programId, rightNames);
  }

  public List<SupervisoryNode> getAllSupervisoryNodesInHierarchyBy(Long userId, String... rightNames) {
    return supervisoryNodeRepository.getAllSupervisoryNodesInHierarchyBy(userId, rightNames);
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

  //Returns the specified SupervisoryNode along with all of its ancestor nodes.
  public List<SupervisoryNode> getAllParentSupervisoryNodesInHierarchy(SupervisoryNode supervisoryNode) {
    return supervisoryNodeRepository.getAllParentSupervisoryNodesInHierarchy(supervisoryNode);
  }

  public SupervisoryNode getByCode(SupervisoryNode supervisoryNode) {
    return supervisoryNodeRepository.getByCode(supervisoryNode);
  }

  public Pagination getPagination(Integer page) {
    return new Pagination(page, pageSize);
  }

  public Integer getTotalSearchResultCount(String param, Boolean parent) {
    if(parent) {
      return supervisoryNodeRepository.getTotalParentSearchResultCount(param);
    }
    return supervisoryNodeRepository.getTotalSearchResultCount(param);
  }

  public SupervisoryNode getSupervisoryNode(Long id) {
    return supervisoryNodeRepository.getSupervisoryNode(id);
  }

  public List<SupervisoryNode> getFilteredSupervisoryNodesByName(String param) {
    return supervisoryNodeRepository.getFilteredSupervisoryNodesByName(param);
  }

  public List<SupervisoryNode> searchTopLevelSupervisoryNodesByName(String param) {
    return supervisoryNodeRepository.searchTopLevelSupervisoryNodesByName(param);
  }

  public Long getTotalUnassignedSupervisoryNodeOfUserBy(Long userId, Long programId){
    return supervisoryNodeRepository.getTotalUnassignedSupervisoryNodeOfUserBy(userId, programId);
  }
}
