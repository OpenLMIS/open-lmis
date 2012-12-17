package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.rnr.domain.RnrColumn;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramRnrColumnMapper {

  @Insert("INSERT INTO program_rnr_template " +
      "(program_code, column_id,          is_visible,           label,              position,              source) " +
      "VALUES " +
      "(#{programCode}, #{rnrColumn.id},  #{rnrColumn.visible}, #{rnrColumn.label}, #{rnrColumn.position}, #{rnrColumn.source.code})")
  int insert(@Param("programCode") String programCode, @Param("rnrColumn") RnrColumn rnrColumn);

  @Delete("DELETE FROM program_rnr_template")
  void deleteAll();

  @Select("select 0<(select count(id) as count from program_rnr_template where program_code=#{programCode})")
  boolean isRnrTemplateDefined(@Param("programCode") String programCode);

  @Select("select m.id, m.name, m.description, m.formula, m.indicator, m.used, m.mandatory, m.sourceConfigurable, " +
      " p.position, p.label, p.is_visible AS visible, p.source as sourceString" +
      " FROM program_rnr_template p INNER JOIN master_rnr_columns m " +
      " ON p.column_id = m.id " +
      " WHERE p.program_code = #{programCode} " +
      " ORDER BY visible DESC, position")
  List<RnrColumn> getAllRnrColumnsForProgram(String programCode);

  @Update("UPDATE program_rnr_template SET " +
      "is_visible = #{rnrColumn.visible}, " +
      "label = #{rnrColumn.label}, " +
      "position = #{rnrColumn.position}, " +
      "source = #{rnrColumn.source.code}" +
      "WHERE program_code = #{programCode} AND column_id = #{rnrColumn.id}")
  void update(@Param("programCode") String programCode, @Param("rnrColumn") RnrColumn rnrColumn);

  @Select("SELECT m.id, m.name, m.description, m.used, m.mandatory, m.formula, m.indicator, " +
      " p.position, p.label, p.is_visible AS visible, p.source as sourceString" +
      " FROM program_rnr_template p INNER JOIN master_rnr_columns m" +
      " ON p.column_id = m.id" +
      " WHERE p.program_code = #{programCode} AND p.is_visible = 'true'" +
      " ORDER BY visible desc, position")
  List<RnrColumn> getVisibleProgramRnrColumns(String programCode);


  @Select(value = "SELECT * FROM master_rnr_columns")
  @Results(value = { @Result(property = "sourceString", column = "source")})
  List<RnrColumn> fetchAllMasterRnRColumns();


}
