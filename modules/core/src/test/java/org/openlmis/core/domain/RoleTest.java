package org.openlmis.core.domain;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;

import static java.util.Arrays.asList;
import static org.openlmis.core.domain.Right.APPROVE_REQUISITION;

public class RoleTest {

    Role role;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        role = new Role("role test", " description");
    }

    @Test
    public void shouldGiveErrorIfRoleDoesNotHaveAnyRights() throws Exception {
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Role can not be created without any rights assigned to it.");
        role.validate();
    }

    @Test
    public void shouldGiveErrorIfRoleHasEmptyRightsList() {
        role.setRights(new ArrayList<Right>());
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Role can not be created without any rights assigned to it.");
        role.validate();
    }


    @Test
    public void shouldGiveErrorIfRoleDoesNotHaveAnyName() throws Exception {
        Role role = new Role("", " description");
        role.setRights(asList(APPROVE_REQUISITION));
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Role can not be created without name.");
        role.validate();

        role.setName(null);
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Role can not be created without name.");
        role.validate();
    }
}
