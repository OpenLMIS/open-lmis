/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.helper.CommaSeparator;
import org.openlmis.core.repository.mapper.RoleRightsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * RoleRightsRepository is Repository class for RoleRights related database operations.
 */

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

  public void createRole(Role role) {
    try {
      roleRightsMapper.insertRole(role);
    } catch (DuplicateKeyException e) {
      throw new DataException("error.duplicate.role");
    }

    createRoleRights(role);
  }

  public void updateRole(Role role) {
    try {
      roleRightsMapper.updateRole(role);
    } catch (DuplicateKeyException e) {
      throw new DataException("error.duplicate.role");
    }
    roleRightsMapper.deleteAllRightsForRole(role.getId());
    createRoleRights(role);
  }

  private void createRoleRights(Role role) {
    for (Right right : role.getRights()) {
      roleRightsMapper.createRoleRight(role, right.getName());
    }
  }

  public List<Role> getAllRoles() {
    return roleRightsMapper.getAllRoles();
  }

  public Role getRole(Long roleId) {
    return roleRightsMapper.getRole(roleId);
  }


  public List<Right> getAllRightsForUser(Long userId) {
    return roleRightsMapper.getAllRightsForUserById(userId);
  }

  public List<Right> getRightsForUserOnSupervisoryNodeAndProgram(Long userId, List<SupervisoryNode> supervisoryNodes, Program program) {
    return roleRightsMapper.getRightsForUserOnSupervisoryNodeAndProgram(userId, commaSeparator.commaSeparateIds(supervisoryNodes), program);
  }

  public List<Right> getRightsForUserOnHomeFacilityAndProgram(Long userId, Program program) {
    return roleRightsMapper.getRightsForUserOnHomeFacilityAndProgram(userId, program);
  }

  public RightType getRightTypeForRoleId(Long roleId) {
    return roleRightsMapper.getRightTypeForRoleId(roleId);
  }

  public List<Right> getRightsForUserAndWarehouse(Long userId, Long warehouseId) {
    return roleRightsMapper.getRightsForUserAndWarehouse(userId, warehouseId);
  }
}
