/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.core.upload;

import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Budget;
import org.openlmis.core.domain.ShipmentFileDetail;
import org.openlmis.core.repository.ShipmentFileDetailRepository;
import org.openlmis.core.service.*;
import org.openlmis.upload.model.AuditFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ShipmentFileDetailHandler extends AbstractModelPersistenceHandler {

  @Autowired
  private ShipmentFileDetailService service;


  @Override
  protected BaseModel getExisting(BaseModel record) {
    ShipmentFileDetail detail = ((ShipmentFileDetail) record);
    return service.getByCodes(detail.getOrderId(), detail.getProductCode());
  }

  @Override
  protected void save(BaseModel record) {
    service.save((ShipmentFileDetail) record);
  }

  @Override
  public String getMessageKey() {
    return "error.duplicate.ShipmentFileDetail";
  }

  @Override
  public void postProcess(AuditFields auditFields) {
  }

}
