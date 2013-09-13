package org.openlmis.rnr.service;

import org.openlmis.rnr.domain.RequisitionStatusChange;
import org.openlmis.rnr.repository.RequisitionStatusChangeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RequisitionStatusChangeService {

  @Autowired
  private RequisitionStatusChangeRepository repository;

  public List<RequisitionStatusChange> getByRnrId(Long rnrId) {
    return repository.getByRnrId(rnrId);
  }
}
