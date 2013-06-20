/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Program;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramMapper {

  @Insert({"INSERT INTO programs(code, name, description, active, push)",
    "VALUES (#{code}, #{name}, #{description}, #{active}, #{push})"})
  @Options(useGeneratedKeys = true)
  Integer insert(Program program);

  @Select({"SELECT P.*",
    "FROM programs P, programs_supported PS",
    "WHERE P.id = PS.programId AND",
    "PS.facilityId = #{facilityId} AND",
    "PS.active = true AND",
    "P.active = true"})
  List<Program> getActiveByFacility(Long facilityId);

  @Select("SELECT * FROM programs WHERE push = FALSE ORDER BY templateConfigured DESC, name ")
  List<Program> getAllPullPrograms();

  @Select("SELECT * FROM programs WHERE push = TRUE ORDER BY templateConfigured DESC, name ")
  List<Program> getAllPushPrograms();

  @Select({"SELECT",
    "p.id AS id,",
    "p.code AS code,",
    "p.name AS name,",
    "p.description AS description,",
    "ps.active AS active",
    "FROM programs p, programs_supported ps WHERE",
    "p.id = ps.programId AND",
    "ps.facilityId = #{facilityId}"})
  List<Program> getByFacilityId(Long facilityId);


  @Select("SELECT id FROM programs WHERE LOWER(code) = LOWER(#{code})")
  Long getIdForCode(String code);

  @Select("SELECT * FROM programs WHERE id = #{id}")
  Program getById(Long id);

  @Select({"SELECT DISTINCT p.*",
    "FROM programs p",
    "INNER JOIN role_assignments ra ON p.id = ra.programId",
    "INNER JOIN role_rights rr ON ra.roleId = rr.roleId",
    "WHERE ra.userId = #{userId}",
    "AND rr.rightName = ANY (#{commaSeparatedRights}::VARCHAR[])",
    "AND ra.supervisoryNodeId IS NOT NULL",
    "AND p.active = TRUE",
    "AND p.push = FALSE"})
  List<Program> getUserSupervisedActivePrograms(@Param(value = "userId") Long userId,
                                                @Param(value = "commaSeparatedRights") String commaSeparatedRights);

  @Select({"SELECT DISTINCT p.* ",
    "FROM programs p",
    "INNER JOIN programs_supported ps ON p.id = ps.programId",
    "INNER JOIN role_assignments ra ON ra.programId = p.id",
    "INNER JOIN role_rights rr ON rr.roleId = ra.roleId",
    "WHERE ra.supervisoryNodeId IS NULL",
    "AND p.active = TRUE",
    "AND p.push = FALSE",
    "AND ps.active= TRUE",
    "AND ra.userId = #{userId}",
    "AND ps.facilityId = #{facilityId}",
    "AND rr.rightName = ANY(#{commaSeparatedRights}::VARCHAR[])"})
  List<Program> getProgramsSupportedByUserHomeFacilityWithRights(@Param("facilityId") Long facilityId,
                                                                 @Param("userId") Long userId,
                                                                 @Param("commaSeparatedRights") String commaSeparatedRights);

  @Select({"SELECT DISTINCT p.*",
    "FROM programs p",
    "INNER JOIN role_assignments ra ON p.id = ra.programId",
    "INNER JOIN role_rights rr ON ra.roleId = rr.roleId",
    "WHERE ra.userId = #{userId}",
    "AND rr.rightName = ANY (#{commaSeparatedRights}::VARCHAR[])",
    "AND p.active = true"})
  List<Program> getActiveProgramsForUserWithRights(@Param(value = "userId") Long userId,
                                                   @Param(value = "commaSeparatedRights") String commaSeparatedRights);


  @Update("UPDATE programs SET templateConfigured = true WHERE id = #{id}")
  void setTemplateConfigured(Long id);

  @Select({"SELECT DISTINCT p.* FROM programs p INNER JOIN programs_supported ps ON p.id = ps.programId",
    "INNER JOIN role_assignments ra ON ra.programId = p.id",
    "INNER JOIN role_rights rr ON rr.roleId = ra.roleId",
    "AND p.active = TRUE",
    "AND ps.active= TRUE",
    "AND ra.userId = #{userId}",
    "AND ps.facilityId = #{facilityId}",
    "AND rr.rightName = ANY(#{commaSeparatedRights}::VARCHAR[])"})
  List<Program> getProgramsForUserByFacilityAndRights(@Param("facilityId") Long facilityId,
                                                      @Param("userId") Long userId,
                                                      @Param("commaSeparatedRights") String commaSeparatedRights);

  @Select("SELECT * FROM programs ORDER BY templateConfigured DESC, name")
  List<Program> getAll();



  @Select("SELECT * FROM programs WHERE LOWER(code) = LOWER(#{code})")
  Program getByCode(String code);

  @Select("SELECT * FROM programs ORDER BY regimenTemplateConfigured DESC, name")
  List<Program> getAllByRegimenTemplate();
}