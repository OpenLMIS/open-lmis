package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.Role;
import org.openlmis.core.domain.RoleAssignment;
import org.openlmis.core.repository.RoleAssignmentRepository;
import org.openlmis.core.repository.RoleRightsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;

@Service
@NoArgsConstructor
public class RoleRightsService {

  private RoleRightsRepository roleRightsRepository;
  private RoleAssignmentRepository roleAssignmentRepository;

  @Autowired
  public RoleRightsService(RoleRightsRepository roleRightsRepository, RoleAssignmentRepository roleAssignmentRepository) {
    this.roleRightsRepository = roleRightsRepository;
    this.roleAssignmentRepository = roleAssignmentRepository;
  }

  public List<RoleAssignment> getRoleAssignments(Right right, int userId) {
    return roleAssignmentRepository.getRoleAssignmentsForUserWithRight(right, userId);
  }

  public Set<Right> getRights(String username) {
    return roleRightsRepository.getAllRightsForUser(username);
  }

  public Set<Right> getAllRights() {
    return new LinkedHashSet<>(asList(Right.values()));
  }

  public void saveRole(Role role) {
    role.validate();
    roleRightsRepository.createRole(role);
  }

  public List<Role> getAllRoles() {
    return roleRightsRepository.getAllRoles();
  }

  public Role getRole(int id) {
    return roleRightsRepository.getRole(id);
  }

  public void updateRole(Role role) {
    roleRightsRepository.updateRole(role);
  }

  public Set<Right> getRights(Integer userId) {
    return roleRightsRepository.getAllRightsForUser(userId);
  }
}
