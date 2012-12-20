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
public class RoleAssignmentMapperIT {

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
    public void shouldReturnProgramAvailableForAFacilityForAUserWithGivenRights() throws Exception {
        Program program1 = insertProgram(make(a(defaultProgram, with(programCode, "p1"))));
        Program program2 = insertProgram(make(a(defaultProgram, with(programCode, "p2"))));

        User user = insertUser();

        Role r1 = new Role("r1", "random description");
        roleMapper.insert(r1);

        Role r2 = new Role("r2", "random description");
        roleMapper.insert(r2);

        roleRightsMapper.createRoleRight(r1.getId(), CREATE_REQUISITION);
        roleRightsMapper.createRoleRight(r1.getId(), VIEW_REQUISITION);
        roleRightsMapper.createRoleRight(r2.getId(), APPROVE_REQUISITION);
        roleRightsMapper.createRoleRight(r2.getId(), VIEW_REQUISITION);

        insertRoleAssignments(program1, user, r1, null);
        insertRoleAssignments(program1, user, r2, null);
        insertRoleAssignments(program2, user, r2, null);

        List<RoleAssignment> roleAssignments =
                roleAssignmentMapper.getRoleAssignmentsWithGivenRightForAUser(CREATE_REQUISITION, user.getUserName());

        assertEquals(1, roleAssignments.size());
        assertEquals(program1.getId(), roleAssignments.get(0).getProgramId());
        assertThat(roleAssignments.get(0).getRoleId(), is(r1.getId()));
    }

    private Program insertProgram(Program program) {
        program.setId(programMapper.insert(program));
        return program;
    }

    private Role insertRoleAssignments(Program program, User user, Role role, SupervisoryNode supervisoryNode) {
        roleAssignmentMapper.createRoleAssignment(user, role, program, supervisoryNode);
        return role;
    }

    private User insertUser() {
        User user = new User("random123123", "pwd");
        user.setId(userMapper.insert(user));
        return user;
    }
}
