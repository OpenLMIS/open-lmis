/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.RoleAssignment;
import org.openlmis.core.repository.mapper.RoleAssignmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.apache.commons.collections.CollectionUtils.forAllDo;
import static org.openlmis.core.domain.Right.commaSeparateRightNames;

@Repository
@NoArgsConstructor
public class RoleAssignmentRepository {

  RoleAssignmentMapper mapper;

  @Autowired
  public RoleAssignmentRepository(RoleAssignmentMapper roleAssignmentMapper) {
    this.mapper = roleAssignmentMapper;
  }

  public List<RoleAssignment> getRoleAssignmentsForUserWithRight(Right right, Long userId) {
    return mapper.getRoleAssignmentsWithGivenRightForAUser(right, userId);
  }

  public void deleteAllRoleAssignmentsForUser(Long id) {
    mapper.deleteAllRoleAssignmentsForUser(id);
  }

  public List<RoleAssignment> getSupervisorRoles(Long userId) {
    return mapper.getSupervisorRoles(userId);
  }

  public List<RoleAssignment> getHomeFacilityRoles(Long userId) {
    return mapper.getHomeFacilityRoles(userId);
  }

  public List<RoleAssignment> getHomeFacilityRolesForUserOnGivenProgramWithRights(Long userId, Long programId, Right... rights) {
    return mapper.getHomeFacilityRolesForUserOnGivenProgramWithRights(userId, programId, commaSeparateRightNames(rights));
  }

  public RoleAssignment getAdminRole(Long userId) {
    return mapper.getAdminRole(userId);
  }

  public void insert(List<RoleAssignment> roleAssignments, final Long userId) {
    if(roleAssignments == null) return;

    for (final RoleAssignment roleAssignment : roleAssignments) {
      if(roleAssignment == null) continue;
      forAllDo(roleAssignment.getRoleIds(), new Closure() {
        @Override
        public void execute(Object o) {
          final Long roleId = (Long) o;
          mapper.insert(userId, roleAssignment.getProgramId(),
            roleAssignment.getSupervisoryNode(), roleAssignment.getDeliveryZone(), roleId);
        }
      });
    }
  }
}
