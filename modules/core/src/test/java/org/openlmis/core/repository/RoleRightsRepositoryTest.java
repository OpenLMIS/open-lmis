package org.openlmis.core.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Role;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.RoleMapper;
import org.openlmis.core.repository.mapper.RoleRightsMapper;
import org.springframework.dao.DuplicateKeyException;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.openlmis.core.domain.Right.APPROVE_REQUISITION;
import static org.openlmis.core.domain.Right.CREATE_REQUISITION;

@RunWith(MockitoJUnitRunner.class)
public class RoleRightsRepositoryTest {

    Role role;
    @Mock
    RoleRightsMapper roleRightsMapper;

    @Mock
    RoleMapper roleMapper;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        role = new Role("role name", "role description");

    }

    @Test
    public void shouldSaveRoleWithMappings() throws Exception {
        role.setRights(asList(APPROVE_REQUISITION, CREATE_REQUISITION));
        role.setId(1);
        new RoleRightsRepository(roleRightsMapper, roleMapper).saveRole(role);

        verify(roleMapper).insert(role);
        verify(roleRightsMapper).createRoleRight(1, APPROVE_REQUISITION);
        verify(roleRightsMapper).createRoleRight(1, CREATE_REQUISITION);
    }

    @Test
    public void shouldNotSaveDuplicateRole() throws Exception {
        doThrow(DuplicateKeyException.class).when(roleMapper).insert(role);

        expectedEx.expect(DataException.class);
        expectedEx.expectMessage("Duplicate Role found");

        new RoleRightsRepository(roleRightsMapper, roleMapper).saveRole(role);
    }
}
