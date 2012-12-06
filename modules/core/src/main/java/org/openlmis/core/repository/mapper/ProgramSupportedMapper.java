package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramSupported;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramSupportedMapper {

    @Insert("INSERT INTO PROGRAMS_SUPPORTED" +
            "(FACILITY_CODE, PROGRAM_CODE, ACTIVE, MODIFIED_BY, MODIFIED_DATE) VALUES" +
            "(#{facilityCode}, #{programCode}, #{active}, #{modifiedBy}, #{modifiedDate})")
    void addSupportedProgram(ProgramSupported programSupported);

    @Delete("DELETE FROM PROGRAMS_SUPPORTED")
    void deleteAll();

    @Select("SELECT * FROM PROGRAMS_SUPPORTED WHERE FACILITY_CODE=#{facilityCode} AND PROGRAM_CODE=#{programCode}")
    @Results(value = {
            @Result(property = "facilityCode", column = "FACILITY_CODE"),
            @Result(property = "programCode", column = "PROGRAM_CODE"),
            @Result(property = "active", column = "is_active"),
            @Result(property = "modifiedBy", column = "MODIFIED_BY"),
            @Result(property = "modifiedDate", column = "MODIFIED_DATE")})
    List<ProgramSupported> getBy(@Param("facilityCode") String facilityCode, @Param("programCode") String programCode);

    @Select("SELECT DISTINCT p.code, p.name, p.description, p.active " +
            "FROM program p, facility f, programs_supported ps, user u, program  WHERE " +
            "ps.program_code = ANY(#{programCodes}::VARCHAR[]) AND " +
            "ps.facility_code = #{facility.code} " +
            "AND ps.program_code = p.code "+
            "AND p.active = true " +
            "AND ps.active = true")
    @Results(value = {
            @Result(property = "code", column = "program.code"),
            @Result(property = "name", column = "program.name"),
            @Result(property = "description", column = "program.description"),
            @Result(property = "active", column = "program.active")
    })
    List<Program> filterActiveProgramsAndFacility(@Param(value = "programCodes") String programCodesCommaSeparated, @Param(value = "facility") Facility facility);


    @Delete("DELETE FROM programs_supported WHERE facility_code=#{facilityCode} AND program_code=#{programCode}")
    void deleteObsoletePrograms(@Param(value = "facilityCode") String facilityCode,@Param(value = "programCode") String programCode);
}
