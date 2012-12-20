package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.Role;
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

  public List<Right> getAllRightsForUser(String username) {
    return roleRightsMapper.getAllRightsForUser(username);
  }

    public void saveRole(Role role) {
        //To change body of created methods use File | Settings | File Templates.
    }
}
