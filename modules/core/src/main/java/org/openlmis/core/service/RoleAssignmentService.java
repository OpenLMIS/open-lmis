/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.RoleAssignment;
import org.openlmis.core.domain.User;
import org.openlmis.core.repository.RoleAssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Arrays.asList;

@Service
@NoArgsConstructor
public class RoleAssignmentService {

  private RoleAssignmentRepository roleAssignmentRepository;


  @Autowired
  public RoleAssignmentService(RoleAssignmentRepository roleAssignmentRepository) {
    this.roleAssignmentRepository = roleAssignmentRepository;
  }

  public void deleteAllRoleAssignmentsForUser(Long id) {
    roleAssignmentRepository.deleteAllRoleAssignmentsForUser(id);
  }

  public List<RoleAssignment> getHomeFacilityRoles(Long userId) {
    return roleAssignmentRepository.getHomeFacilityRoles(userId);
  }

  public RoleAssignment getAdminRole(Long userId) {
    return roleAssignmentRepository.getAdminRole(userId);
  }

  public List<RoleAssignment> getSupervisorRoles(Long userId) {
    return roleAssignmentRepository.getSupervisorRoles(userId);
  }

  public List<RoleAssignment> getAllocationRoles(Long userId) {
    return roleAssignmentRepository.getAllocationRoles(userId);
  }

  public List<RoleAssignment> getHomeFacilityRolesForUserOnGivenProgramWithRights(Long userId, Long programId, Right... rights) {
    return roleAssignmentRepository.getHomeFacilityRolesForUserOnGivenProgramWithRights(userId, programId, rights);
  }


  public List<RoleAssignment> getRoleAssignments(Right right, Long userId) {
    return roleAssignmentRepository.getRoleAssignmentsForUserWithRight(right, userId);
  }

  public void saveRolesForUser(User user) {
    roleAssignmentRepository.deleteAllRoleAssignmentsForUser(user.getId());
    roleAssignmentRepository.insert(user.getHomeFacilityRoles(), user.getId());
    roleAssignmentRepository.insert(user.getSupervisorRoles(), user.getId());
    roleAssignmentRepository.insert(user.getAllocationRoles(), user.getId());
    roleAssignmentRepository.insert(asList(user.getAdminRole()), user.getId());
  }
}
