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
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramRnrColumnMapper {

  @Insert({"INSERT INTO program_rnr_columns",
      "(programId, masterColumnId, visible, label,",
      "position, source, formulaValidationRequired, calculationOption" +
      "createdBy, modifiedBy)",
      "VALUES",
      "(#{programId}, #{rnrColumn.id},  #{rnrColumn.visible}, #{rnrColumn.label},",
      "#{rnrColumn.position}, #{rnrColumn.source.code}, #{rnrColumn.formulaValidationRequired}, #{rnrColumn.calculationOption}" +
      "#{rnrColumn.createdBy}, #{rnrColumn.modifiedBy})"})
  int insert(@Param("programId") Long programId, @Param("rnrColumn") RnrColumn rnrColumn);

  @Select("select 0<(select count(id) as count from program_rnr_columns where programId = #{programId})")
  boolean isRnrTemplateDefined(@Param("programId") Long programId);

  @Select({"SELECT m.id, m.name, m.description, m.formula, m.indicator, m.used, m.mandatory, m.sourceConfigurable,",
      "p.position, p.label, p.visible, p.source as sourceString, p.formulaValidationRequired, p.calculationOption, m.calculationOption calculationOptions",
      "FROM program_rnr_columns p INNER JOIN master_rnr_columns m",
      "ON p.masterColumnId = m.id",
      "WHERE p.programId = #{programId}",
      "ORDER BY visible DESC, position"})
  @Results(value = {@Result(property = "calculationOptions", column = "options")})
  List<RnrColumn> fetchDefinedRnrColumnsForProgram(Long programId);

  @Update("UPDATE program_rnr_columns SET " +
      "visible = #{rnrColumn.visible}, " +
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


  @Select("SELECT * FROM master_rnr_columns")
  @Results(value = {@Result(property = "sourceString", column = "source")})
  List<RnrColumn> fetchAllMasterRnRColumns();


  @Select({"SELECT COUNT(DISTINCT(true)) = 1 FROM program_rnr_columns",
      "WHERE formulaValidationRequired = TRUE AND programId = #{id}"})
  boolean isFormulaValidationRequired(Program program);
}
