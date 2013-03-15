package org.openlmis.rnr.service;

import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.RoleAssignment;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.RoleAssignmentService;
import org.openlmis.rnr.domain.Rnr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.openlmis.core.domain.Right.*;
import static org.openlmis.rnr.domain.RnrStatus.*;

@Service
@NoArgsConstructor
public class RequisitionPermissionService {


  private FacilityService facilityService;
  private RoleAssignmentService roleAssignmentService;

  @Autowired
  public RequisitionPermissionService(FacilityService facilityService, RoleAssignmentService roleAssignmentService) {
    this.facilityService = facilityService;
    this.roleAssignmentService = roleAssignmentService;
  }

  public Boolean hasPermission(Integer userId, Integer facilityId, Integer programId, Right... rights) {
    boolean permitted = hasHomeFacilityPermission(userId, facilityId, programId, rights);
    if (!permitted) {
      return hasSupervisoryPermission(userId, facilityId, programId, rights);
    }
    return permitted;
  }

  private boolean hasSupervisoryPermission(Integer userId, Integer facilityId, Integer programId, Right... rights) {
    List<Facility> supervisedFacilities = facilityService.getUserSupervisedFacilities(userId, programId, rights);
    return exists(facilityId, supervisedFacilities);
  }

  private boolean hasHomeFacilityPermission(Integer userId, Integer facilityId, Integer programId, Right... rights) {
    boolean permitted = false;
    Facility homeFacility = facilityService.getHomeFacility(userId);

    if (homeFacility != null && homeFacility.getId().equals(facilityId)) {
      List<RoleAssignment> roleAssignments = roleAssignmentService.getHomeFacilityRolesForUserOnGivenProgramWithRights(userId, programId, rights);
      permitted = roleAssignments.size() > 0;
    }
    return permitted;
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

  public Boolean hasPermission(Integer userId, Rnr rnr, Right... rights) {
    return hasPermission(userId, rnr.getFacility().getId(), rnr.getProgram().getId(), rights);
  }


  public boolean hasPermissionToSave(Integer userId, Rnr rnr) {
    return (rnr.getStatus() == INITIATED && hasPermission(userId, rnr, CREATE_REQUISITION)) ||
        (rnr.getStatus() == SUBMITTED && hasPermission(userId, rnr, AUTHORIZE_REQUISITION)) ||
        (rnr.getStatus() == AUTHORIZED && hasPermission(userId, rnr, APPROVE_REQUISITION)) ||
        (rnr.getStatus() == IN_APPROVAL && hasPermission(userId, rnr, APPROVE_REQUISITION));
  }

  public boolean hasPermissionToApprove(Integer userId, final Rnr rnr) {
    List<RoleAssignment> assignments = roleAssignmentService.getRoleAssignments(APPROVE_REQUISITION, userId);

    return CollectionUtils.exists(assignments, new Predicate() {
      @Override
      public boolean evaluate(Object o) {
        final RoleAssignment o1 = (RoleAssignment) o;
        return (o1.getSupervisoryNode().getId().equals(rnr.getSupervisoryNodeId()));
      }
    });
  }
}
