package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.RoleAssignment;
import org.openlmis.core.repository.RoleAssignmentRepository;
import org.openlmis.core.repository.RoleRightsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Arrays.asList;

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

    public List<RoleAssignment> getRoleAssignments(Right right, String userName) {
        return roleAssignmentRepository.getRoleAssignments(right, userName);
    }

    public List<Right> getRights(String username) {
        return roleRightsRepository.getAllRightsForUser(username);
    }

    public List<Right> getAllRights() {
        return asList(Right.values());
    }
}
