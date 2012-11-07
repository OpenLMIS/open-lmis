package org.openlmis.core.repository.mapper;

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
    List<Program> getAll();

    @Insert("INSERT INTO PROGRAM(ID, NAME, DESCRIPTION)" +
            " values (#{program.id}, #{program.name}, #{program.description})")
    int insert(@Param("program") Program program);

    @Delete("DELETE FROM PROGRAM")
    void deleteAll();


    @Select("select * from program, programs_supported where program.id = programs_supported.program_id and facility_code = #{facilityCode}")
    @Results(value = {
            @Result(property = "id", column = "program.id"),
            @Result(property = "name", column = "program.name"),
            @Result(property = "description", column = "program.description")
    })
    List<Program> getByFacilityCode(String facilityCode);
}