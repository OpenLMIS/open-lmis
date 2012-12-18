package org.openlmis.web.security;

import org.openlmis.core.domain.Right;
import org.openlmis.core.service.RoleRightsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import java.io.Serializable;

public class ApplicationUserPermissionEvaluator implements PermissionEvaluator {

  @Autowired
  private RoleRightsService roleRightService;

  @Override
  public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
    Right requiredRight = Right.valueOf((String) permission);
    return roleRightService.getRights(authentication.getName()).contains(requiredRight);
  }

  @Override
  public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
    return false;
  }
}
