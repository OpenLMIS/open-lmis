package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramMapper {

    @Select("INSERT INTO programs(code, name, description, active)" +
            " VALUES (#{program.code}, #{program.name}, #{program.description}, #{program.active}) returning id")
    @Options(useGeneratedKeys = true)
    Integer insert(@Param("program") Program program);

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
    Integer getIdByCode(String code);

    @Select("SELECT * FROM programs WHERE id = #{id}")
    Program getById(Integer id);

    @Select("SELECT p.* " +
            "FROM programs p " +
            "INNER JOIN role_assignments ra ON p.id = ra.programId " +
            "INNER JOIN role_rights rr ON ra.roleId = rr.roleId " +
            "INNER JOIN users u ON u.id = ra.userId " +
            "WHERE u.userName = #{userName} " +
            "AND rr.rightId = #{right.name} " +
            "AND ra.supervisoryNodeId IS NOT NULL " +
            "AND p.active = true")
    List<Program> getUserSupervisedActivePrograms(@Param(value = "userName") String userName, @Param(value = "right") Right right);

}