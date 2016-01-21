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

package org.openlmis.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_NULL;

/**
 * FulfillmentRoleAssignment represents the roles assigned to the user for functionality related to fulfillment process.
 */
@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = NON_NULL)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class FulfillmentRoleAssignment extends BaseModel {
  private Long userId;
  private Long facilityId;
  private List<Long> roleIds = new ArrayList<>();

  @SuppressWarnings("unused (used for mybatis mapping)")
  public void setRoleAsString(String roleIds) {
    parseRoleIdsIntoList(roleIds);
  }

  private void parseRoleIdsIntoList(String roleIds) {
    roleIds = roleIds.replace("{", "").replace("}", "");
    String[] roleIdsArray = roleIds.split(",");
    for (String roleId : roleIdsArray) {
      this.roleIds.add(Long.parseLong(roleId));
    }
  }
}
