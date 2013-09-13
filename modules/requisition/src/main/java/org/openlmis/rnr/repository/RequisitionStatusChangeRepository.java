package org.openlmis.rnr.repository;

import org.openlmis.rnr.domain.RequisitionStatusChange;
import org.openlmis.rnr.repository.mapper.RequisitionStatusChangeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RequisitionStatusChangeRepository {

  @Autowired
  private RequisitionStatusChangeMapper mapper;

  public List<RequisitionStatusChange> getByRnrId(Long rnrId) {
    return mapper.getByRnrId(rnrId);
  }
}
