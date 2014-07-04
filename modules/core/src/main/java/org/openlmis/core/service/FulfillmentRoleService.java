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

package org.openlmis.core.service;

import org.openlmis.core.domain.FulfillmentRoleAssignment;
import org.openlmis.core.domain.User;
import org.openlmis.core.repository.FulfillmentRoleAssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Exposes the services for handling FulfillmentRole entity.
 */

@Service
public class FulfillmentRoleService {

  @Autowired
  private FulfillmentRoleAssignmentRepository fulfillmentRoleAssignmentRepository;

  public List<FulfillmentRoleAssignment> getRolesForUser(Long userId) {
    return fulfillmentRoleAssignmentRepository.getFulfillmentRolesForUser(userId);
  }

  public void saveFulfillmentRoles(User user) {
    fulfillmentRoleAssignmentRepository.insertFulfillmentRoles(user);
  }

  public List<FulfillmentRoleAssignment> getRolesWithRight(Long userId, String rightName) {
    return fulfillmentRoleAssignmentRepository.getRolesWithRight(userId, rightName);
  }
}
