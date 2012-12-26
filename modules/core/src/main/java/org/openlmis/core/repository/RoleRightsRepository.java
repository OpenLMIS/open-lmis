package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.Role;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.RoleMapper;
import org.openlmis.core.repository.mapper.RoleRightsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@NoArgsConstructor
public class RoleRightsRepository {

    private RoleRightsMapper roleRightsMapper;
    private RoleMapper roleMapper;

    @Autowired
    public RoleRightsRepository(RoleRightsMapper roleRightsMapper, RoleMapper roleMapper) {
        this.roleRightsMapper = roleRightsMapper;
        this.roleMapper = roleMapper;
    }

    public List<Right> getAllRightsForUser(String username) {
        return roleRightsMapper.getAllRightsForUser(username);
    }

    public void saveRole(Role role) {
        try {
            roleMapper.insert(role);
        } catch (DuplicateKeyException e) {
            throw new DataException("Duplicate Role found");
        }

        for (Right right : role.getRights()) {
            roleRightsMapper.createRoleRight(role.getId(), right);
        }
    }
}
