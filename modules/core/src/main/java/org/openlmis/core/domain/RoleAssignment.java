/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.ArrayList;
import java.util.List;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_NULL;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = NON_NULL)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class RoleAssignment extends BaseModel{
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
      this.roleIds.add(Long.parseLong(roleId));
    }
  }

}
