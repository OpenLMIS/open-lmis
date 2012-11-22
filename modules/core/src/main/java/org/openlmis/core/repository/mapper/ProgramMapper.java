package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Program;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramMapper {

    @Insert("INSERT INTO PROGRAM(CODE, NAME, DESCRIPTION)" +
            " values (#{program.code}, #{program.name}, #{program.description})")
    int insert(@Param("program") Program program);

    @Delete("DELETE FROM PROGRAM WHERE CODE = #{programCode}")
    void delete(String programCode);

    @Delete("DELETE FROM PROGRAM")
    void deleteAll();

    @Select("SELECT * FROM program WHERE active=true")
    @Results(value = {
            @Result(property = "code", column = "CODE"),
            @Result(property = "name", column = "NAME"),
            @Result(property = "description", column = "DESCRIPTION")
    })
    List<Program> getAllActive();

    @Select("select * from program P, programs_supported PS where P.code = PS.program_code and PS.facility_code = #{facilityCode} and PS.active=true and P.active=true")
    @Results(value = {
            @Result(property = "code", column = "program.code"),
            @Result(property = "name", column = "program.name"),
            @Result(property = "description", column = "program.description")
    })
    List<Program> getActiveByFacilityCode(String facilityCode);
}