package org.openlmis.core.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.SupervisoryNodeBuilder;
import org.openlmis.core.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.core.builder.ProgramBuilder.programCode;
import static org.openlmis.core.builder.UserBuilder.*;
import static org.openlmis.core.domain.Right.APPROVE_REQUISITION;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class UserMapperIT {


    @Autowired
    UserMapper userMapper;
    @Autowired
    private ProgramMapper programMapper;
    @Autowired
    RoleAssignmentMapper roleAssignmentMapper;
    @Autowired
    private RoleRightsMapper roleRightsMapper;
    @Autowired
    private FacilityMapper facilityMapper;
    @Autowired
    private SupervisoryNodeMapper supervisoryNodeMapper;

    private Facility facility;
    private SupervisoryNode supervisoryNode;

    @Before
    public void setUp() throws Exception {
        supervisoryNode = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode));

        facility = make(a(FacilityBuilder.defaultFacility));
        facilityMapper.insert(facility);
        supervisoryNode.setFacility(facility);

        supervisoryNodeMapper.insert(supervisoryNode);
    }

    @Test
    public void shouldGetUserByUserNameAndPassword() throws Exception {
        User someUser = make(a(defaultUser, with(facilityId, facility.getId())));
        userMapper.insert(someUser);

        User user = userMapper.selectUserByUserNameAndPassword(defaultUserName, defaultPassword);
        assertThat(user, is(notNullValue()));
        assertThat(user.getUserName(), is(defaultUserName));
        assertThat(user.getId(), is(someUser.getId()));
        User user1 = userMapper.selectUserByUserNameAndPassword(defaultUserName, "wrongPassword");
        assertThat(user1, is(nullValue()));
        User user2 = userMapper.selectUserByUserNameAndPassword("wrongUserName", defaultPassword);
        assertThat(user2, is(nullValue()));
    }

    @Test
    public void shouldGetUsersWithGivenRightInNodeForProgram() {
        User someUser = make(a(defaultUser, with(facilityId, facility.getId())));
        userMapper.insert(someUser);

        Program program = insertProgram(make(a(defaultProgram, with(programCode, "p1"))));

        Role role = insertRole();
        roleRightsMapper.createRoleRight(role.getId(), APPROVE_REQUISITION);

        insertRoleAssignments(program, someUser, role, supervisoryNode);

        final List<User> users = userMapper.getUsersWithRightInNodeForProgram(program.getId(), supervisoryNode.getId(), Right.APPROVE_REQUISITION);
        assertThat(users.contains(someUser), is(true));
    }

    @Test
    public void shouldInsertAUser() throws Exception {
        User user = make(a(defaultUser, with(facilityId, facility.getId())));
        Integer userCount = userMapper.insert(user);
        assertThat(userCount, is(1));
        assertThat(user.getId(), is(notNullValue()));
    }

    private Program insertProgram(Program program) {
        programMapper.insert(program);
        return program;
    }

    private Role insertRole() {
        Role r1 = new Role("r1", "random description");
        roleRightsMapper.insertRole(r1);
        return r1;
    }

    private Role insertRoleAssignments(Program program, User user, Role role, SupervisoryNode supervisoryNode) {
        roleAssignmentMapper.createRoleAssignment(user, role, program, supervisoryNode);
        return role;
    }
}
