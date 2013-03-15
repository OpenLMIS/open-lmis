package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.*;
import org.openlmis.core.repository.RoleRightsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
    return new LinkedHashSet<>(asList(Right.values()));
  }

  public void saveRole(Role role) {
    role.validate();
    roleRightsRepository.createRole(role);
  }

  public List<Role> getAllRoles() {
    return roleRightsRepository.getAllRoles();
  }

  public Role getRole(int id) {
    return roleRightsRepository.getRole(id);
  }

  public void updateRole(Role role) {
    roleRightsRepository.updateRole(role);
  }

  public Set<Right> getRights(Integer userId) {
    return roleRightsRepository.getAllRightsForUser(userId);
  }

  public List<Right> getRightsForUserAndFacilityProgram(Integer userId, Facility facility, Program program) {
    List<Right> result = new ArrayList<>();
    Facility homeFacility = facilityService.getHomeFacility(userId);
    if (homeFacility!=null && homeFacility.getId().equals(facility.getId()))
      result.addAll(roleRightsRepository.getRightsForUserOnHomeFacilityAndProgram(userId, program));

    SupervisoryNode supervisoryNode = supervisoryNodeService.getFor(facility, program);
    if (supervisoryNode != null)
      result.addAll(roleRightsRepository.getRightsForUserOnSupervisoryNodeAndProgram(userId, supervisoryNode, program));
    return result;
  }
}
