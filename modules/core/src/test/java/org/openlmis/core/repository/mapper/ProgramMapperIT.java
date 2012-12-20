package org.openlmis.core.repository.mapper;

import org.junit.Test;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.builder.SupervisoryNodeBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.service.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static com.natpryce.makeiteasy.MakeItEasy.with;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.core.builder.ProgramBuilder.programCode;
import static org.openlmis.core.builder.ProgramBuilder.programStatus;
import static org.openlmis.core.builder.ProgramSupportedBuilder.*;

@ContextConfiguration(locations = "classpath*:applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class ProgramMapperIT extends SpringIntegrationTest {

    public static final String PROGRAM_CODE = "HIV";
    public static final Integer PROGRAM_ID = 1;

    @Autowired
    ProgramSupportedMapper programSupportedMapper;

    @Autowired
    FacilityMapper facilityMapper;

    @Autowired
    ProgramMapper programMapper;

    @Autowired
    RoleRightsMapper roleRightsMapper;

    @Autowired
    RoleAssignmentMapper roleAssignmentMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    SupervisoryNodeMapper supervisoryNodeMapper;

    @Autowired
    RoleMapper roleMapper;

    @Test
    public void shouldGetAllActiveProgram() {
        List<Program> programs = programMapper.getAllActive();
        assertEquals(6, programs.size());
        assertThat(programs, hasItem(new Program(PROGRAM_ID, PROGRAM_CODE, PROGRAM_CODE, PROGRAM_CODE, true)));
    }

    @Test
    public void shouldGetProgramsWhichAreActiveByFacilityCode() {
        Facility facility = make(a(FacilityBuilder.defaultFacility));
        Integer facilityId = facilityMapper.insert(facility);
        Program program = make(a(defaultProgram));
        Integer programId = programMapper.insert(program);
        ProgramSupported programSupported = make(a(defaultProgramSupported, with(supportedFacilityId, facilityId), with(supportedProgramId, programId)));
        programSupportedMapper.addSupportedProgram(programSupported);

        List<Program> programs = programMapper.getActiveByFacility(facilityId);

        assertThat(programs.size(), is(1));
        assertThat(programs.get(0).getCode(), is(ProgramBuilder.PROGRAM_CODE));
    }

    @Test
    public void shouldGetAllPrograms() throws Exception {
        List<Program> programs = programMapper.getAll();
        assertEquals(7, programs.size());
    }

    @Test
    public void shouldGetProgramsSupportedByFacility() throws Exception {
        Facility facility = make(a(defaultFacility));
        Integer facilityId = facilityMapper.insert(facility);
        Program program = make(a(defaultProgram));
        Integer programId = programMapper.insert(program);
        ProgramSupported programSupported = make(a(defaultProgramSupported, with(supportedFacilityId, facilityId), with(supportedProgramId, programId)));
        programSupportedMapper.addSupportedProgram(programSupported);
        List<Program> supportedPrograms = programMapper.getByFacilityId(facilityId);
        assertThat(supportedPrograms.get(0).getCode(), is(ProgramBuilder.PROGRAM_CODE));
    }

    @Test
    public void shouldGetIdByCode() throws Exception {
        Integer id = programMapper.insert(make(a(defaultProgram)));
        assertThat(id, is(programMapper.getIdByCode(ProgramBuilder.PROGRAM_CODE)));
    }

    @Test
    public void shouldReturnProgramById() throws Exception {
        Program program = make(a(defaultProgram));
        program.setId(programMapper.insert(program));
        assertThat(programMapper.getById(program.getId()), is(program));
    }

    @Test
    public void shouldGetAllProgramsForUserSupervisedFacilitiesForWhichHeHasCreateRnrRight(){
        Facility facility = make(a(defaultFacility));
        facility.setId(facilityMapper.insert(facility));
        SupervisoryNode node = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode));
        node.setFacility(facility);
        SupervisoryNode supervisoryNode = insertSupervisoryNode(node);

        Program activeProgram = insertProgram(make(a(defaultProgram, with(programCode, "P1"))));
        Program inactiveProgram = insertProgram(make(a(defaultProgram, with(programCode, "P2"), with(programStatus, false))));
        Program activeProgramWithoutRight = insertProgram(make(a(defaultProgram, with(programCode, "P3"))));
        Program activeProgramForHomeFacility = insertProgram(make(a(defaultProgram, with(programCode, "P4"))));

        User user = insertUser();

        Role createRnrRole = new Role("R1", "Create Rnr Role");
        roleMapper.insert(createRnrRole);
        roleRightsMapper.createRoleRight(createRnrRole.getId(), Right.CREATE_REQUISITION);
        insertRoleAssignments(activeProgram, user, createRnrRole, supervisoryNode);
        insertRoleAssignments(inactiveProgram, user, createRnrRole, supervisoryNode);
        insertRoleAssignments(activeProgramForHomeFacility, user, createRnrRole, null);

        Role viewRnrRole = new Role("R2", "View Rnr Role");
        roleMapper.insert(viewRnrRole);
        roleRightsMapper.createRoleRight(viewRnrRole.getId(), Right.VIEW_REQUISITION);
        insertRoleAssignments(activeProgramWithoutRight, user, viewRnrRole, supervisoryNode);

        List<Program> programs = programMapper.getActiveProgramsForUser(user.getId(), Right.CREATE_REQUISITION);

        assertThat(programs.size(), is(1));
        assertThat(programs.get(0).getCode(), is("P1"));
    }

    private SupervisoryNode insertSupervisoryNode(SupervisoryNode supervisoryNode) {
        supervisoryNodeMapper.insert(supervisoryNode);
        return supervisoryNode;
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
        int id = userMapper.insert(user);
        user.setId(id);
        return user;
    }
}
