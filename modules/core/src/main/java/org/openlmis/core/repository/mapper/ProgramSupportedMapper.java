/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramSupported;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramSupportedMapper {
  @Insert("INSERT INTO programs_supported" +
    "(facilityId, programId, active, startDate, createdBy, modifiedBy, modifiedDate) VALUES (" +
    "#{facilityId}, #{program.id}, #{active}, #{startDate}, #{createdBy}, #{modifiedBy}, #{modifiedDate})")
  @Options(flushCache = true, useGeneratedKeys = true)
  void add(ProgramSupported programSupported);

  @Select("SELECT * FROM programs_supported " +
    "WHERE facilityId = #{facilityId} AND programId = #{programId} LIMIT 1")
  @Results({
    @Result(property = "program", javaType = Program.class, column = "programId",
      one = @One(select = "org.openlmis.core.repository.mapper.ProgramMapper.getById"))
  })
  ProgramSupported getBy(@Param("facilityId") Long facilityId, @Param("programId") Long programId);

  @Delete("DELETE FROM programs_supported WHERE facilityId = #{facilityId} AND programId = #{programId}")
  void delete(@Param(value = "facilityId") Long facilityId, @Param(value = "programId") Long programId);

  @Select("SELECT * FROM programs_supported " +
    "WHERE facilityId = #{facilityId}")
  @Results({
    @Result(property = "program", javaType = Program.class, column = "programId", one = @One(select = "org.openlmis.core.repository.mapper.ProgramMapper.getById"))
  })
  List<ProgramSupported> getAllByFacilityId(Long facilityId);

  @Update("UPDATE programs_supported set active=#{active}, startDate=#{startDate}, modifiedDate=#{modifiedDate}, modifiedBy=#{modifiedBy}" +
    "where facilityId=#{facilityId} AND programId=#{program.id}")
    //TODO use COALESCE for modifiedDate
  void update(ProgramSupported programSupported);

}
