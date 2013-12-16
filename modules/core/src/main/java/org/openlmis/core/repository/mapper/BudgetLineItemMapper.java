package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Update;
import org.openlmis.core.domain.BudgetLineItem;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetLineItemMapper {

  @Insert({
    "INSERT INTO budget_line_items (facilityCode, programCode, budgetFileId, periodId, periodDate, allocatedBudget, notes) ",
    "VALUES (#{facilityCode}, #{programCode}, #{budgetFileId}, #{periodId}, #{periodDate}, #{allocatedBudget}, #{notes})"
  })
  @Options(useGeneratedKeys = true)
  void insert(BudgetLineItem budgetLineItem);

  @Update({
    "UPDATE budget_line_items SET budgetFileId = #{budgetFileId}, periodDate = #{periodDate}, allocatedBudget = #{allocatedBudget}, notes = #{notes} " ,
      "WHERE facilityCode = #{facilityCode} AND programCode = #{programCode} AND periodId = #{periodId}"
  })
  void update(BudgetLineItem budgetLineItem);
}
