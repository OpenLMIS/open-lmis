package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.openlmis.rnr.domain.RnrColumn;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RnrColumnMapper {

    @Select(value = "SELECT master.* FROM Master_RnR_Template as master")
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
            @Result(property = "mandatory", column = "is_mandatory"),
            @Result(property = "cyclicDependencies", javaType = java.util.List.class, column = "column_name",
                    many = @Many(select = "getCyclicDependencyFor"))
    })
    List<RnrColumn> fetchAllMasterRnRColumns();

    @Select(value = "SELECT master.* FROM Master_RnR_Template as master, Master_Template_Column_Cyclic_Dependency as dependency" +
            " where master.column_name = dependency.dependent_column_name" +
            " and dependency.column_name = #{columnName}")
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
    List<RnrColumn> getCyclicDependencyFor(String columnName);
}
