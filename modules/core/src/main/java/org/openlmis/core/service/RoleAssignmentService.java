/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.RoleAssignment;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.domain.User;
import org.openlmis.core.repository.RoleAssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@NoArgsConstructor
public class RoleAssignmentService {

  private RoleAssignmentRepository roleAssignmentRepository;


  @Autowired
  public RoleAssignmentService(RoleAssignmentRepository roleAssignmentRepository) {
    this.roleAssignmentRepository = roleAssignmentRepository;
  }

  public void saveHomeFacilityRoles(User user) {
    List<RoleAssignment> homeFacilityRoles = user.getHomeFacilityRoles();
    saveRoles(user, homeFacilityRoles);
  }

  public void saveSupervisoryRoles(User user) {
    List<RoleAssignment> supervisorRoles = user.getSupervisorRoles();
    saveRoles(user, supervisorRoles);
  }
  public void saveAdminRole(User user) {
    saveRoles(user, user.getAdminRole());
  }

  public void deleteAllRoleAssignmentsForUser(Integer id) {
    roleAssignmentRepository.deleteAllRoleAssignmentsForUser(id);
  }

  public List<RoleAssignment> getHomeFacilityRoles(Integer userId) {
    return roleAssignmentRepository.getHomeFacilityRoles(userId);
  }

  public List<RoleAssignment> getSupervisorRoles(Integer userId) {
    return roleAssignmentRepository.getSupervisorRoles(userId);
  }

  private void saveRoles(User user, List<RoleAssignment> roleAssignments) {
    if (roleAssignments == null) return;
    for (RoleAssignment roleAssignment : roleAssignments) {
      for (Integer role : roleAssignment.getRoleIds()) {
        SupervisoryNode node = roleAssignment.getSupervisoryNode();
        Integer supervisoryNodeId = null;
        if (node != null) supervisoryNodeId = node.getId();
        roleAssignmentRepository.insertRoleAssignment(user.getId(), roleAssignment.getProgramId(), supervisoryNodeId, role);
      }
    }
  }

  private void saveRoles(User user, RoleAssignment adminRole) {
    if (adminRole == null) return;
    for (Integer role : adminRole.getRoleIds()) {
      roleAssignmentRepository.insertRoleAssignment(user.getId(), null, null, role);
    }
  }

  public List<RoleAssignment> getHomeFacilityRolesForUserOnGivenProgramWithRights(Integer userId, Integer programId, Right... rights) {
    return roleAssignmentRepository.getHomeFacilityRolesForUserOnGivenProgramWithRights(userId, programId, rights);
  }

  public List<RoleAssignment> getRoleAssignments(Right right, Integer userId) {
    return roleAssignmentRepository.getRoleAssignmentsForUserWithRight(right, userId);
  }

  public RoleAssignment getAdminRole(Integer userId) {
    return roleAssignmentRepository.getAdminRole(userId);
  }


}
