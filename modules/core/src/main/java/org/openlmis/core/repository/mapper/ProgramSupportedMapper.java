package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.core.domain.ProgramSupported;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgramSupportedMapper {
  @Insert("INSERT INTO programs_supported" +
      "(facilityId, programId, active, startDate, modifiedBy, modifiedDate) VALUES (" +
      "#{facilityId}, #{programId}, #{active}, #{startDate}, #{modifiedBy}, #{modifiedDate})")
  void addSupportedProgram(ProgramSupported programSupported);

  @Select("SELECT * FROM programs_supported " +
      "WHERE facilityId = #{facilityId} AND programId = #{programId} LIMIT 1")
  ProgramSupported getBy(@Param("facilityId") Integer facilityId, @Param("programId") Integer programId);

  @Delete("DELETE FROM programs_supported WHERE facilityId = #{facilityId} AND programId = #{programId}")
  void delete(@Param(value = "facilityId") Integer facilityId, @Param(value = "programId") Integer programId);
}
