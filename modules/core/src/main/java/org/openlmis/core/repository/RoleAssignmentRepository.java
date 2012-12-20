package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.RoleAssignment;
import org.openlmis.core.repository.mapper.RoleAssignmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@NoArgsConstructor
public class RoleAssignmentRepository {

    RoleAssignmentMapper roleAssignmentMapper;

    @Autowired
    public RoleAssignmentRepository(RoleAssignmentMapper roleAssignmentMapper){
        this.roleAssignmentMapper = roleAssignmentMapper;
    }

    public List<RoleAssignment> getRoleAssignments(Right right, String userName){
        return roleAssignmentMapper.getRoleAssignmentsWithGivenRightForAUser(right, userName);
    }

}
