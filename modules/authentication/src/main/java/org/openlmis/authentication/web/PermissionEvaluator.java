package org.openlmis.authentication.web;

import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.RoleAssignment;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.RoleAssignmentService;
import org.openlmis.core.service.RoleRightsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@NoArgsConstructor
public class PermissionEvaluator {

  private RoleRightsService roleRightService;
  private FacilityService facilityService;
  private RoleAssignmentService roleAssignmentService;

  @Autowired
  public PermissionEvaluator(RoleRightsService roleRightService, FacilityService facilityService, RoleAssignmentService roleAssignmentService) {
    this.roleRightService = roleRightService;
    this.facilityService = facilityService;
    this.roleAssignmentService = roleAssignmentService;
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

  public Boolean hasPermission(Integer userId, Integer facilityId, Integer programId, String commaSeparatedRights) {
    List<Right> rightsToCheck = getRightList(commaSeparatedRights);
    Facility homeFacility = facilityService.getHomeFacility(userId);

    if (homeFacility != null && homeFacility.getId().equals(facilityId)) {
      List<RoleAssignment> roleAssignments = roleAssignmentService.getHomeFacilityRolesForUserOnGivenProgramWithRights(userId, programId, rightsToCheck.toArray(new Right[rightsToCheck.size()]));
      return roleAssignments.size() > 0;
    }
    List<Facility> supervisedFacilities = facilityService.getUserSupervisedFacilities(userId, programId, rightsToCheck.toArray(new Right[rightsToCheck.size()]));
    return exists(facilityId, supervisedFacilities);
  }

  private boolean exists(final Integer facilityId, List<Facility> supervisedFacilities) {
    return CollectionUtils.exists(supervisedFacilities, new Predicate() {
      @Override
      public boolean evaluate(Object o) {
        Facility facility = (Facility) o;
        return facility.getId().equals(facilityId);
      }
    });
  }
}
