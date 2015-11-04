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
import org.openlmis.core.domain.Program;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ProgramMapper maps the Program entity to corresponding representation in database. Apart from basic CRUD operations
 * provides methods like getting user supervised/home facility active programs, getting programs for a user and facility
 * on which user have given rights.
 */
@Repository
public interface ProgramMapper {

  @Insert({"INSERT INTO programs(code, name, description, active, push, templateConfigured, regimenTemplateConfigured)",
    "VALUES (#{code}, #{name}, #{description}, #{active}, #{push}, #{templateConfigured}, #{regimenTemplateConfigured})"})
  @Options(useGeneratedKeys = true)
  Integer insert(Program program);

  @Delete("DELETE FROM programs WHERE code=#{code}")
  public void deleteByCode(String code);

  @Select({"SELECT P.*",
    "FROM programs P, programs_supported PS",
    "WHERE P.id = PS.programId AND",
    "PS.facilityId = #{facilityId} AND",
    "PS.active = true AND",
    "P.active = true"})
  List<Program> getActiveByFacility(Long facilityId);

  @Select("SELECT * FROM programs WHERE push = FALSE ORDER BY name ")
  List<Program> getAllPullPrograms();

  @Select("SELECT * FROM programs WHERE push = TRUE ORDER BY name ")
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

  @Select({"SELECT DISTINCT p.*",
    "FROM programs p",
    "INNER JOIN role_assignments ra ON p.id = ra.programId",
    "INNER JOIN role_rights rr ON ra.roleId = rr.roleId",
    "WHERE ra.userId = #{userId}",
    "AND rr.rightName = ANY (#{commaSeparatedRights}::VARCHAR[])",
    "AND ra.supervisoryNodeId IS NOT NULL",
    "AND p.active = TRUE " ,
    "AND p.enableIvdForm = TRUE ",
    "AND p.push = FALSE"})
  List<Program> getUserSupervisedActiveIvdPrograms(@Param(value = "userId") Long userId,
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
    "AND p.enableIvdForm = TRUE ",
    "AND ps.facilityId = #{facilityId}",
    "AND rr.rightName = ANY(#{commaSeparatedRights}::VARCHAR[])"})
  List<Program> getIvdProgramsSupportedByUserHomeFacilityWithRights(@Param("facilityId") Long facilityId,
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

  @Update("UPDATE programs SET regimenTemplateConfigured = true WHERE id = #{id}")
  void setRegimenTemplateConfigured(Long id);

  @Update("UPDATE programs SET sendFeed = #{sendFeed} WHERE code = #{program.code}")
  void setFeedSendFlag(@Param("program") Program program, @Param("sendFeed") Boolean sendFeed);

  @Select("SELECT * FROM programs WHERE sendFeed = TRUE")
  List<Program> getProgramsForNotification();

  @Update("UPDATE programs SET " +
    "code = #{code}, name = #{name}, " +
    "isEquipmentConfigured = #{isEquipmentConfigured}, " +
    "showNonFullSupplyTab = #{showNonFullSupplyTab}, " +
    "hideSkippedProducts = #{hideSkippedProducts}, " +
    "enableSkipPeriod = #{enableSkipPeriod}," +
    "enableIvdForm = #{enableIvdForm},  " +
    "budgetingApplies = #{budgetingApplies}, " +
    "usepriceschedule = #{usePriceSchedule}" +
    "WHERE id = #{id}")
  void update(Program program);

  @Select("SELECT * FROM programs " +
    " where enableIvdForm = true " +
    " order by name")
  List<Program> getAllIvdPrograms();
}