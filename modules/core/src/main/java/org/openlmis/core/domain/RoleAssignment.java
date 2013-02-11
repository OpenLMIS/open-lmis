package org.openlmis.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class RoleAssignment {
  private Integer userId;
  private List<Integer> roleIds = new ArrayList<>();

  private Integer programId;
  private SupervisoryNode supervisoryNode;

  public RoleAssignment(Integer userId, Integer roleId, Integer programId, SupervisoryNode supervisoryNode) {
    this.userId = userId;
    this.setRoleId(roleId);
    this.programId = programId;
    this.supervisoryNode = supervisoryNode;
  }

  @SuppressWarnings("unused (used for mybatis mapping)")
  public void setRoleId(Integer roleId) {
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
      this.roleIds.add(Integer.parseInt(roleId));
    }
  }

}
