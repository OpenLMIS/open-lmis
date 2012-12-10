package org.openlmis.rnr.handler;

import lombok.NoArgsConstructor;
import org.openlmis.rnr.domain.RequisitionGroup;
import org.openlmis.rnr.mapper.RequisitionGroupMapper;
import org.openlmis.rnr.repository.RequisitionGroupRepository;
import org.openlmis.rnr.service.RequisitionGroupService;
import org.openlmis.upload.Importable;
import org.openlmis.upload.handler.AbstractModelPersistenceHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class RequisitionGroupPersistenceHandler extends AbstractModelPersistenceHandler {

  private RequisitionGroupRepository requisitionGroupRepository;

  @Autowired
  public RequisitionGroupPersistenceHandler(RequisitionGroupRepository requisitionGroupRepository) {
    this.requisitionGroupRepository = requisitionGroupRepository;
  }

  @Override
  protected void save(Importable importable, String userName) {
    RequisitionGroup requisitionGroup = (RequisitionGroup) importable;
    requisitionGroup.setModifiedBy(userName);
    requisitionGroupRepository.insert(requisitionGroup);
  }
}
