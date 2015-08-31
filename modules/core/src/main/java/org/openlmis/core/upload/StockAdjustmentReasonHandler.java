package org.openlmis.core.upload;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.StockAdjustmentReason;
import org.openlmis.core.service.StockAdjustmentReasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class StockAdjustmentReasonHandler extends AbstractModelPersistenceHandler {

  @Autowired
  private StockAdjustmentReasonService service;

  @Override
  protected BaseModel getExisting(BaseModel record) {
    return service.getAdjustmentReasonByName(((StockAdjustmentReason)record).getName());
  }

  @Override
  protected void save(BaseModel record) {
    service.saveAdjustmentReason((StockAdjustmentReason)record);
  }

  @Override
  public String getMessageKey() {
    return "error.duplicate.stock.adjustment.reason.code";
  }
}
