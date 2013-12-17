package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.openlmis.core.domain.BudgetLineItem;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetLineItemMapper {

  @Insert({
    "INSERT INTO budget_line_items (facilityId, programId, budgetFileId, periodId, periodDate, allocatedBudget, notes) ",
    "VALUES (#{facilityId}, #{programId}, #{budgetFileId}, #{periodId}, #{periodDate}, #{allocatedBudget}, #{notes})"
  })
  @Options(useGeneratedKeys = true)
  void insert(BudgetLineItem budgetLineItem);

  @Update({
    "UPDATE budget_line_items SET budgetFileId = #{budgetFileId}, periodDate = #{periodDate}, allocatedBudget = #{allocatedBudget}, notes = #{notes} ",
    "WHERE id = #{id}"
  })
  void update(BudgetLineItem budgetLineItem);

  @Select({
    "SELECT * FROM budget_line_items WHERE facilityId = #{facilityId} AND programId = #{programId} AND periodId = #{periodId}"
  })
  BudgetLineItem getBy(BudgetLineItem budgetLineItem);
}
