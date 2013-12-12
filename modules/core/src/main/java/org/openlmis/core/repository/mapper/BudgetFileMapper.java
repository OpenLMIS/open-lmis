package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.openlmis.core.domain.BudgetFileInfo;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetFileMapper {
  @Insert({
    "INSERT INTO budget_file_info (fileName, processingError) VALUES (#{fileName}, #{processingError})"
  })
  @Options(useGeneratedKeys = true)
  void insert(BudgetFileInfo budgetFileInfo);
}
