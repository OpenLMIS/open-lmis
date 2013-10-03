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
    Long supervisoryNodeId = supervisoryNodeRepository.getIdForCode(requisitionGroup.getSupervisoryNode().getCode());
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
