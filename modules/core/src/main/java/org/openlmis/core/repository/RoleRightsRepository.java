package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.Role;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.RoleRightsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@NoArgsConstructor
public class RoleRightsRepository {

  private RoleRightsMapper roleRightsMapper;

  @Autowired
  public RoleRightsRepository(RoleRightsMapper roleRightsMapper) {
    this.roleRightsMapper = roleRightsMapper;
  }

  public List<Right> getAllRightsForUser(String username) {
    return roleRightsMapper.getAllRightsForUserByUserName(username);
  }

  public void saveRole(Role role) {
    try {
      roleRightsMapper.insertRole(role);
    } catch (DuplicateKeyException e) {
      throw new DataException("Duplicate Role found");
    }

    for (Right right : role.getRights()) {
      roleRightsMapper.createRoleRight(role.getId(), right);
    }
  }

  public List<Role> getAllRoles() {
    return roleRightsMapper.getAllRoles();
  }

  public Role getRole(int roleId) {
    return roleRightsMapper.getRole(roleId);
  }

  public void updateRole(Role role) {
    try {
      roleRightsMapper.updateRole(role);
    } catch (DuplicateKeyException e) {
      throw new DataException("Duplicate Role found");
    }
    List<Right> rights = role.getRights();

    roleRightsMapper.deleteAllRightsForRole(role.getId());

    for (Right right : rights) {
      roleRightsMapper.createRoleRight(role.getId(), right);
    }
  }

  public List<Right> getAllRightsForUser(Integer userId) {
    return roleRightsMapper.getAllRightsForUserById(userId);
  }
}
