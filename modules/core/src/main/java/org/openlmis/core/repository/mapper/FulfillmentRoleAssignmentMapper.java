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

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.core.domain.FulfillmentRoleAssignment;
import org.openlmis.core.domain.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FulfillmentRoleAssignmentMapper {

  @Select({"SELECT userId, facilityId, array_agg(roleId) as roleAsString FROM fulfillment_role_assignments WHERE userId = #{userId} GROUP BY userId,facilityId"})
  List<FulfillmentRoleAssignment> getFulfillmentRolesForUser(Long userId);

  @Insert({"INSERT INTO fulfillment_role_assignments(userId, facilityId, roleId) VALUES(#{userId}, #{facilityId}, #{roleId})"})
  void insertFulfillmentRole(@Param("userId") Long userId,
                             @Param("facilityId") Long facilityId,
                             @Param("roleId") Long roleId);

  @Delete({"DELETE FROM fulfillment_role_assignments WHERE userId = #{id}"})
  void deleteAllFulfillmentRoles(User user);
}
