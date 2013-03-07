package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.RoleAssignment;
import org.openlmis.core.repository.mapper.RoleAssignmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.openlmis.core.domain.Right.commaSeparateRightNames;

@Repository
@NoArgsConstructor
public class RoleAssignmentRepository {

  RoleAssignmentMapper mapper;

  @Autowired
  public RoleAssignmentRepository(RoleAssignmentMapper roleAssignmentMapper) {
    this.mapper = roleAssignmentMapper;
  }

  public List<RoleAssignment> getRoleAssignmentsForUserWithRight(Right right, int userId) {
    return mapper.getRoleAssignmentsWithGivenRightForAUser(right, userId);
  }

  public void insertRoleAssignment(Integer userId, Integer programId, Integer supervisoryNodeId, Integer roleId) {
    mapper.insertRoleAssignment(userId, programId, supervisoryNodeId, roleId);
  }

  public void deleteAllRoleAssignmentsForUser(Integer id) {
    mapper.deleteAllRoleAssignmentsForUser(id);
  }

  public List<RoleAssignment> getSupervisorRoles(Integer userId) {
    return mapper.getSupervisorRoles(userId);
  }

  public List<RoleAssignment> getHomeFacilityRoles(Integer userId) {
    return mapper.getHomeFacilityRoles(userId);
  }

  public List<RoleAssignment> getHomeFacilityRolesForUserOnGivenProgramWithRights(Integer userId, Integer programId, Right... rights) {
    return mapper.getHomeFacilityRolesForUserOnGivenProgramWithRights(userId, programId, commaSeparateRightNames(rights));
  }
}
