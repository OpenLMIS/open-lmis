/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.RequisitionGroup;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.repository.RequisitionGroupRepository;
import org.openlmis.core.repository.SupervisoryNodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@NoArgsConstructor
public class RequisitionGroupService {

  private RequisitionGroupRepository requisitionGroupRepository;
  private SupervisoryNodeRepository supervisoryNodeRepository;

  @Autowired
  public RequisitionGroupService(RequisitionGroupRepository requisitionGroupRepository, SupervisoryNodeRepository supervisoryNodeRepository) {
    this.requisitionGroupRepository = requisitionGroupRepository;
    this.supervisoryNodeRepository = supervisoryNodeRepository;
  }

  public void save(RequisitionGroup requisitionGroup) {
    Integer supervisoryNodeId = supervisoryNodeRepository.getIdForCode(requisitionGroup.getSupervisoryNode().getCode());
    requisitionGroup.getSupervisoryNode().setId(supervisoryNodeId);

    if (requisitionGroup.getId() == null)
      requisitionGroupRepository.insert(requisitionGroup);
    else
      requisitionGroupRepository.update(requisitionGroup);
  }

  public List<RequisitionGroup> getRequisitionGroupsBy(List<SupervisoryNode> supervisoryNodes) {
    return requisitionGroupRepository.getRequisitionGroups(supervisoryNodes);
  }

  public RequisitionGroup getByCode(RequisitionGroup requisitionGroup) {
    return requisitionGroupRepository.getByCode(requisitionGroup);
  }
}
