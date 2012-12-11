package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramSupported;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramSupportedMapper {
//TODO : Change the OR query.
    @Insert("INSERT INTO PROGRAMS_SUPPORTED" +
            "(facility_id, program_id, active, modified_by, modified_date) VALUES " +
            "((SELECT id FROM facility WHERE LOWER(code) = LOWER(#{facilityCode}) OR id = #{facilityId}), " +
            "(SELECT id FROM program WHERE LOWER(code) = LOWER(#{programCode}) OR id = #{programId}), " +
            "#{active}, #{modifiedBy}, #{modifiedDate})")
    void addSupportedProgram(ProgramSupported programSupported);

    @Delete("DELETE FROM PROGRAMS_SUPPORTED")
    void deleteAll();

    @Select("SELECT DISTINCT p.code, p.name, p.description, p.active " +
            "FROM program p, facility f, programs_supported ps, user u, program  WHERE " +
            "ps.program_id = ANY(#{programIds}::INTEGER[]) AND " +
            "ps.facility_id = #{facilityId} " +
            "AND ps.program_id = p.id " +
            "AND p.active = true " +
            "AND ps.active = true")
    @Results(value = {
            @Result(property = "code", column = "program.code"),
            @Result(property = "name", column = "program.name"),
            @Result(property = "description", column = "program.description"),
            @Result(property = "active", column = "program.active")
    })
    List<Program> filterActiveProgramsAndFacility(@Param(value = "programIds") String programCodesCommaSeparated, @Param(value = "facilityId") Long facilityId);

  @Select("SELECT ps.facility_id, ps.program_id, ps.active, ps.modified_by, ps.modified_date FROM " +
      "programs_supported ps where facility_id = #{facilityId} and program_id = #{programId}")
  @Results(value = {
      @Result(property = "facilityId", column = "facility_id"),
      @Result(property = "programId", column = "program_id"),
      @Result(property = "active", column = "active"),
      @Result(property = "modifiedBy", column = "modified_by"),
      @Result(property = "modifiedDate", column = "modified_date")})
  List<ProgramSupported> getBy(@Param("facilityId") Long facilityId, @Param("programId") Long programId);


    @Delete("DELETE FROM programs_supported WHERE facility_Id=#{facilityId} AND program_id=#{programId}")
    void deleteObsoletePrograms(@Param(value = "facilityId") Long facilityId, @Param(value = "programId") Long programId);
}
