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

    requisitionGroupRepository.insert(requisitionGroup);
  }

  public List<RequisitionGroup> getRequisitionGroupsBy(List<SupervisoryNode> supervisoryNodes) {
    return requisitionGroupRepository.getRequisitionGroups(supervisoryNodes);
  }
}
