/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.*;
import org.openlmis.core.repository.RoleRightsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.util.Arrays.asList;

@Service
@NoArgsConstructor
public class RoleRightsService {

  private RoleRightsRepository roleRightsRepository;
  private SupervisoryNodeService supervisoryNodeService;
  private FacilityService facilityService;

  @Autowired
  public RoleRightsService(RoleRightsRepository roleRightsRepository, SupervisoryNodeService supervisoryNodeService, FacilityService facilityService) {
    this.roleRightsRepository = roleRightsRepository;
    this.supervisoryNodeService = supervisoryNodeService;
    this.facilityService = facilityService;
  }

  public Set<Right> getRights(String username) {
    return roleRightsRepository.getAllRightsForUser(username);
  }

  public Set<Right> getAllRights() {
    TreeSet<Right> rights = new TreeSet<>(new Right.RightComparator());
    rights.addAll(asList(Right.values()));
    return rights;
  }

  public void saveRole(Role role) {
    role.validate();
    roleRightsRepository.createRole(role);
  }

  public List<Role> getAllRoles() {
    return roleRightsRepository.getAllRoles();
  }

  public Role getRole(Long id) {
    return roleRightsRepository.getRole(id);
  }

  public void updateRole(Role role) {
    roleRightsRepository.updateRole(role);
  }

  public Set<Right> getRights(Long userId) {
    return roleRightsRepository.getAllRightsForUser(userId);
  }

  public Set<Right> getRightsForUserAndFacilityProgram(Long userId, Facility facility, Program program) {
    Set<Right> result = new HashSet<>();
    result.addAll(getHomeFacilityRights(userId, facility, program));
    result.addAll(getSupervisoryRights(userId, facility, program));
    return result;
  }

  private List<Right> getSupervisoryRights(Long userId, Facility facility, Program program) {
    SupervisoryNode supervisoryNode = supervisoryNodeService.getFor(facility, program);
    if (supervisoryNode != null) {
      List<SupervisoryNode> supervisoryNodes = supervisoryNodeService.getAllParentSupervisoryNodesInHierarchy(supervisoryNode);
      return roleRightsRepository.getRightsForUserOnSupervisoryNodeAndProgram(userId, supervisoryNodes, program);
    }
    return Collections.emptyList();
  }

  private List<Right> getHomeFacilityRights(Long userId, Facility facility, Program program) {
    Facility homeFacility = facilityService.getHomeFacility(userId);
    if (homeFacility != null && homeFacility.getId().equals(facility.getId())) {
      return roleRightsRepository.getRightsForUserOnHomeFacilityAndProgram(userId, program);
    }
    return Collections.emptyList();
  }
}
