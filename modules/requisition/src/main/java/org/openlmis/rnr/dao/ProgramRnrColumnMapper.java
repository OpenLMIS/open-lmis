package org.openlmis.rnr.dao;

import org.apache.ibatis.annotations.*;
import org.openlmis.rnr.domain.ProgramRnrColumn;
import org.openlmis.rnr.domain.RnrColumn;

import java.util.List;

public interface ProgramRnrColumnMapper {

    @Select("SELECT * FROM Program_RnR_Template WHERE program_id=#{programId} AND column_id=#{columnId}")
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property = "columnId", column = "column_id"),
            @Result(property = "programId", column = "program_id"),
            @Result(property = "used", column = "is_used")
    })
    ProgramRnrColumn get(@Param("programId") Integer programId, @Param("columnId") Integer columnId);

    @Insert("INSERT INTO Program_RnR_Template(program_id, column_id, is_used)" +
            " values (#{programId}, #{rnrColumn.id}, #{rnrColumn.used})")
    int insert(@Param("programId") int programId, @Param("rnrColumn") RnrColumn rnrColumn);

    @Delete("DELETE FROM Program_RnR_Template")
    void deleteAll();

    @Select("select 0<(select count(id) as count from program_rnr_template where program_id=#{programId})")
    boolean isRnrTemplateDefined(@Param("programId") int programId);

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
            " m.column_position as position, m.column_label as label, m.default_value as defaultValue," +
            " m.data_source as source, m.formula as formula, m.column_indicator as indicator," +
            " p.is_used as used, m.is_visible as visible, m.is_mandatory as mandatory" +
            " from program_rnr_template p INNER JOIN master_rnr_template m" +
            " ON p.column_id = m.id" +
            " where p.program_id=#{programId}")
    List<RnrColumn> getAllRnrColumnsForProgram(int programId);

    @Update("UPDATE Program_RnR_Template set is_used = #{rnrColumn.used} " +
            "where program_id = #{programId} and column_id = #{rnrColumn.id}")
    void update(@Param("programId") int programId, @Param("rnrColumn") RnrColumn rnrColumn);
}
