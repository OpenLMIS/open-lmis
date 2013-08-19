package org.openlmis.core.upload;

import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Budget;
import org.openlmis.core.domain.ShipmentFileDetail;
import org.openlmis.core.repository.ShipmentFileDetailRepository;
import org.openlmis.core.service.*;
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
  protected String getDuplicateMessageKey() {
    return "error.duplicate.ShipmentFileDetail";
  }
}
