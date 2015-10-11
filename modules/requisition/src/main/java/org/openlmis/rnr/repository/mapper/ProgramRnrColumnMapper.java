/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Program;
import org.openlmis.rnr.domain.RnrColumn;
import org.openlmis.rnr.domain.RnrColumnOption;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * It maps the RnrTemplate and RnrColumn entity to corresponding representations in database.
 */

@Repository
public interface ProgramRnrColumnMapper {

  @Insert({"INSERT INTO program_rnr_columns",
      "(programId, masterColumnId, rnrOptionId, visible, label,",
      "position, source, formulaValidationRequired, calculationOption, " +
      "createdBy, modifiedBy)",
      "VALUES",
      "(#{programId}, #{rnrColumn.id}, #{rnrColumn.configuredOption.id}, #{rnrColumn.visible}, #{rnrColumn.label},",
      "#{rnrColumn.position}, #{rnrColumn.source.code}, #{rnrColumn.formulaValidationRequired}, #{rnrColumn.calculationOption}," +
      "#{rnrColumn.createdBy}, #{rnrColumn.modifiedBy})"})
  int insert(@Param("programId") Long programId, @Param("rnrColumn") RnrColumn rnrColumn);

  @Select("select 0<(select count(id) as count from program_rnr_columns where programId = #{programId})")
  boolean isRnrTemplateDefined(@Param("programId") Long programId);

  @Select({"SELECT * FROM configurable_rnr_options WHERE id = #{id}"})
  RnrColumnOption getRnrColumnOptionById(Integer id);

  @Select({"SELECT m.id, m.name, m.description, m.formula, m.indicator, m.used, m.mandatory, m.sourceConfigurable,",
      "p.rnrOptionId, p.position, p.label, p.visible, p.source as sourceString, p.formulaValidationRequired, m.calculationOption cOptions, p.calculationOption ",
      "FROM program_rnr_columns p INNER JOIN master_rnr_columns m",
      "ON p.masterColumnId = m.id",
      "WHERE p.programId = #{programId}",
      "ORDER BY visible DESC, position"})
  @Results(value = {
      @Result(property = "id", column = "id"),
      @Result(property = "options", column = "cOptions" ),
      @Result(property = "configuredOption",  javaType = RnrColumnOption.class, column = "rnrOptionId",
          many = @Many(select = "org.openlmis.rnr.repository.mapper.ProgramRnrColumnMapper.getRnrColumnOptionById")),
      @Result(property = "rnrColumnOptions",  javaType = List.class, column = "id",
          many = @Many(select = "org.openlmis.rnr.repository.mapper.ProgramRnrColumnMapper.getRnrColumnOptionsByMasterRnrColumnId"))
  })
  List<RnrColumn> fetchDefinedRnrColumnsForProgram(Long programId);




  @Update("UPDATE program_rnr_columns SET " +
      "visible = #{rnrColumn.visible}, " +
      "rnrOptionId = #{rnrColumn.configuredOption.id}, " +
      "label = #{rnrColumn.label}, " +
      "position = #{rnrColumn.position}, " +
      "source = #{rnrColumn.source.code}, " +
      "formulaValidationRequired = #{rnrColumn.formulaValidationRequired}," +
      "modifiedBy = #{rnrColumn.modifiedBy}," +
      "calculationOption = #{rnrColumn.calculationOption}, " +
      "modifiedDate = NOW() " +
      "WHERE programId = #{programId} AND masterColumnId = #{rnrColumn.id}")
  void update(@Param("programId") Long programId, @Param("rnrColumn") RnrColumn rnrColumn);

  @Select({"SELECT m.id, m.name, m.description, m.used, m.mandatory, m.formula, m.indicator,",
      "p.position, p.label, p.visible , p.source as sourceString, p.formulaValidationRequired, p.calculationOption",
      "FROM program_rnr_columns p INNER JOIN master_rnr_columns m",
      "ON p.masterColumnId = m.id",
      "WHERE p.programId = #{programId} AND p.visible = 'true'",
      "ORDER BY visible DESC, position"})
  List<RnrColumn> getVisibleProgramRnrColumns(Long programId);

  @Select({"SELECT co.id, co.name, co.label FROM master_rnr_column_options mo INNER JOIN configurable_rnr_options co " ,
    "ON mo.rnrOptionId = co.id WHERE masterrnrcolumnid = #{masterRnrColumnId}"})
  List<RnrColumnOption> getRnrColumnOptionsByMasterRnrColumnId(Integer masterRnrColumnId);

  @Select("SELECT * FROM master_rnr_columns")
  @Results(value = {
    @Result(property = "id", column = "id"),
    @Result(property = "sourceString", column = "source"),
    @Result(property = "rnrColumnOptions",  javaType = List.class, column = "id",
      many = @Many(select = "org.openlmis.rnr.repository.mapper.ProgramRnrColumnMapper.getRnrColumnOptionsByMasterRnrColumnId"))
  })
  List<RnrColumn> fetchAllMasterRnRColumns();

  @Select({"SELECT COUNT(DISTINCT(true)) = 1 FROM program_rnr_columns",
      "WHERE formulaValidationRequired = TRUE AND programId = #{id}"})
  boolean isFormulaValidationRequired(Program program);
}
