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
      "(#{programCode}, #{rnrColumn.id},  #{rnrColumn.visible}, #{rnrColumn.label}, #{rnrColumn.position}, #{rnrColumn.selectedColumnType.code})")
  int insert(@Param("programCode") String programCode, @Param("rnrColumn") RnrColumn rnrColumn);

  @Delete("DELETE FROM program_rnr_template")
  void deleteAll();

  @Select("select 0<(select count(id) as count from program_rnr_template where program_code=#{programCode})")
  boolean isRnrTemplateDefined(@Param("programCode") String programCode);

  @Results(value = {
      @Result(property = "id", column = "id"),
      @Result(property = "name", column = "name"),
      @Result(property = "description", column = "description"),
      @Result(property = "position", column = "position"),
      @Result(property = "label", column = "label"),
      @Result(property = "sourceConfigurable", column = "is_source_configurable"),
      @Result(property = "formula", column = "formula"),
      @Result(property = "indicator", column = "indicator"),
      @Result(property = "used", column = "used"),
      @Result(property = "visible", column = "visible"),
      @Result(property = "mandatory", column = "mandatory"),
      @Result(property = "selectedColumnTypeString", column = "selectedColumnTypeString")

  })
  @Select("select m.id as id, m.column_name as name, m.description description," +
      " p.position as position, p.label as label," +
      " m.formula as formula, m.column_indicator as indicator," +
      " p.is_visible as visible, m.is_used as used, m.is_mandatory as mandatory," +
      " p.source as selectedColumnTypeString, m.is_source_configurable" +
      " from program_rnr_template p INNER JOIN master_rnr_template m" +
      " ON p.column_id = m.id" +
      " where p.program_code=#{programCode}" +
      " ORDER BY visible desc,position")
  List<RnrColumn> getAllRnrColumnsForProgram(String programCode);

  @Update("UPDATE Program_RnR_Template SET is_visible = #{rnrColumn.visible}, label = #{rnrColumn.label}, position = #{rnrColumn.position}, source = #{rnrColumn.selectedColumnType.code}" +
      "WHERE program_code = #{programCode} AND column_id = #{rnrColumn.id}")
  void update(@Param("programCode") String programCode, @Param("rnrColumn") RnrColumn rnrColumn);

  @Results(value = {
      @Result(property = "id", column = "id"),
      @Result(property = "name", column = "name"),
      @Result(property = "description", column = "description"),
      @Result(property = "position", column = "position"),
      @Result(property = "label", column = "label"),
      @Result(property = "formula", column = "formula"),
      @Result(property = "indicator", column = "indicator"),
      @Result(property = "used", column = "used"),
      @Result(property = "visible", column = "visible"),
      @Result(property = "mandatory", column = "mandatory"),
      @Result(property = "selectedColumnTypeString", column = "selectedColumnTypeString")
  })
  @Select("select m.id as id, m.column_name as name, m.description description," +
      " p.position as position, p.label as label," +
      " m.formula as formula, m.column_indicator as indicator," +
      " p.is_visible as visible, m.is_used as used, m.is_mandatory as mandatory," +
      " p.source as selectedColumnTypeString" +
      " from program_rnr_template p INNER JOIN master_rnr_template m" +
      " ON p.column_id = m.id" +
      " where p.program_code=#{programCode} and p.is_visible = 'true'" +
      " ORDER BY visible desc,position")
  List<RnrColumn> getVisibleProgramRnrColumns(String programCode);


  @Select(value = "SELECT master.* FROM master_rnr_template as master ")
  @Results(value = {
      @Result(property = "id", column = "id"),
      @Result(property = "name", column = "column_name"),
      @Result(property = "description", column = "description"),
      @Result(property = "position", column = "column_position"),
      @Result(property = "label", column = "column_label"),
      @Result(property = "selectedColumnTypeString", column = "source"),
      @Result(property = "sourceConfigurable", column = "is_source_configurable"),
      @Result(property = "formula", column = "formula"),
      @Result(property = "indicator", column = "column_indicator"),
      @Result(property = "used", column = "is_used"),
      @Result(property = "visible", column = "is_visible"),
      @Result(property = "mandatory", column = "is_mandatory")
  })
  List<RnrColumn> fetchAllMasterRnRColumns();


}
