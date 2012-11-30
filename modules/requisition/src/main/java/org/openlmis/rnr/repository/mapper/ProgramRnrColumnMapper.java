package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.rnr.domain.RnrColumn;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramRnrColumnMapper {

    @Insert("INSERT INTO program_rnr_template(program_code, column_id, is_visible, label, position, column_type)" +
            " values (#{programCode}, #{rnrColumn.id}, #{rnrColumn.visible}, #{rnrColumn.label}, #{rnrColumn.position}, #{rnrColumn.selectedColumnType})")
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
            @Result(property = "mandatory", column = "mandatory"),
            @Result(property = "availableColumnTypesString", column = "availableColumnTypesString"),
            @Result(property = "selectedColumnTypeString", column = "selectedColumnTypeString")

    })
    @Select("select m.id as id, m.column_name as name, m.description description," +
            " p.position as position, p.label as label, m.default_value as defaultValue," +
            " m.data_source as source, m.formula as formula, m.column_indicator as indicator," +
            " p.is_visible as visible, m.is_used as used, m.is_mandatory as mandatory," +
            " p.column_type as selectedColumnTypeString, m.available_sources as availableColumnTypesString" +
            " from program_rnr_template p INNER JOIN master_rnr_template m" +
            " ON p.column_id = m.id" +
            " where p.program_code=#{programCode}" +
            " ORDER BY visible desc,position")
    List<RnrColumn> getAllRnrColumnsForProgram(String programCode);

    @Update("UPDATE Program_RnR_Template SET is_visible = #{rnrColumn.visible}, label = #{rnrColumn.label}, position = #{rnrColumn.position}, column_type = #{rnrColumn.selectedColumnType}" +
            "WHERE program_code = #{programCode} AND column_id = #{rnrColumn.id}")
    void update(@Param("programCode") String programCode, @Param("rnrColumn") RnrColumn rnrColumn);

    @Select("select m.id as id, m.column_name as name, m.description description," +
                " p.position as position, p.label as label, m.default_value as defaultValue," +
                " m.data_source as source, m.formula as formula, m.column_indicator as indicator," +
                " p.is_visible as visible, m.is_used as used, m.is_mandatory as mandatory" +
                " from program_rnr_template p INNER JOIN master_rnr_template m" +
                " ON p.column_id = m.id" +
                " where p.program_code=#{programCode} and p.is_visible = 'true'" +
                " ORDER BY visible desc,position")
    List<RnrColumn> getVisibleProgramRnrColumns(String programCode);


    @Select(value = "SELECT master.* FROM Master_RnR_Template as master ")
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "column_name"),
            @Result(property = "description", column = "description"),
            @Result(property = "position", column = "column_position"),
            @Result(property = "label", column = "column_label"),
            @Result(property = "defaultValue", column = "default_value"),
            @Result(property = "dataSource", column = "data_source"),
            @Result(property = "availableColumnTypesString", column = "available_sources"),
            @Result(property = "formula", column = "formula"),
            @Result(property = "indicator", column = "column_indicator"),
            @Result(property = "used", column = "is_used"),
            @Result(property = "visible", column = "is_visible"),
            @Result(property = "mandatory", column = "is_mandatory")
    })
    List<RnrColumn> fetchAllMasterRnRColumns();


}
