package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.core.domain.Program;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramMapper {

    @Insert("INSERT INTO programs(code, name, description, active)" +
            " VALUES (#{code}, #{name}, #{description}, #{active})")
    @Options(useGeneratedKeys = true)
    Integer insert(Program program);

    @Select("SELECT * FROM programs WHERE active=true")
    List<Program> getAllActive();

    @Select("SELECT P.* " +
            "FROM programs P, programs_supported PS " +
            "WHERE P.id = PS.programId AND " +
            "PS.facilityId = #{facilityId} AND " +
            "PS.active = true AND " +
            "P.active = true")
    List<Program> getActiveByFacility(Integer facilityId);

    @Select("SELECT * FROM programs")
    List<Program> getAll();

    @Select("SELECT " +
            "p.id AS id, " +
            "p.code AS code, " +
            "p.name AS name, " +
            "p.description AS description, " +
            "ps.active AS active " +
            "FROM programs p, programs_supported ps WHERE " +
            "p.id = ps.programId AND " +
            "ps.facilityId = #{facilityId}")
    List<Program> getByFacilityId(Integer facilityId);


    @Select("SELECT id FROM programs WHERE LOWER(code) = LOWER(#{code})")
    Integer getIdForCode(String code);

    @Select("SELECT * FROM programs WHERE id = #{id}")
    Program getById(Integer id);

    @Select("SELECT DISTINCT p.* " +
            "FROM programs p " +
            "INNER JOIN role_assignments ra ON p.id = ra.programId " +
            "INNER JOIN role_rights rr ON ra.roleId = rr.roleId " +
            "WHERE ra.userId = #{userId} " +
            "AND rr.rightName = ANY (#{commaSeparatedRights}::VARCHAR[]) " +
            "AND ra.supervisoryNodeId IS NOT NULL " +
            "AND p.active = true")
    List<Program> getUserSupervisedActivePrograms(@Param(value = "userId") Integer userId, @Param(value = "commaSeparatedRights") String commaSeparatedRights);

  @Select({"SELECT DISTINCT p.* FROM programs p INNER JOIN programs_supported ps ON p.id = ps.programId",
  "INNER JOIN role_assignments ra ON ra.programId = p.id",
  "INNER JOIN role_rights rr ON rr.roleId = ra.roleId",
  "WHERE p.active = true and  ps.active=true and ra.userId = #{userId} and ps.facilityId = #{facilityId} and rr.rightName = ANY(#{commaSeparatedRights}::VARCHAR[])",
  ""})
  List<Program> getProgramsSupportedByFacilityForUserWithRight(@Param("facilityId")Integer facilityId, @Param("userId") Integer userId, @Param("commaSeparatedRights") String commaSeparatedRights);



}