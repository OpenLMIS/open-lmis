/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
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
    return userRights.contains(right);
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
        RoleAssignment roleAssignment = (RoleAssignment) o;
        return (roleAssignment.getSupervisoryNode().getId().equals(rnr.getSupervisoryNodeId()) && roleAssignment.getProgramId().equals(rnr.getProgram().getId()));
      }
    });
  }

  public boolean hasPermission(Long userId, Right right) {
    return roleRightsService.getRights(userId).contains(right);
  }
}
