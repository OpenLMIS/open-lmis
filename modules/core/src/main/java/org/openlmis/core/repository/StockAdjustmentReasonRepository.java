package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.apache.commons.collections4.*;
import org.openlmis.core.domain.StockAdjustmentReason;
import org.openlmis.core.domain.StockAdjustmentReasonProgram;
import org.openlmis.core.repository.mapper.StockAdjustmentReasonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@NoArgsConstructor
public class StockAdjustmentReasonRepository {

  @Autowired
  StockAdjustmentReasonMapper adjustmentReasonMapper;

  public List<StockAdjustmentReason> getAdjustmentReasons(final Boolean additive,
                                                          final Long programId,
                                                          final StockAdjustmentReason.Category category) {
    // determine master list by program / no program
    List<StockAdjustmentReason> reasons;
    if (programId != null) {
      reasons = adjustmentReasonMapper.getAllByProgram(programId);
    } else {
      reasons = adjustmentReasonMapper.getAllDefault();
    }

    // filter out reasons based on additive and category arguments
    CollectionUtils.filter(reasons, new Predicate<StockAdjustmentReason>() {
      @Override
      public boolean evaluate(StockAdjustmentReason reason) {
        // remove if category is given and we're not in it
        if(null != category && false == reason.inCategory(category)) return false;

        // remove if additive is given and we're not of the same additive/not-additive
        if(null != additive && reason.getAdditive() != additive) return false;

        return true; // gautlet passed
      }
    });

    return reasons;
  }

  public StockAdjustmentReason getAdjustmentReasonByName(String name) {
    return adjustmentReasonMapper.getByName(name);
  }

  public void insertAdjustmentReason(StockAdjustmentReason reason) {
    adjustmentReasonMapper.insert(reason);
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
