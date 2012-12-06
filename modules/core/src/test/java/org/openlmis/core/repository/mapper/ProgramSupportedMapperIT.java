package org.openlmis.core.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.FacilityBuilder.FACILITY_CODE;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.ProgramBuilder.programCode;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.core.builder.ProgramBuilder.programStatus;
import static org.openlmis.core.domain.Right.CREATE_REQUISITION;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-core.xml")
@TransactionConfiguration(defaultRollback=true)
@Transactional
public class ProgramSupportedMapperIT {

    public static final String YELLOW_FEVER = "YELL_FVR";

    @Autowired
    ProgramMapper programMapper;

    @Autowired
    FacilityMapper facilityMapper;

    @Autowired
    ProgramSupportedMapper programSupportedMapper;

    @Autowired
    private RoleRightsMapper roleRightsMapper;

    @Autowired
    private UserMapper userMapper;

    @Test
    public void shouldSaveProgramSupported() throws Exception {
        facilityMapper.insert(make(a(defaultFacility)));
        programMapper.insert(make(a(defaultProgram, with(programCode, YELLOW_FEVER))));
        ProgramSupported programSupported = new ProgramSupported(FACILITY_CODE, YELLOW_FEVER, true, "user", now().toDate());

        programSupportedMapper.addSupportedProgram(programSupported);

        List<ProgramSupported> programsSupported = programSupportedMapper.getBy(FACILITY_CODE, YELLOW_FEVER);
        assertThat(programSupported, is(programsSupported.get(0)));
    }

    @Test
    public void shouldDeleteProgramMapping() throws Exception {
        facilityMapper.insert(make(a(defaultFacility)));
        programMapper.insert(make(a(defaultProgram, with(programCode, YELLOW_FEVER))));
        ProgramSupported programSupported = new ProgramSupported(FACILITY_CODE, YELLOW_FEVER, true, "user", now().toDate());
        programSupportedMapper.addSupportedProgram(programSupported);

        programSupportedMapper.deleteObsoletePrograms(FACILITY_CODE,YELLOW_FEVER);

        List<ProgramSupported> programsSupported = programSupportedMapper.getBy(FACILITY_CODE, YELLOW_FEVER);
        assertFalse(programsSupported.contains(programSupported));
    }
    
    @Test
    public void shouldFetchActiveProgramsForGivenProgramIdsForAUserAndAFacility(){
        User user = insertUser();

        Program activeProgram = insertProgram(make(a(defaultProgram, with(programCode, "p1"))));
        Program inactiveProgram = insertProgram(make(a(defaultProgram, with(programCode, "p3"), with(programStatus, false))));

        Role r1 = new Role("r1", "random description");
        roleRightsMapper.insertRole(r1);

        roleRightsMapper.createRoleRight(r1.getId(), CREATE_REQUISITION);
        insertRoleAssignments(activeProgram, user, r1);
        insertRoleAssignments(inactiveProgram, user, r1);

        Facility facility = insertFacility(make(a(defaultFacility)));

        insertProgramSupportedForFacility(activeProgram, facility, true);
        insertProgramSupportedForFacility(inactiveProgram, facility, true);

        ArrayList<String> programCodes = new ArrayList<>();
        programCodes.add(activeProgram.getCode());
        programCodes.add(inactiveProgram.getCode());

        String programCodesCommaSeparated = programCodes.toString().replace("[", "{").replace("]", "}");
        List<Program> programs = programSupportedMapper.filterActiveProgramsAndFacility(programCodesCommaSeparated, facility);
        assertEquals(1, programs.size());
        assertEquals(activeProgram.getCode(), programs.get(0).getCode());
    }

    private void insertProgramSupportedForFacility(Program program, Facility facility, boolean isActive) {
        programSupportedMapper.addSupportedProgram(new ProgramSupported(facility.getCode(), program.getCode(), isActive));
    }

    private Program insertProgram(Program program) {
        programMapper.insert(program);
        return program;
    }

    private Facility insertFacility(Facility facility) {
        facilityMapper.insert(facility);
        return facility;
    }

    private Role insertRoleAssignments(Program program, User user, Role role) {
        roleRightsMapper.createRoleAssignment(user, role, program);
        return role;
    }

    private User insertUser() {
        User user = new User("random123123", "pwd");
        userMapper.insert(user);
        return user;
    }
}
