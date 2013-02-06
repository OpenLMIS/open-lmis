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
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
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
  RoleAssignmentMapper mapper;
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
    supervisoryNode.setFacility(facility);
    supervisoryNodeMapper.insert(supervisoryNode);

    mapper.createRoleAssignment(user.getId(), program1.getId(), r1.getId(), supervisoryNode.getId());
    mapper.createRoleAssignment(user.getId(), program1.getId(), r2.getId(), null);
    mapper.createRoleAssignment(user.getId(), program2.getId(), r2.getId(), null);

    List<RoleAssignment> roleAssignments =
        mapper.getRoleAssignmentsWithGivenRightForAUser(CREATE_REQUISITION, user.getId());

    assertEquals(1, roleAssignments.size());
    RoleAssignment expectedRoleAssignment = new RoleAssignment(user.getId(), r1.getId(), program1.getId(), supervisoryNode);
    assertThat(roleAssignments.get(0), is(expectedRoleAssignment));
  }

  @Test
  public void shouldGetRolesForAUserAndProgram() throws Exception {
    mapper.createRoleAssignment(user.getId(), 1, 1, null);
    mapper.createRoleAssignment(user.getId(), 2, 1, null);

    List<Integer> roleIds = mapper.getRoleAssignmentsForUserAndProgram(user.getId(), 1);

    assertThat(roleIds.size(), is(1));
    assertThat(roleIds.get(0).equals(1), is(true));
  }

  @Test
  public void shouldGetProgramsForWhichUserHasRoleAssignments() throws Exception {
    mapper.deleteAllRoleAssignmentsForUser(1);
    mapper.createRoleAssignment(1, 1, 1, null);
    mapper.createRoleAssignment(1, 2, 1, null);
    mapper.createRoleAssignment(1, null, 1, null);

    List<Integer> listOfProgramIdsForTheUser = mapper.getProgramsForWhichUserHasRoleAssignments(1);

    assertThat(listOfProgramIdsForTheUser.size(), is(2));

    for (Integer programId : listOfProgramIdsForTheUser) {
      assertThat(programId.equals(1) || programId.equals(2), is(true));
    }
  }

  @Test
  public void shouldDeleteRoleAssignmentsForAUser() throws Exception {
    Integer userId = user.getId();
    mapper.createRoleAssignment(userId, 2, 1, null);

    mapper.deleteAllRoleAssignmentsForUser(userId);

    assertThat(mapper.getRoleAssignmentsForUserAndProgram(userId, 2).size(), is(0));
  }

  private Program insertProgram(Program program) {
    programMapper.insert(program);
    return program;
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
