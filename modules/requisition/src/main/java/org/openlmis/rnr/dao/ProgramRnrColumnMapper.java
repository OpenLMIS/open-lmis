package org.openlmis.rnr.dao;

import org.apache.ibatis.annotations.*;
import org.openlmis.rnr.domain.ProgramRnrColumn;
import org.openlmis.rnr.domain.RnrColumn;

import java.util.List;

public interface ProgramRnrColumnMapper {

    @Select("SELECT * FROM program_rnr_template WHERE program_code=#{programCode} AND column_id=#{columnId}")
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property = "columnId", column = "column_id"),
            @Result(property = "programCode", column = "program_code"),
            @Result(property = "used", column = "is_used")
    })
    ProgramRnrColumn get(@Param("programCode") String programCode, @Param("columnId") Integer columnId);

    @Insert("INSERT INTO program_rnr_template(program_code, column_id, is_used, label, position)" +
            " values (#{programCode}, #{rnrColumn.id}, #{rnrColumn.used}, #{rnrColumn.label}, #{rnrColumn.position})")
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
            @Result(property = "defaultValue", column = "defaultValue"),
            @Result(property = "dataSource", column = "source"),
            @Result(property = "formula", column = "formula"),
            @Result(property = "indicator", column = "indicator"),
            @Result(property = "used", column = "used"),
            @Result(property = "visible", column = "visible"),
            @Result(property = "mandatory", column = "mandatory")
    })
    @Select("select m.id as id, m.column_name as name, m.description description," +
            " p.position as position, p.label as label, m.default_value as defaultValue," +
            " m.data_source as source, m.formula as formula, m.column_indicator as indicator," +
            " p.is_used as used, m.is_visible as visible, m.is_mandatory as mandatory" +
            " from program_rnr_template p INNER JOIN master_rnr_template m" +
            " ON p.column_id = m.id" +
            " where p.program_code=#{programCode}")
    List<RnrColumn> getAllRnrColumnsForProgram(String programCode);

    @Update("UPDATE Program_RnR_Template SET is_used = #{rnrColumn.used}, label = #{rnrColumn.label}, position = #{rnrColumn.position}" +
            "WHERE program_code = #{programCode} AND column_id = #{rnrColumn.id}")
    void update(@Param("programCode") String programCode, @Param("rnrColumn") RnrColumn rnrColumn);

}
