package org.openlmis.rnr.handler;

import lombok.NoArgsConstructor;
import org.openlmis.rnr.domain.RequisitionGroup;
import org.openlmis.rnr.service.RequisitionGroupService;
import org.openlmis.upload.Importable;
import org.openlmis.upload.handler.AbstractModelPersistenceHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("requisitionGroupHandler")
@NoArgsConstructor
public class RequisitionGroupHandler extends AbstractModelPersistenceHandler {

  private RequisitionGroupService requisitionGroupService;

  @Autowired
  public RequisitionGroupHandler(RequisitionGroupService requisitionGroupService) {
    this.requisitionGroupService = requisitionGroupService;
  }

  @Override
  protected void save(Importable importable, String userName) {
    RequisitionGroup requisitionGroup = (RequisitionGroup) importable;
    requisitionGroup.setModifiedBy(userName);
    requisitionGroupService.save(requisitionGroup);
  }
}
