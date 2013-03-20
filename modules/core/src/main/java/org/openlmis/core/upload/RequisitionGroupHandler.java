package org.openlmis.core.upload;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.RequisitionGroup;
import org.openlmis.core.service.RequisitionGroupService;
import org.openlmis.upload.Importable;
import org.openlmis.upload.model.AuditFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component("requisitionGroupHandler")
@NoArgsConstructor
public class RequisitionGroupHandler extends AbstractModelPersistenceHandler {

  private RequisitionGroupService requisitionGroupService;

  @Autowired
  public RequisitionGroupHandler(RequisitionGroupService requisitionGroupService) {
    this.requisitionGroupService = requisitionGroupService;
  }

  @Override
  protected void save(Importable importable, AuditFields userName) {
    RequisitionGroup requisitionGroup = (RequisitionGroup) importable;
    requisitionGroup.setModifiedBy(userName.getUser());
    requisitionGroup.setModifiedDate(new Date());
    requisitionGroupService.save(requisitionGroup);
  }
}
