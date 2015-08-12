package org.openlmis.core.upload;

import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.dto.StockAdjustmentReasonProgram;
import org.openlmis.core.service.StockManagementConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StockAdjustmentReasonProgramHandler extends AbstractModelPersistenceHandler {

  @Autowired
  private StockManagementConfigService service;

  @Override
  protected BaseModel getExisting(BaseModel record) {
    StockAdjustmentReasonProgram entry = (StockAdjustmentReasonProgram)record;
    return service.getAdjustmentReasonProgram(entry.getProgram().getCode(), entry.getReason().getName());
  }

  @Override
  protected void save(BaseModel record) {
    service.saveAdjustmentReasonProgram((StockAdjustmentReasonProgram)record);
  }

  @Override
  public String getMessageKey() {
    return "error.duplicate.stock.adjustment.reason.program.code";
  }
}
