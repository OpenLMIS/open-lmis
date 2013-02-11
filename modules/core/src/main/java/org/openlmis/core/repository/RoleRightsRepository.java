package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.Role;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.RoleRightsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@NoArgsConstructor
public class RoleRightsRepository {

  private RoleRightsMapper roleRightsMapper;

  @Autowired
  public RoleRightsRepository(RoleRightsMapper roleRightsMapper) {
    this.roleRightsMapper = roleRightsMapper;
  }

  public Set<Right> getAllRightsForUser(String username) {
    return roleRightsMapper.getAllRightsForUserByUserName(username);
  }

  public void createRole(Role role) {
    try {
      roleRightsMapper.insertRole(role);
    } catch (DuplicateKeyException e) {
      throw new DataException("Duplicate Role found");
    }


    role.setRights(getRightsWithItsDependents(role.getRights()));
    assignRightsToRole(role);
  }

  public void updateRole(Role role) {
    try {
      roleRightsMapper.updateRole(role);
    } catch (DuplicateKeyException e) {
      throw new DataException("Duplicate Role found");
    }
    roleRightsMapper.deleteAllRightsForRole(role.getId());

    assignRightsToRole(role);
  }

  private void assignRightsToRole(Role role) {
    for (Right right : role.getRights()) {
      roleRightsMapper.createRoleRight(role.getId(), right);
    }
  }

  private Set<Right> getRightsWithItsDependents(Set<Right> rightList) {
    final Set<Right> rights = new HashSet<>();
    for (Right right : rightList) {
      rights.add(right);
      rights.addAll(right.getDependentRights());
    }
    return rights;
  }

  public List<Role> getAllRoles() {
    return roleRightsMapper.getAllRoles();
  }

  public Role getRole(int roleId) {
    return roleRightsMapper.getRole(roleId);
  }


  public Set<Right> getAllRightsForUser(Integer userId) {
    return roleRightsMapper.getAllRightsForUserById(userId);
  }
}
