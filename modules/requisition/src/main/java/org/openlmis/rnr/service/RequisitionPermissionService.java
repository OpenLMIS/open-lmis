/*
 *
 *  * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *  *
 *  * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package org.openlmis.rnr.service;

import org.apache.commons.collections.Predicate;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.RoleAssignment;
import org.openlmis.core.service.RoleAssignmentService;
import org.openlmis.core.service.RoleRightsService;
import org.openlmis.rnr.domain.Rnr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.apache.commons.collections.CollectionUtils.containsAny;
import static org.apache.commons.collections.CollectionUtils.exists;
import static org.openlmis.core.domain.Right.*;
import static org.openlmis.rnr.domain.RnrStatus.*;

@Service
public class RequisitionPermissionService {


  @Autowired
  private RoleRightsService roleRightsService;
  @Autowired
  private RoleAssignmentService roleAssignmentService;

  public Boolean hasPermission(Long userId, Facility facility, Program program, Right right) {
    Set<Right> userRights = roleRightsService.getRightsForUserAndFacilityProgram(userId, facility, program);
    return containsAny(userRights, asList(right));
  }

  public Boolean hasPermission(Long userId, Rnr rnr, Right right) {
    if (right.equals(APPROVE_REQUISITION))
      return hasPermissionToApprove(userId, rnr);

    return hasPermission(userId, rnr.getFacility(), rnr.getProgram(), right);
  }

  public boolean hasPermissionToSave(Long userId, Rnr rnr) {
    return (rnr.getStatus() == INITIATED && hasPermission(userId, rnr, CREATE_REQUISITION)) ||
      (rnr.getStatus() == SUBMITTED && hasPermission(userId, rnr, AUTHORIZE_REQUISITION)) ||
      (rnr.getStatus() == AUTHORIZED && hasPermissionToApprove(userId, rnr)) ||
      (rnr.getStatus() == IN_APPROVAL && hasPermissionToApprove(userId, rnr));
  }

  private boolean hasPermissionToApprove(Long userId, final Rnr rnr) {
    List<RoleAssignment> assignments = roleAssignmentService.getRoleAssignments(APPROVE_REQUISITION, userId);

    return exists(assignments, new Predicate() {
      @Override
      public boolean evaluate(Object o) {
        return (((RoleAssignment) o).getSupervisoryNode().getId().equals(rnr.getSupervisoryNodeId()));
      }
    });
  }

  public boolean hasPermission(Long userId, Right right) {
    return roleRightsService.getRights(userId).contains(right);
  }
}
