package org.openlmis.web.security;

import org.openlmis.core.domain.Right;
import org.openlmis.core.service.RoleRightsService;
import org.openlmis.rnr.domain.Rnr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import java.io.Serializable;

import static org.openlmis.core.domain.Right.AUTHORIZE_REQUISITION;
import static org.openlmis.core.domain.Right.CREATE_REQUISITION;

public class ApplicationUserPermissionEvaluator implements PermissionEvaluator {

  private RoleRightsService roleRightService;

  @Autowired
  public ApplicationUserPermissionEvaluator(RoleRightsService roleRightService) {
    this.roleRightService = roleRightService;
  }

  @Override
  public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
    Right requiredRight = Right.valueOf((String) permission);
    String user = authentication.getName();
//    if((requiredRight.equals(CREATE_REQUISITION)) || (requiredRight.equals(AUTHORIZE_REQUISITION))){
//      Rnr requisition = (Rnr) targetDomainObject;
//      return roleRightService.userHasRightForFacilityProgram(requisition.getFacilityId(), requisition.getProgramId(), user, requiredRight);
//    }
    return roleRightService.getRights(user).contains(requiredRight);
  }

  @Override
  public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
    return false;
  }
}
