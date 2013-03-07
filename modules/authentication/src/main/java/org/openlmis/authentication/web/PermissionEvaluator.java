package org.openlmis.authentication.web;

import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.openlmis.core.domain.Right;
import org.openlmis.core.service.RoleRightsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@NoArgsConstructor
public class PermissionEvaluator {

  private RoleRightsService roleRightService;

  @Autowired
  public PermissionEvaluator(RoleRightsService roleRightService) {
    this.roleRightService = roleRightService;
  }

  public Boolean hasPermission(Integer userId, String commaSeparatedRights) {

    return CollectionUtils.containsAny(roleRightService.getRights(userId), getRightList(commaSeparatedRights));
  }

  private List<Right> getRightList(String commaSeparatedRights) {
    List<Right> rights = new ArrayList<>();
    String[] permissions = commaSeparatedRights.split(",");
    for (String permission : permissions) {
      rights.add(Right.valueOf(permission.trim()));
    }

    return rights;
  }

  public Boolean hasPermission(String userName, Integer facilityId, Integer programId, String commaSeparatedRights) {
    return null;  //To change body of created methods use File | Settings | File Templates.
  }
}
