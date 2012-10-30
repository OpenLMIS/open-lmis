package org.openlmis.rnr.dao;

import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.openlmis.rnr.domain.RnRColumn;

import java.util.List;

public interface RnRColumnMapper {

    final String SELECT_ALL_FROM_RNR_MASTER_TEMPLATE = "SELECT * FROM Master_RnR_Template";

    @Select(value = SELECT_ALL_FROM_RNR_MASTER_TEMPLATE)
    @Results(value = {
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
}
