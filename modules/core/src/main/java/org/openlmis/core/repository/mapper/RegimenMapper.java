package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Regimen;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegimenMapper {

  @Insert({"INSERT INTO regimens (code, name, active, programId, categoryId, displayOrder, createdBy, modifiedBy) ",
    "VALUES (#{code}, #{name}, #{active}, #{programId}, #{category.id}, #{displayOrder}, #{createdBy}, #{modifiedBy})"})
  @Options(useGeneratedKeys = true)
  public void insert(Regimen regimen);

  @Select({"SELECT * FROM regimens R INNER JOIN regimen_categories RC ON R.categoryId = RC.id ",
    "WHERE R.programId=#{programId} ORDER BY RC.displayOrder,R.displayOrder"})
  @Results(value = {
    @Result(property = "category", column = "categoryId", javaType = Long.class,
      one = @One(select = "org.openlmis.core.repository.mapper.RegimenCategoryMapper.getById"))})
  List<Regimen> getByProgram(Long programId);

  @Delete("DELETE FROM regimens where programId = #{programId}")
  void deleteByProgramId(Long programId);

  @Update({"UPDATE regimens SET code = #{code}, name = #{name}, active = #{active}, displayOrder = #{displayOrder},",
    "modifiedBy = #{modifiedBy} WHERE id = #{id}"})
  void update(Regimen regimen);
}
