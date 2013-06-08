/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.Role;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.helper.CommaSeparator;
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
  private CommaSeparator commaSeparator;

  @Autowired
  public RoleRightsRepository(RoleRightsMapper roleRightsMapper, CommaSeparator commaSeparator) {
    this.roleRightsMapper = roleRightsMapper;
    this.commaSeparator = commaSeparator;
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

    createRoleRights(role);
  }

  public void updateRole(Role role) {
    try {
      roleRightsMapper.updateRole(role);
    } catch (DuplicateKeyException e) {
      throw new DataException("Duplicate Role found");
    }
    roleRightsMapper.deleteAllRightsForRole(role.getId());
    createRoleRights(role);
  }

  private void createRoleRights(Role role) {
    for (Right right : getRightsWithItsDependents(role.getRights())) {
      roleRightsMapper.createRoleRight(role, right);
    }
  }

  private Set<Right> getRightsWithItsDependents(Set<Right> rightList) {
    Set<Right> rights = new HashSet<>();
    for (Right right : rightList) {
      rights.add(right);
      rights.addAll(right.getDefaultRights());
    }
    return rights;
  }

  public List<Role> getAllRoles() {
    return roleRightsMapper.getAllRoles();
  }

  public Role getRole(Long roleId) {
    return roleRightsMapper.getRole(roleId);
  }


  public Set<Right> getAllRightsForUser(Long userId) {
    return roleRightsMapper.getAllRightsForUserById(userId);
  }

  public List<Right> getRightsForUserOnSupervisoryNodeAndProgram(Long userId, List<SupervisoryNode> supervisoryNodes, Program program) {
    return roleRightsMapper.getRightsForUserOnSupervisoryNodeAndProgram(userId, commaSeparator.commaSeparateIds(supervisoryNodes), program);
  }

  public List<Right> getRightsForUserOnHomeFacilityAndProgram(Long userId, Program program) {
    return roleRightsMapper.getRightsForUserOnHomeFacilityAndProgram(userId, program);
  }
}
