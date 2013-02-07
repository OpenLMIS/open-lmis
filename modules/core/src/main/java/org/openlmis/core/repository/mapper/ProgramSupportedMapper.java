package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramSupported;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramSupportedMapper {
  @Insert("INSERT INTO programs_supported" +
    "(facilityId, programId, active, startDate, modifiedBy) VALUES (" +
    "#{facilityId}, #{programId}, #{active}, #{startDate}, #{modifiedBy})")
  @Options(flushCache = true)
  void addSupportedProgram(ProgramSupported programSupported);

  @Select("SELECT * FROM programs_supported " +
    "WHERE facilityId = #{facilityId} AND programId = #{programId} LIMIT 1")
  ProgramSupported getBy(@Param("facilityId") Integer facilityId, @Param("programId") Integer programId);

  @Delete("DELETE FROM programs_supported WHERE facilityId = #{facilityId} AND programId = #{programId}")
  void delete(@Param(value = "facilityId") Integer facilityId, @Param(value = "programId") Integer programId);

  @Select("SELECT * FROM programs_supported " +
    "WHERE facilityId = #{facilityId}")
  @Results({
    @Result(property = "program", javaType = Program.class, column = "programId", one = @One(select = "org.openlmis.core.repository.mapper.ProgramMapper.getById"))
  })
  List<ProgramSupported> getAllByFacilityId(Integer facilityId);
}
