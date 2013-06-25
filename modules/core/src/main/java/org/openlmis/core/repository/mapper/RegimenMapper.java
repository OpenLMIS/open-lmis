package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Regimen;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegimenMapper {

  @Insert({"INSERT INTO regimens (code, name, active, programId, categoryId, displayOrder, createdBy) ",
    "VALUES (#{code}, #{name}, #{active}, #{programId}, #{category.id}, #{displayOrder}, #{createdBy})"})
  @Options(useGeneratedKeys = true)
  public void insert(Regimen regimen);

  @Select({"SELECT * FROM regimens r INNER JOIN regimen_categories rc ON r.categoryid = rc.id ",
    "WHERE r.programId=#{programId} ORDER BY rc.displayOrder,r.displayOrder"})
  @Results(value = {
    @Result(property = "category", column = "categoryId", javaType = Long.class,
      one = @One(select = "org.openlmis.core.repository.mapper.RegimenCategoryMapper.getById"))})
  List<Regimen> getByProgram(Long programId);

  @Delete("DELETE FROM regimens where programId = #{programId}")
  void deleteByProgramId(Long programId);
}
