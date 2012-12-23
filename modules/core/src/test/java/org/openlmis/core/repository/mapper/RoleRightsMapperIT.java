package org.openlmis.core.repository.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.core.builder.ProgramBuilder.programCode;
import static org.openlmis.core.domain.Right.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-core.xml")
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class RoleRightsMapperIT {

    @Autowired
    UserMapper userMapper;
    @Autowired
    ProgramMapper programMapper;
    @Autowired
    ProgramSupportedMapper programSupportedMapper;
    @Autowired
    RoleRightsMapper roleRightsMapper;
    @Autowired
    RoleAssignmentMapper roleAssignmentMapper;
    @Autowired
    RoleMapper roleMapper;

    @Test
    public void shouldSetupRightsForAdminRole() {
        List<Right> adminRights = roleRightsMapper.getAllRightsForUser("Admin123");
        assertEquals(Right.CONFIGURE_RNR, adminRights.get(0));
        assertEquals(Right.MANAGE_FACILITY, adminRights.get(1));
        assertEquals(Right.UPLOADS, adminRights.get(2));
    }

    @Test
    public void shouldGetAllRightsForAUser() throws Exception {
        User user = insertUser();

        List<Right> allRightsForUser = roleRightsMapper.getAllRightsForUser(user.getUserName());
        assertThat(allRightsForUser.size(), is(0));

        Program program = insertProgram(make(a(defaultProgram, with(programCode, "p1"))));
        Role role = insertRole();

        insertRoleAssignments(program, user, role);

        roleRightsMapper.createRoleRight(role.getId(), CREATE_REQUISITION);
        roleRightsMapper.createRoleRight(role.getId(), VIEW_REQUISITION);

        allRightsForUser = roleRightsMapper.getAllRightsForUser(user.getUserName());
        assertThat(allRightsForUser.size(), is(2));
    }

    private Role insertRole() {
        Role r1 = new Role("r1", "random description");
        roleMapper.insert(r1);
        return r1;
    }

    private Program insertProgram(Program program) {
        programMapper.insert(program);
        return program;
    }

    private Role insertRoleAssignments(Program program, User user, Role role) {
        roleAssignmentMapper.createRoleAssignment(user, role, program, null);
        return role;
    }

    private User insertUser() {
        User user = new User("random123123", "pwd");
        userMapper.insert(user);
        return user;
    }


}
