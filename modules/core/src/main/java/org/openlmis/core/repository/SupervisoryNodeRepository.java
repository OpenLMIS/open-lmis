/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.SupervisoryNodeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.openlmis.core.domain.RightName.commaSeparateRightNames;

/**
 * SupervisoryNodeRepository is Repository class for SupervisoryNode related database operations.
 */

@Component
@NoArgsConstructor
public class SupervisoryNodeRepository {

  private SupervisoryNodeMapper supervisoryNodeMapper;
  private FacilityRepository facilityRepository;
  private RequisitionGroupRepository requisitionGroupRepository;

  @Autowired
  public SupervisoryNodeRepository(SupervisoryNodeMapper supervisoryNodeMapper,
                                   FacilityRepository facilityRepository,
                                   RequisitionGroupRepository requisitionGroupRepository) {
    this.supervisoryNodeMapper = supervisoryNodeMapper;
    this.facilityRepository = facilityRepository;
    this.requisitionGroupRepository = requisitionGroupRepository;
  }

  public void insert(SupervisoryNode supervisoryNode) {
    try {
      supervisoryNodeMapper.insert(supervisoryNode);
    } catch (DuplicateKeyException ex) {
      throw new DataException("error.duplicate.code.supervisory.node");
    }
  }

  public List<SupervisoryNode> getAllSupervisoryNodesInHierarchyBy(Long userId, Long programId, String... rightNames) {
    return supervisoryNodeMapper.getAllSupervisoryNodesInHierarchyBy(userId, programId,
      commaSeparateRightNames(rightNames));
  }

  public Long getIdForCode(String code) {
    Long supervisoryNodeId = supervisoryNodeMapper.getIdForCode(code);
    if (supervisoryNodeId == null)
      throw new DataException("error.supervisory.node.invalid");

    return supervisoryNodeId;
  }

  public Long getSupervisoryNodeParentId(Long supervisoryNodeId) {
    SupervisoryNode parent = supervisoryNodeMapper.getSupervisoryNode(supervisoryNodeId).getParent();
    return parent == null ? null : parent.getId();
  }

  public SupervisoryNode getFor(Facility facility, Program program) {
    RequisitionGroup requisitionGroup = requisitionGroupRepository.getRequisitionGroupForProgramAndFacility(program,
      facility);
    return (requisitionGroup == null) ? null : supervisoryNodeMapper.getFor(requisitionGroup.getCode());
  }

  public SupervisoryNode getParent(Long id) {
    return supervisoryNodeMapper.getParent(id);
  }

  public List<SupervisoryNode> getAll() {
    return supervisoryNodeMapper.getAll();
  }

  public List<SupervisoryNode> getAllSupervisoryNodesInHierarchyBy(Long userId, String... rightNames) {
    return supervisoryNodeMapper.getAllSupervisoryNodesInHierarchyByUserAndRights(userId,
      commaSeparateRightNames(rightNames));
  }

  //Returns the specified SupervisoryNode along with all of its ancestor nodes.
  public List<SupervisoryNode> getAllParentSupervisoryNodesInHierarchy(SupervisoryNode node) {
    return supervisoryNodeMapper.getAllParentSupervisoryNodesInHierarchy(node);
  }

  //Returns the specified SupervisoryNode along with all of its descendant nodes.
  public List<SupervisoryNode> getAllChildSupervisoryNodesInHierarchy(SupervisoryNode node) {
    return supervisoryNodeMapper.getAllChildSupervisoryNodesInHierarchy(node);
  }

  public SupervisoryNode getByCode(SupervisoryNode supervisoryNode) {
    return supervisoryNodeMapper.getByCode(supervisoryNode);
  }

  public void update(SupervisoryNode supervisoryNode) {
    try {
      supervisoryNodeMapper.update(supervisoryNode);
    } catch (DuplicateKeyException ex) {
      throw new DataException("error.duplicate.code.supervisory.node");
    }
  }

  public List<SupervisoryNode> getSupervisoryNodesByParent(Pagination pagination, String nameSearchCriteria) {
    return supervisoryNodeMapper.getSupervisoryNodesByParent(nameSearchCriteria, pagination);
  }

  public List<SupervisoryNode> getSupervisoryNodesBy(Pagination pagination, String nameSearchCriteria) {
    return supervisoryNodeMapper.getSupervisoryNodesBy(nameSearchCriteria, pagination);
  }

  public Integer getTotalSearchResultCount(String param) {
    return supervisoryNodeMapper.getTotalSearchResultCount(param);
  }

  public Integer getTotalParentSearchResultCount(String param) {
    return supervisoryNodeMapper.getTotalParentSearchResultCount(param);
  }

  public SupervisoryNode getSupervisoryNode(Long id) {
    return supervisoryNodeMapper.getSupervisoryNode(id);
  }

  public List<SupervisoryNode> getFilteredSupervisoryNodesByName(String param) {
    return supervisoryNodeMapper.getFilteredSupervisoryNodesByName(param);
  }

  public List<SupervisoryNode> searchTopLevelSupervisoryNodesByName(String param) {
    return supervisoryNodeMapper.searchTopLevelSupervisoryNodesByName(param);
  }

  public Long getTotalUnassignedSupervisoryNodeOfUserBy(Long userId, Long programId) {
    return supervisoryNodeMapper.getTotalUnassignedSupervisoryNodeOfUserBy(userId, programId);
  }

}
