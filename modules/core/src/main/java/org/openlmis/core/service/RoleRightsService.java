package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.*;
import org.openlmis.core.repository.RoleRightsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@NoArgsConstructor
public class RoleRightsService {

  private RoleRightsRepository roleRightsRepository;

  @Autowired
  public RoleRightsService(RoleRightsRepository roleRightsRepository) {
    this.roleRightsRepository = roleRightsRepository;
  }

  public List<RoleAssignment> getProgramWithGivenRightForAUser(Right right, String userName) {
    return roleRightsRepository.getProgramWithGivenRightForAUser(right, userName);
  }

  public List<Right> getAllRightsForUser(String username) {
    return roleRightsRepository.getAllRightsForUser(username);
  }
}
