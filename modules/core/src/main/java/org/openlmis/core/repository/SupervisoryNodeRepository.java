/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.SupervisoryNodeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.openlmis.core.domain.Right.commaSeparateRightNames;


@Component
@NoArgsConstructor
public class SupervisoryNodeRepository {
  private SupervisoryNodeMapper supervisoryNodeMapper;
  private FacilityRepository facilityRepository;
  private RequisitionGroupRepository requisitionGroupRepository;

  @Autowired
  public SupervisoryNodeRepository(SupervisoryNodeMapper supervisoryNodeMapper, FacilityRepository facilityRepository, RequisitionGroupRepository requisitionGroupRepository) {
    this.supervisoryNodeMapper = supervisoryNodeMapper;
    this.facilityRepository = facilityRepository;
    this.requisitionGroupRepository = requisitionGroupRepository;
  }

  public void insert(SupervisoryNode supervisoryNode) {
    supervisoryNodeMapper.insert(supervisoryNode);
  }

  public List<SupervisoryNode> getAllSupervisoryNodesInHierarchyBy(Long userId, Long programId, Right... rights) {
    return supervisoryNodeMapper.getAllSupervisoryNodesInHierarchyBy(userId, programId, commaSeparateRightNames(rights));
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
    RequisitionGroup requisitionGroup = requisitionGroupRepository.getRequisitionGroupForProgramAndFacility(program, facility);
    return (requisitionGroup == null) ? null : supervisoryNodeMapper.getFor(requisitionGroup.getCode());
  }

  public SupervisoryNode getParent(Long id) {
    return supervisoryNodeMapper.getParent(id);
  }

  public List<SupervisoryNode> getAll() {
    return supervisoryNodeMapper.getAll();
  }

  public List<SupervisoryNode> getAllSupervisoryNodesInHierarchyBy(Long userId, Right... rights) {
    return supervisoryNodeMapper.getAllSupervisoryNodesInHierarchyByUserAndRights(userId, commaSeparateRightNames(rights));
  }

  public List<SupervisoryNode> getAllParentSupervisoryNodesInHierarchy(SupervisoryNode node) {
    return supervisoryNodeMapper.getAllParentSupervisoryNodesInHierarchy(node);
  }

  public SupervisoryNode getByCode(SupervisoryNode supervisoryNode) {
    return supervisoryNodeMapper.getByCode(supervisoryNode);
  }

  public void update(SupervisoryNode supervisoryNode) {
    supervisoryNodeMapper.update(supervisoryNode);
  }

  public List<SupervisoryNode> getCompleteList(){
      return supervisoryNodeMapper.getCompleteList();
  }

  public SupervisoryNode loadSupervisoryNodeById(Long id)
  {
      return supervisoryNodeMapper.getSupervisoryNodeById(id);
  }

}
