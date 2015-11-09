/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.*;
import org.openlmis.core.repository.RoleRightsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Exposes the services for handling Role and Right entities.
 */

@Service
@NoArgsConstructor
public class RoleRightsService {

  private RoleRightsRepository roleRightsRepository;
  private SupervisoryNodeService supervisoryNodeService;
  private FacilityService facilityService;
  private ProgramService programService;
  private ProgramProductService programProductService;

  @Autowired
  public RoleRightsService(RoleRightsRepository roleRightsRepository,
                           SupervisoryNodeService supervisoryNodeService,
                           FacilityService facilityService,
                           ProgramService programService,
                           ProgramProductService programProductService) {
    this.roleRightsRepository = roleRightsRepository;
    this.supervisoryNodeService = supervisoryNodeService;
    this.facilityService = facilityService;
    this.programService = programService;
    this.programProductService = programProductService;
  }

  @Transactional
  public void saveRole(Role role) {
    role.validate();
    roleRightsRepository.createRole(role);
  }

  public Map<String, List<Role>> getAllRolesMap() {
    Map<String, List<Role>> rolesMap = new HashMap<>();
    for (Role role : roleRightsRepository.getAllRoles()) {
      RightType rightType = getRightTypeForRoleId(role.getId());
      if (rightType != null) {
        String rightName = rightType.name();
        List<Role> roles = rolesMap.get(rightName);
        if (roles == null) {
          roles = new ArrayList<>();
          rolesMap.put(rightName, roles);
        }
        roles.add(role);
      }
    }
    return rolesMap;
  }

  public Role getRole(Long id) {
    return roleRightsRepository.getRole(id);
  }
  public List<Role> getAllRoles(){return roleRightsRepository.getAllRoles();}
  public void updateRole(Role role) {
    roleRightsRepository.updateRole(role);
  }

  public List<Right> getRights(Long userId) {
    return roleRightsRepository.getAllRightsForUser(userId);
  }

  public List<Right> getRightsForUserAndFacilityProgram(Long userId, Facility facility, Program program) {
    List<Right> result = new ArrayList<>();
    result.addAll(getHomeFacilityRights(userId, facility, program));
    result.addAll(getSupervisoryRights(userId, facility, program));
    return result;
  }

  public List<Right> getRightsForUserAndWarehouse(Long userId, Long warehouseId) {
    return roleRightsRepository.getRightsForUserAndWarehouse(userId, warehouseId);
  }


  public List<Right> getRightsForUserFacilityAndProductCode(Long userId, Long facilityId, String productCode)
  {
    Facility facility = facilityService.getById(facilityId);

    // Get programs by product code, through programProducts
    List<ProgramProduct> programProducts = programProductService.getByProductCode(productCode);
    List<Program> programs = new ArrayList<>();
    for (ProgramProduct programProduct : programProducts) {
      Program program = programService.getByCode(programProduct.getProgram().getCode());
      programs.add(program);
    }

    // For each program, get rights
    List<Right> rights = new ArrayList<>();
    for (Program program : programs) {
      rights.addAll(getRightsForUserAndFacilityProgram(userId, facility, program));
    }

    return rights;
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

  public RightType getRightTypeForRoleId(Long roleId) {
    return roleRightsRepository.getRightTypeForRoleId(roleId);
  }
}
