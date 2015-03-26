/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Regimen;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * RegimenMapper maps the Regimen entity to corresponding representation in database.
 */
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

    @Select({"SELECT * FROM regimens"})
    @Results(value = {
            @Result(property = "category", column = "categoryId", javaType = Long.class,
                    one = @One(select = "org.openlmis.core.repository.mapper.RegimenCategoryMapper.getById"))})
    List<Regimen> getAllRegimens();

  @Delete("DELETE FROM regimens where programId = #{programId}")
  void deleteByProgramId(Long programId);

  @Update({"UPDATE regimens SET code = #{code}, name = #{name}, active = #{active}, displayOrder = #{displayOrder},",
    "modifiedBy = #{modifiedBy} WHERE id = #{id}"})
  void update(Regimen regimen);

    @Select("SELECT * FROM regimens WHERE id = #{id}")
    Regimen getById(Long id);


}
