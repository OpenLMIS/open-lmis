package org.openlmis.rnr.dao;

import org.apache.ibatis.annotations.*;
import org.openlmis.rnr.domain.RnrColumn;

import java.util.List;

public interface RnrColumnMapper {

    @Select(value = "SELECT * FROM Master_RnR_Template")
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
            @Result(property = "visible", column = "is_visible"),
            @Result(property = "mandatory", column = "is_mandatory")
    })
    List<RnrColumn> fetchAllMasterRnRColumns();

}
