package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.RequisitionGroup;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.repository.RequisitionGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@NoArgsConstructor
public class RequisitionGroupService {

  private RequisitionGroupRepository requisitionGroupRepository;

  @Autowired
  public RequisitionGroupService(RequisitionGroupRepository requisitionGroupRepository) {

    this.requisitionGroupRepository = requisitionGroupRepository;
  }

  public void save(RequisitionGroup requisitionGroup) {
    requisitionGroupRepository.insert(requisitionGroup);
  }


    public List<RequisitionGroup> getRequisitionGroupsForSupervisoryNodes(List<SupervisoryNode> supervisoryNodes) {
        return requisitionGroupRepository.getRequisitionGroups(supervisoryNodes);
    }
}
