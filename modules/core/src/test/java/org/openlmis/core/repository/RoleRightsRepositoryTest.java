package org.openlmis.core.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Role;
import org.openlmis.core.repository.mapper.RoleMapper;
import org.openlmis.core.repository.mapper.RoleRightsMapper;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.core.domain.Right.APPROVE_REQUISITION;
import static org.openlmis.core.domain.Right.CREATE_REQUISITION;

@RunWith(MockitoJUnitRunner.class)
public class RoleRightsRepositoryTest {

    @Mock
    RoleRightsMapper roleRightsMapper;

    @Mock
    RoleMapper roleMapper;

    @Test
    public void shouldSaveRoleWithMappings() throws Exception {
        Role role = new Role("role name", "role description");
        role.setRights(asList(APPROVE_REQUISITION,CREATE_REQUISITION));
        when(roleMapper.insert(role)).thenReturn(1);

        new RoleRightsRepository(roleRightsMapper, roleMapper).saveRole(role);

        verify(roleMapper).insert(role);
        verify(roleRightsMapper).createRoleRight(1, APPROVE_REQUISITION);
        verify(roleRightsMapper).createRoleRight(1, CREATE_REQUISITION);
    }


}
