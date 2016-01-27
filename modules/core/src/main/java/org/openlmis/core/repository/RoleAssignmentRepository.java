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
import org.apache.commons.collections.Closure;
import org.openlmis.core.domain.RoleAssignment;
import org.openlmis.core.repository.mapper.RoleAssignmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.apache.commons.collections.CollectionUtils.forAllDo;
import static org.openlmis.core.domain.RightName.commaSeparateRightNames;

/**
 * RoleAssignmentRepository is Repository class for RoleAssignment related database operations.
 */

@Repository
@NoArgsConstructor
public class RoleAssignmentRepository {

  RoleAssignmentMapper mapper;

  @Autowired
  public RoleAssignmentRepository(RoleAssignmentMapper roleAssignmentMapper) {
    this.mapper = roleAssignmentMapper;
  }

  public List<RoleAssignment> getRoleAssignmentsForUserWithRight(String rightName, Long userId) {
    return mapper.getRoleAssignmentsWithGivenRightForAUser(rightName, userId);
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

  public List<RoleAssignment> getHomeFacilityRolesForUserOnGivenProgramWithRights(Long userId, Long programId, String... rightNames) {
    return mapper.getHomeFacilityRolesForUserOnGivenProgramWithRights(userId, programId, commaSeparateRightNames(rightNames));
  }

  public RoleAssignment getAdminRole(Long userId) {
    return mapper.getAdminRole(userId);
  }

  public RoleAssignment getReportRole(Long userId) {
      return mapper.getReportRole(userId);
  }

  public void insert(List<RoleAssignment> roleAssignments, final Long userId) {
    if (roleAssignments == null) return;

    for (final RoleAssignment roleAssignment : roleAssignments) {
      if (roleAssignment == null) continue;
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

  public List<RoleAssignment> getAllocationRoles(Long userId) {
    return mapper.getAllocationRoles(userId);
  }

  public RoleAssignment getReportingRole(Long userId) {
    return mapper.getReportingRole(userId);
  }
}
