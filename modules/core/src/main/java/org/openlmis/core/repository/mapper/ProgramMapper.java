package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Program;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramMapper {

    @Select("INSERT INTO program(code, name, description, active)" +
            " VALUES (#{program.code}, #{program.name}, #{program.description}, #{program.active}) returning id")
    @Options(useGeneratedKeys = true)
    Long insert(@Param("program") Program program);


    @Select("SELECT * FROM program WHERE active=true")
    @Results(value = {
            @Result(property = "id", column = "ID"),
            @Result(property = "code", column = "CODE"),
            @Result(property = "name", column = "NAME"),
            @Result(property = "description", column = "DESCRIPTION")
    })
    List<Program> getAllActive();

    @Select("select p.id, p.code, p.name, p.description from program P, programs_supported PS where P.id = PS.program_id and PS.facility_id = #{facilityId} and PS.active=true and P.active=true")
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property = "code", column = "code"),
            @Result(property = "name", column = "name"),
            @Result(property = "description", column = "description")
    })
    List<Program> getActiveByFacility(Long facilityId);

    @Select("SELECT * FROM program")
    List<Program> getAll();

    @Select("select P.id as id,P.code as code,P.name as name,P.description as desc,PS.active as active " +
        "from program P, programs_supported PS where " +
        "P.id = PS.program_id " +
        "and PS.facility_id = #{facilityId}")
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property = "code", column = "code"),
            @Result(property = "name", column = "name"),
            @Result(property = "description", column = "desc"),
            @Result(property = "active", column = "active")
    })
    List<Program> getByFacilityId(Long facilityId);
}