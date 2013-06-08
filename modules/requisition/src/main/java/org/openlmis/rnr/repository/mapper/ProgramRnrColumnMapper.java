/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
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
      "position, source, formulaValidationRequired," +
      "createdBy, modifiedBy)",
      "VALUES",
      "(#{programId}, #{rnrColumn.id},  #{rnrColumn.visible}, #{rnrColumn.label},",
      "#{rnrColumn.position}, #{rnrColumn.source.code}, #{rnrColumn.formulaValidationRequired}," +
      "#{rnrColumn.createdBy}, #{rnrColumn.modifiedBy})"})
  int insert(@Param("programId") Long programId, @Param("rnrColumn") RnrColumn rnrColumn);

  @Select("select 0<(select count(id) as count from program_rnr_columns where programId = #{programId})")
  boolean isRnrTemplateDefined(@Param("programId") Long programId);

  @Select({"SELECT m.id, m.name, m.description, m.formula, m.indicator, m.used, m.mandatory, m.sourceConfigurable,",
      "p.position, p.label, p.visible, p.source as sourceString, p.formulaValidationRequired",
      "FROM program_rnr_columns p INNER JOIN master_rnr_columns m",
      "ON p.masterColumnId = m.id",
      "WHERE p.programId = #{programId}",
      "ORDER BY visible DESC, position"})
  List<RnrColumn> fetchDefinedRnrColumnsForProgram(Long programId);

  @Update("UPDATE program_rnr_columns SET " +
      "visible = #{rnrColumn.visible}, " +
      "label = #{rnrColumn.label}, " +
      "position = #{rnrColumn.position}, " +
      "source = #{rnrColumn.source.code}, " +
      "formulaValidationRequired = #{rnrColumn.formulaValidationRequired}," +
      "modifiedBy = #{rnrColumn.modifiedBy}," +
      "modifiedDate = NOW() " +
      "WHERE programId = #{programId} AND masterColumnId = #{rnrColumn.id}")
  void update(@Param("programId") Long programId, @Param("rnrColumn") RnrColumn rnrColumn);

  @Select({"SELECT m.id, m.name, m.description, m.used, m.mandatory, m.formula, m.indicator,",
      "p.position, p.label, p.visible , p.source as sourceString, p.formulaValidationRequired",
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
