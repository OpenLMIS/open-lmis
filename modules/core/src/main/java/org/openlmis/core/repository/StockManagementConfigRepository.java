package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.dto.StockAdjustmentReason;
import org.openlmis.core.dto.StockAdjustmentReasonProgram;
import org.openlmis.core.repository.mapper.StockAdjustmentReasonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@NoArgsConstructor
public class StockManagementConfigRepository {

  @Autowired
  StockAdjustmentReasonMapper adjustmentReasonMapper;

  public List<StockAdjustmentReason> getAdjustmentReasons(Boolean additive, Long programId) {
    List<StockAdjustmentReason> reasons;
    if (programId != null) {
      reasons = adjustmentReasonMapper.getAllByProgram(programId);
    } else {
      reasons = adjustmentReasonMapper.getAll();
    }

    if (additive != null) {
      List<StockAdjustmentReason> filteredReasons = new ArrayList<>();
      for (StockAdjustmentReason reason : reasons) {
        if (additive == reason.getAdditive()) {
          filteredReasons.add(reason);
        }
      }
      reasons = filteredReasons;
    }

    return reasons;
  }

  public StockAdjustmentReason getAdjustmentReasonByName(String name) {
    return adjustmentReasonMapper.getByName(name);
  }

  public void insertAdjustmentReason(StockAdjustmentReason reason) {
    adjustmentReasonMapper.insert(reason);
  }

  public void updateAdjustmentReason(StockAdjustmentReason reason) {
    adjustmentReasonMapper.update(reason);
  }

  public StockAdjustmentReasonProgram getAdjustmentReasonProgram(String programCode, String reasonName) {
    return adjustmentReasonMapper.getAdjustmentReasonProgram(programCode, reasonName);
  }

  public void insertAdjustmentReasonProgram(StockAdjustmentReasonProgram entry) {
    adjustmentReasonMapper.insertAdjustmentReasonProgram(entry);
  }

  public void updateAdjustmentReasonProgram(StockAdjustmentReasonProgram entry) {
    adjustmentReasonMapper.updateAdjustmentReasonProgram(entry);
  }
}
