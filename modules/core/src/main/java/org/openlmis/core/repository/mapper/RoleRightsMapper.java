package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.Role;
import org.openlmis.core.domain.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRightsMapper {

    @Insert("INSERT INTO role_rights(role_id, right_id) VALUES " +
            "(#{roleId}, #{right.id})")
    int createRoleRight(@Param(value = "roleId") int roleId, @Param(value = "right") Right right);

    @Insert("INSERT INTO roles" +
            "(name, description) VALUES " +
            "(#{name}, #{description})")
    @Options(useGeneratedKeys = true)
    int insertRole(Role role);

    @Select("SELECT P.code, P.name, P.description, P.active from " +
            "role_assignments RA, users U, role_rights RR, program P, programs_supported PS WHERE " +
            "U.user_name = #{userName} " +
            "AND U.id  = RA.user_id " +
            "AND RA.role_id = RR.role_id " +
            "AND RR.right_id = #{right.id} " +
            "AND P.code = RA.program_id " +
            "AND P.code = PS.program_code " +
            "AND PS.facility_code = #{facilityCode}")
    @Results(value = {
            @Result(property = "code", column = "code"),
            @Result(property = "name", column = "name"),
            @Result(property = "description", column = "description"),
            @Result(property = "active", column = "active")})
    List<Program> getProgramWithGivenRightForAUserAndFacility(@Param(value = "right") Right right, @Param(value = "userName") String userName,
                                                              @Param(value = "facilityCode") String facilityCode);

    @Insert("INSERT INTO role_assignments" +
            "(user_id, role_id, program_id) VALUES " +
            "(#{user.id}, #{role.id}, #{program.code})")
    int createRoleAssignment(@Param(value = "user") User user, @Param(value = "role") Role role, @Param(value = "program") Program program);
}
