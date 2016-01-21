/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
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
 * RoleAssignment represents a Role assigned to the user which has associated rights with it.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = NON_NULL)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class RoleAssignment extends BaseModel {
  private Long userId;
  private List<Long> roleIds = new ArrayList<>();

  private Long programId;
  private SupervisoryNode supervisoryNode;
  private DeliveryZone deliveryZone;

  public RoleAssignment(Long userId, Long roleId, Long programId, SupervisoryNode supervisoryNode) {
    this.userId = userId;
    this.roleIds.add(roleId);
    this.programId = programId;
    this.supervisoryNode = supervisoryNode;
  }

  public RoleAssignment(Long userId, List<Long> roleIds, Long programId, SupervisoryNode supervisoryNode) {
    this.userId = userId;
    this.roleIds = roleIds;
    this.programId = programId;
    this.supervisoryNode = supervisoryNode;
  }

  @SuppressWarnings("unused (used for mybatis mapping)")
  public void setRoleId(Long roleId) {
    this.roleIds.add(roleId);
  }

  @SuppressWarnings("unused (used for mybatis mapping)")
  public void setRoleIdsAsString(String roleIds) {
    parseRoleIdsIntoList(roleIds);
  }

  private void parseRoleIdsIntoList(String roleIds) {
    roleIds = roleIds.replace("{", "").replace("}", "");
    String[] roleIdsArray = roleIds.split(",");
    for (String roleId : roleIdsArray) {
      Long id = Long.parseLong(roleId);
      if (!this.roleIds.contains(id))
        this.roleIds.add(id);
    }
  }

}
