package org.openlmis.rnr.service;

import lombok.NoArgsConstructor;
import org.openlmis.rnr.domain.RequisitionGroup;
import org.openlmis.rnr.repository.RequisitionGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class RequisitionGroupService {

  private RequisitionGroupRepository requisitionGroupRepository;

  @Autowired
  public RequisitionGroupService(RequisitionGroupRepository requisitionGroupRepository) {

    this.requisitionGroupRepository = requisitionGroupRepository;
  }

  public void save(RequisitionGroup requisitionGroup) {
    requisitionGroupRepository.save(requisitionGroup);
  }
}
