package org.openlmis.rnr.dao;

import org.apache.ibatis.annotations.*;
import org.openlmis.rnr.domain.ProgramRnrColumn;
import org.openlmis.rnr.domain.RnrColumn;

public interface ProgramRnRColumnMapper {

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

}
