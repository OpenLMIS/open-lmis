package org.openlmis.rnr.dao;

import org.apache.ibatis.annotations.*;
import org.openlmis.rnr.domain.RnRColumn;

import java.util.List;

public interface RnRColumnMapper {

    final String SELECT_ALL_FROM_RNR_MASTER_TEMPLATE = "SELECT * FROM Master_RnR_Template";

    final String INSERT_RNR_COLUMN_FOR_PROGRAM = "INSERT INTO Program_RnR_Template(program_id, column_id, column_name, description, column_position, column_label, " +
            "default_value, data_source, formula, column_indicator, is_used, is_visible)" +
            " values (#{programId},#{rnRColumn.id},#{rnRColumn.name}, #{rnRColumn.description}, #{rnRColumn.position}, #{rnRColumn.label}," +
            "#{rnRColumn.defaultValue}, #{rnRColumn.dataSource}, #{rnRColumn.formula}, #{rnRColumn.indicator}, #{rnRColumn.used}, #{rnRColumn.visible})";

    @Select(value = SELECT_ALL_FROM_RNR_MASTER_TEMPLATE)
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "column_name"),
            @Result(property = "description", column = "description"),
            @Result(property = "position", column = "column_position"),
            @Result(property = "label", column = "column_label"),
            @Result(property = "defaultValue", column = "default_value"),
            @Result(property = "dataSource", column = "data_source"),
            @Result(property = "formula", column = "formula"),
            @Result(property = "indicator", column = "column_indicator"),
            @Result(property = "used", column = "is_used"),
            @Result(property = "visible", column = "is_visible")
    })
    List<RnRColumn> fetchAllMasterRnRColumns();

    @Insert(INSERT_RNR_COLUMN_FOR_PROGRAM)
    int insert(@Param("programId") Integer programId, @Param("rnRColumn") RnRColumn rnRColumn);
}
