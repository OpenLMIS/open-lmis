package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.RoleAssignment;
import org.openlmis.core.repository.mapper.RoleRightsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@NoArgsConstructor
public class RoleRightsRepository {

    RoleRightsMapper roleRightsMapper;

    @Autowired
    public RoleRightsRepository(RoleRightsMapper roleRightsMapper){
        this.roleRightsMapper = roleRightsMapper;
    }

    public List<RoleAssignment> getProgramWithGivenRightForAUser(Right right, String userName){
        return roleRightsMapper.getProgramWithGivenRightForAUser(right, userName);
    }
}
