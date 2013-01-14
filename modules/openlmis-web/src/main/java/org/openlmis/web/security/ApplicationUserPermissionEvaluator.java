package org.openlmis.web.security;

import org.apache.commons.collections.CollectionUtils;
import org.openlmis.core.domain.Right;
import org.openlmis.core.service.RoleRightsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ApplicationUserPermissionEvaluator implements PermissionEvaluator {

  private RoleRightsService roleRightService;

  @Autowired
  public ApplicationUserPermissionEvaluator(RoleRightsService roleRightService) {
    this.roleRightService = roleRightService;
  }

  @Override
  public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permissions) {
    final List<Right> rightsToCheck = getRightList((String) permissions);

    String userName = authentication.getName();
    return CollectionUtils.containsAny(roleRightService.getRights(userName), rightsToCheck);
  }

  private List<Right> getRightList(String permissionCommaSeparated) {
    List<Right> rights = new ArrayList<>();
    String[] permissions = permissionCommaSeparated.split(",");
    for(String permission: permissions){
      rights.add(Right.valueOf(permission.trim()));
    }

    return rights;
  }

  @Override
  public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
    return false;
  }
}
