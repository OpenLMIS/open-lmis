/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 *
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.core.repository;

import org.apache.commons.collections.Closure;
import org.openlmis.core.domain.FulfillmentRoleAssignment;
import org.openlmis.core.domain.User;
import org.openlmis.core.repository.mapper.FulfillmentRoleAssignmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.apache.commons.collections.CollectionUtils.forAllDo;

/**
 * FulfillmentRoleAssignmentRepository is repository class for Shipment RoleAssignments related database operations.
 */

@Repository
public class FulfillmentRoleAssignmentRepository {

  @Autowired
  private FulfillmentRoleAssignmentMapper fulfillmentRoleAssignmentMapper;

  public List<FulfillmentRoleAssignment> getFulfillmentRolesForUser(Long userId) {
    return fulfillmentRoleAssignmentMapper.getFulfillmentRolesForUser(userId);
  }

  public void insertFulfillmentRoles(final User user) {
    if (user.getFulfillmentRoles() == null) return;
    fulfillmentRoleAssignmentMapper.deleteAllFulfillmentRoles(user);

    for (final FulfillmentRoleAssignment fulfillmentRoleAssignment : user.getFulfillmentRoles()) {
      if (fulfillmentRoleAssignment == null) continue;
      forAllDo(fulfillmentRoleAssignment.getRoleIds(), new Closure() {
        @Override
        public void execute(Object o) {
          final Long roleId = (Long) o;
          fulfillmentRoleAssignmentMapper.insertFulfillmentRole(user, fulfillmentRoleAssignment.getFacilityId(), roleId);
        }
      });
    }
  }

  public List<FulfillmentRoleAssignment> getRolesWithRight(Long userId, String rightName) {
    return fulfillmentRoleAssignmentMapper.getRolesWithRight(userId, rightName);
  }
}
