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

import org.openlmis.core.domain.ShipmentRoleAssignment;
import org.openlmis.core.repository.mapper.ShipmentRoleAssignmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ShipmentRoleRepository {

  @Autowired
  private ShipmentRoleAssignmentMapper shipmentRoleAssignmentMapper;

  public List<ShipmentRoleAssignment> getShipmentRolesForUser(Long userId) {
    return shipmentRoleAssignmentMapper.getShipmentRolesForUser(userId);
  }
}
