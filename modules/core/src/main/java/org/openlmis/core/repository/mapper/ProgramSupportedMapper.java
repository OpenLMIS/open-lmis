/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramSupported;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ProgramSupportedMapper maps the ProgramSupported mapping entity to corresponding representation in database. Also
 * provides methods to replicate programs supported to virtual facility.
 */
@Repository
public interface ProgramSupportedMapper {
  @Insert("INSERT INTO programs_supported" +
    "(facilityId, programId, active, startDate, createdBy, modifiedBy, modifiedDate) VALUES (" +
    "#{facilityId}, #{program.id}, #{active}, #{startDate}, #{createdBy}, #{modifiedBy}, #{modifiedDate})")
  @Options(flushCache = true, useGeneratedKeys = true)
  void insert(ProgramSupported programSupported);

  @Select("SELECT * FROM programs_supported WHERE facilityId = #{facilityId} AND programId = #{programId}")
  @Results({
    @Result(property = "program", javaType = Program.class, column = "programId",
      one = @One(select = "org.openlmis.core.repository.mapper.ProgramMapper.getById"))
  })
  ProgramSupported getBy(@Param("facilityId") Long facilityId, @Param("programId") Long programId);

  @Delete("DELETE FROM programs_supported WHERE facilityId = #{facilityId} AND programId = #{programId}")
  void delete(@Param(value = "facilityId") Long facilityId, @Param(value = "programId") Long programId);

  @Select({"SELECT * FROM programs_supported",
    "WHERE facilityId = #{facilityId}",
    "ORDER BY programId"})
  @Results({
    @Result(property = "program", javaType = Program.class, column = "programId", one = @One(select = "org.openlmis.core.repository.mapper.ProgramMapper.getById"))
  })
  List<ProgramSupported> getAllByFacilityId(Long facilityId);

  @Update("UPDATE programs_supported SET active=#{active}, startDate=#{startDate}, modifiedDate=#{modifiedDate}, modifiedBy=#{modifiedBy}" +
    "WHERE facilityId=#{facilityId} AND programId=#{program.id}")
    //TODO use COALESCE for modifiedDate
  void update(ProgramSupported programSupported);

  @Select({"SELECT P.code FROM programs_supported PS INNER JOIN programs P ON P.id = PS.programId ",
    "WHERE PS.facilityId = #{facilityId} AND PS.active = TRUE AND P.active = TRUE"})
  @Results({
    @Result(property = "program.code", column = "code")
  })
  List<ProgramSupported> getActiveProgramsByFacilityId(Long facilityId);


  @Delete({"DELETE FROM programs_supported PS USING facilities F",
    "WHERE PS.facilityId = F.id AND F.parentFacilityId = #{id}"})
  int deleteVirtualFacilityProgramSupported(Facility parentFacility);

  @Insert({"INSERT INTO programs_supported(facilityId, programId, active, startDate, createdBy, modifiedBy)",
    "SELECT C.virtualFacilityId, programId, active, startDate, createdBy, modifiedBy",
    "FROM programs_supported PS, ",
    "(SELECT id as virtualFacilityId FROM facilities where parentFacilityId=#{id}) AS C",
    "WHERE PS.facilityId = #{id}"})
  void copyToVirtualFacilities(Facility parentFacility);
}
