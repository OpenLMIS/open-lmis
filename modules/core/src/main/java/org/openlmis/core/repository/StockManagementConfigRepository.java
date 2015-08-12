package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.dto.StockAdjustmentReason;
import org.openlmis.core.dto.StockAdjustmentReasonProgram;
import org.openlmis.core.repository.mapper.StockAdjustmentReasonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@NoArgsConstructor
public class StockManagementConfigRepository {

  @Autowired
  StockAdjustmentReasonMapper adjustmentReasonMapper;

  public List<StockAdjustmentReason> getAdjustmentReasons(Boolean additive) {
    if (additive != null) {
      return adjustmentReasonMapper.getAllByAdditive(additive);
    } else {
      return adjustmentReasonMapper.getAll();
    }
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
