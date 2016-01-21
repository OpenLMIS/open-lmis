package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.StockAdjustmentReason;
import org.openlmis.core.domain.StockAdjustmentReasonProgram;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.StockAdjustmentReasonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@NoArgsConstructor
public class StockAdjustmentReasonService {

  @Autowired
  StockAdjustmentReasonRepository repository;

  public List<StockAdjustmentReason> getAdjustmentReasons(Boolean additive,
                                                          Long programId,
                                                          StockAdjustmentReason.Category category) {
    return repository.getAdjustmentReasons(additive, programId, category);
  }

  public StockAdjustmentReason getAdjustmentReasonByName(String name) {
    return repository.getAdjustmentReasonByName(name);
  }

  public void saveAdjustmentReason(StockAdjustmentReason reason) {
    if (getAdjustmentReasonByName(reason.getName()) != null) {
      throw new DataException("error.stock.adjustment.reason.exists");
    } else {
      repository.insertAdjustmentReason(reason);
    }
  }

  public StockAdjustmentReasonProgram getAdjustmentReasonProgram(String programCode, String reasonName) {
    return repository.getAdjustmentReasonProgram(programCode, reasonName);
  }

  public void saveAdjustmentReasonProgram(StockAdjustmentReasonProgram entry) {
    if (entry.getId() != null) {
      repository.updateAdjustmentReasonProgram(entry);
    } else {
      repository.insertAdjustmentReasonProgram(entry);
    }
  }
}
