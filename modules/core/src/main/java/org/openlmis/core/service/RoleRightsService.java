package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.Role;
import org.openlmis.core.domain.RoleAssignment;
import org.openlmis.core.repository.RoleAssignmentRepository;
import org.openlmis.core.repository.RoleRightsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.sort;

@Service
@NoArgsConstructor
public class RoleRightsService {

    private RoleRightsRepository roleRightsRepository;
    private RoleAssignmentRepository roleAssignmentRepository;

    @Autowired
    public RoleRightsService(RoleRightsRepository roleRightsRepository, RoleAssignmentRepository roleAssignmentRepository) {
        this.roleRightsRepository = roleRightsRepository;
        this.roleAssignmentRepository = roleAssignmentRepository;
    }

    public List<RoleAssignment> getRoleAssignments(Right right, int userId) {
        return roleAssignmentRepository.getRoleAssignmentsForUserWithRight(right, userId);
    }

    public List<Right> getRights(String username) {
        return roleRightsRepository.getAllRightsForUser(username);
    }

    public List<Right> getAllRights() {
        List<Right> rights = asList(Right.values());
        sort(rights, new Comparator<Right>() {
            @Override
            public int compare(Right right1, Right right2) {
                return right1.getRightName().compareTo(right2.getRightName());
            }
        });
        return rights;
    }

    public void saveRole(Role role) {
        role.validate();
       roleRightsRepository.saveRole(role);
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

  public boolean userHasRightForFacilityProgram(Integer facilityId, Integer programId, String user, Right right) {
    return false;
  }

  public List<Right> getRights(Integer userId) {
    return roleRightsRepository.getAllRightsForUser(userId);
  }
}
