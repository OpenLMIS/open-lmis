package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.dto.StockAdjustmentReason;
import org.openlmis.core.dto.StockAdjustmentReasonProgram;
import org.openlmis.core.repository.StockManagementConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@NoArgsConstructor
public class StockManagementConfigService {

  @Autowired
  StockManagementConfigRepository repository;

  public List<StockAdjustmentReason> getAdjustmentReasons(Boolean additive, Long programId) {
    return repository.getAdjustmentReasons(additive, programId);
  }

  public StockAdjustmentReason getAdjustmentReasonByName(String name) {
    return repository.getAdjustmentReasonByName(name);
  }

  public void saveAdjustmentReason(StockAdjustmentReason reason) {
    if (getAdjustmentReasonByName(reason.getName()) != null) {
      repository.updateAdjustmentReason(reason);
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
