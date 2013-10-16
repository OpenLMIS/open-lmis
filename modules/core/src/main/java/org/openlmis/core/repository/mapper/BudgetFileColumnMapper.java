package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.openlmis.core.domain.EDIFileColumn;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetFileColumnMapper {

  @Update({"UPDATE budget_file_columns SET",
    "position = #{position},",
    "include = #{include},",
    "datePattern = #{datePattern},",
    "modifiedBy = #{modifiedBy},",
    "modifiedDate = DEFAULT ",
    "WHERE name = #{name}"})
  public void update(EDIFileColumn ediFileColumn);

  @Select("SELECT * FROM budget_file_columns")
  public List<EDIFileColumn> getAll();
}
