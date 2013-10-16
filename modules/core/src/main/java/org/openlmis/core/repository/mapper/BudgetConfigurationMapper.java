package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.openlmis.core.domain.EDIConfiguration;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetConfigurationMapper {

  @Select("SELECT * FROM budget_configuration")
  EDIConfiguration get();

  @Update({"UPDATE budget_configuration SET",
    "headerInFile =  #{headerInFile},",
    "modifiedDate = DEFAULT,",
    "modifiedBy = #{modifiedBy}"})
  void update(EDIConfiguration budgetConfiguration);

}
