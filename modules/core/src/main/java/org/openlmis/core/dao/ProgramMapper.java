package org.openlmis.core.dao;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Program;

import java.util.List;

public interface ProgramMapper {

    @Select("SELECT * FROM PROGRAM")
    @Results(value = {
            @Result(property = "id", column = "ID"),
            @Result(property = "name", column = "NAME"),
            @Result(property = "description", column = "DESCRIPTION")
    })
    List<Program> selectAll();

    @Insert("INSERT INTO PROGRAM(ID, NAME, DESCRIPTION)" +
            " values (#{program.id}, #{program.name}, #{program.description})")
    int insert(@Param("program") Program program);

    @Delete("DELETE FROM PROGRAM")
    void deleteAll();

}