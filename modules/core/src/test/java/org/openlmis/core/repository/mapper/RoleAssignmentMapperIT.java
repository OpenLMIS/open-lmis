package org.openlmis.core.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.SupervisoryNodeBuilder;
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
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.core.builder.ProgramBuilder.programCode;
import static org.openlmis.core.builder.UserBuilder.defaultUser;
import static org.openlmis.core.builder.UserBuilder.facilityId;
import static org.openlmis.core.domain.Right.CONFIGURE_RNR;
import static org.openlmis.core.domain.Right.CREATE_REQUISITION;

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
  FacilityMapper facilityMapper;
  @Autowired
  SupervisoryNodeMapper supervisoryNodeMapper;

  private User user;
  private Facility facility;

  @Before
  public void setUp() throws Exception {
    facility = insertFacility();
    user = insertUser(facility);
  }

  @Test
  public void shouldReturnProgramAvailableForAFacilityForAUserWithGivenRights() throws Exception {
    Program program1 = insertProgram(make(a(defaultProgram, with(programCode, "p1"))));
    Program program2 = insertProgram(make(a(defaultProgram, with(programCode, "p2"))));

    Role r1 = new Role("r1", "random description");
    roleRightsMapper.insertRole(r1);

    Role r2 = new Role("r2", "random description");
    roleRightsMapper.insertRole(r2);

    roleRightsMapper.createRoleRight(r1.getId(), CREATE_REQUISITION);
    roleRightsMapper.createRoleRight(r1.getId(), CONFIGURE_RNR);
    roleRightsMapper.createRoleRight(r2.getId(), CONFIGURE_RNR);

    SupervisoryNode supervisoryNode = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode));
    supervisoryNode.setFacility(facility );
    supervisoryNodeMapper.insert(supervisoryNode);

    insertRoleAssignments(program1, user, r1, supervisoryNode);
    insertRoleAssignments(program1, user, r2, null);
    insertRoleAssignments(program2, user, r2, null);

    List<RoleAssignment> roleAssignments =
      roleAssignmentMapper.getRoleAssignmentsWithGivenRightForAUser(CREATE_REQUISITION, user.getId());

    assertEquals(1, roleAssignments.size());
    RoleAssignment expectedRoleAssignment = new RoleAssignment(user.getId(), r1.getId(), program1.getId(), supervisoryNode);
    assertThat(roleAssignments.get(0), is(expectedRoleAssignment));
  }

  private Program insertProgram(Program program) {
    programMapper.insert(program);
    return program;
  }

  private Role insertRoleAssignments(Program program, User user, Role role, SupervisoryNode supervisoryNode) {
    roleAssignmentMapper.createRoleAssignment(user, role, program, supervisoryNode);
    return role;
  }

  private User insertUser(Facility facility) {
    User user = make(a(defaultUser, with(facilityId, facility.getId())));
    userMapper.insert(user);
    return user;
  }

  private Facility insertFacility() {
    Facility facility = make(a(defaultFacility));
    facilityMapper.insert(facility);
    return facility;
  }
}
