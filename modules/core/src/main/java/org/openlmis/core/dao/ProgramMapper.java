package org.openlmis.core.dao;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Program;

import java.util.List;

public interface ProgramMapper {

    final String SELECT_ALL = "SELECT * FROM PROGRAM";
    final String INSERT = "INSERT INTO PROGRAM(NAME, DESCRIPTION) VALUES (#{name}, #{description})";
    final String DELETE_ALL = "DELETE FROM PROGRAM";

    @Select(SELECT_ALL)
    @Results(value = {
            @Result(property = "id", column = "ID"),
            @Result(property = "name", column = "NAME"),
            @Result(property = "description", column = "DESCRIPTION")
    })
    List<Program> selectAll();

    @Insert(INSERT)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Program program);

    @Delete(DELETE_ALL)
    void deleteAll();

}
