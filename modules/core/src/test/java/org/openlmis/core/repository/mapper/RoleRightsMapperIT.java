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

    @Test
    public void shouldSetupRightsForAdminRole() {
        List<Right> adminRights = roleRightsMapper.getAllRightsForUser("Admin123");
        assertEquals(Right.CONFIGURE_RNR, adminRights.get(0));
        assertEquals(Right.MANAGE_FACILITY, adminRights.get(1));
        assertEquals(Right.UPLOADS, adminRights.get(2));
    }

    @Test
    public void shouldReturnProgramAvailableForAFacilityForAUserWithGivenRights() throws Exception {
        Program program1 = insertProgram(make(a(defaultProgram, with(programCode, "p1"))));
        Program program2 = insertProgram(make(a(defaultProgram, with(programCode, "p2"))));

        User user = insertUser();

        Role r1 = new Role("r1", "random description");
        roleRightsMapper.insertRole(r1);

        Role r2 = new Role("r2", "random description");
        roleRightsMapper.insertRole(r2);


        roleRightsMapper.createRoleRight(r1.getId(), CREATE_REQUISITION);
        roleRightsMapper.createRoleRight(r1.getId(), VIEW_REQUISITION);
        roleRightsMapper.createRoleRight(r2.getId(), APPROVE_REQUISITION);
        roleRightsMapper.createRoleRight(r2.getId(), VIEW_REQUISITION);

        insertRoleAssignments(program1, user, r1);
        insertRoleAssignments(program1, user, r2);
        insertRoleAssignments(program2, user, r2);

        List<RoleAssignment> roleAssignments =
                roleRightsMapper.getRoleAssignmentsWithGivenRightForAUser(CREATE_REQUISITION, user.getUserName());

        assertEquals(1, roleAssignments.size());
        assertEquals(program1.getId(), roleAssignments.get(0).getProgramId());
        assertThat(roleAssignments.get(0).getRoleId(), is(r1.getId()));
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
        roleRightsMapper.insertRole(r1);
        return r1;
    }

    private Program insertProgram(Program program) {
        program.setId(programMapper.insert(program));
        return program;
    }

    private Role insertRoleAssignments(Program program, User user, Role role) {
        roleRightsMapper.createRoleAssignment(user, role, program);
        return role;
    }

    private User insertUser() {
        User user = new User("random123123", "pwd");
        user.setId(userMapper.insert(user));
        return user;
    }


}
