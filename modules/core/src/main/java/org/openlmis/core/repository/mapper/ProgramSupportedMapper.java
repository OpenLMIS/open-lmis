package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramSupported;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramSupportedMapper {
  //TODO : Change the OR query.
  @Insert("INSERT INTO programs_supported" +
      "(facilityId, programId, active, modifiedBy, modifiedDate) VALUES " +
      "((SELECT id FROM facilities WHERE LOWER(code) = LOWER(#{facilityCode}) OR id = #{facilityId}), " +
      "(SELECT id FROM program WHERE LOWER(code) = LOWER(#{programCode}) OR id = #{programId}), " +
      "#{active}, #{modifiedBy}, #{modifiedDate})")
  void addSupportedProgram(ProgramSupported programSupported);

  @Select("SELECT DISTINCT p.* " +
      "FROM program p, facilities f, programs_supported ps, user u, program  WHERE " +
      "ps.programId = ANY(#{programIds}::INTEGER[]) AND " +
      "ps.facilityId = #{facilityId} AND " +
      "ps.programId = p.id AND " +
      "p.active = true AND " +
      "ps.active = true")
  List<Program> filterActiveProgramsAndFacility(@Param(value = "programIds") String programCodesCommaSeparated,
                                                @Param(value = "facilityId") Integer facilityId);

  @Select("SELECT * FROM programs_supported " +
      "WHERE facilityId = #{facilityId} AND programId = #{programId}")
  List<ProgramSupported> getBy(@Param("facilityId") Integer facilityId, @Param("programId") Integer programId);

  @Delete("DELETE FROM programs_supported WHERE facilityId = #{facilityId} AND programId = #{programId}")
  void delete(@Param(value = "facilityId") Integer facilityId, @Param(value = "programId") Integer programId);
}
