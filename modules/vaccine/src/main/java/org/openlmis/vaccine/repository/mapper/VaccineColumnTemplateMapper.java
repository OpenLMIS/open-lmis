/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.vaccine.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.openlmis.vaccine.domain.reports.LogisticsColumn;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaccineColumnTemplateMapper {

  @Select("select id as masterColumnId, * from vaccine_logistics_master_columns")
  List<LogisticsColumn> getAllMasterColumns();


  @Select("select v.*, c.indicator, c.mandatory, c.description, c.name from vaccine_program_logistics_columns v " +
    "           join vaccine_logistics_master_columns c " +
    "               on c.id = v.masterColumnId " +
    "         where v.programId = #{programId} " +
    "         order by v.displayOrder")
  List<LogisticsColumn> getForProgram(@Param("programId") Long programId);


  @Insert("INSERT INTO vaccine_program_logistics_columns (programId, masterColumnId, label, displayOrder, visible, createdBy) " +
            " values (#{programId}, #{masterColumnId}, #{label}, #{displayOrder}, #{visible}, #{createdBy})")
  void insertProgramColumn(LogisticsColumn column);

  @Update("UPDATE vaccine_program_logistics_columns SET " +
              " programId = #{programId}," +
              " masterColumnId = #{masterColumnId}, " +
              " label = #{label}, " +
              " displayOrder = #{displayOrder}, " +
              " visible = #{visible}," +
              " modifiedBy = #{modifiedBy}" +
              " WHERE id = #{id} ")
  void updateProgramColumn(LogisticsColumn column);
}
