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
import static org.openlmis.core.builder.FacilityBuilder.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-core.xml")
@TransactionConfiguration(defaultRollback=true)
@Transactional
public class RoleRightsMapperIT {

    @Autowired
    RoleRightsMapper roleRightsMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    ProgramMapper programMapper;

    @Autowired
    private FacilityMapper facilityMapper;

    @Autowired
    ProgramSupportedMapper programSupportedMapper;

    @Test
    public void shouldReturnProgramAvailableForAFacilityForAUserWithGivenRights() throws Exception {
        Program program = insertProgram();
        Facility dummyFacility = insertFacility();
        User user = insertUser();
        insertRoleRightsAndAssignments(program, user);
        insertProgramSupportedForFacility(program, dummyFacility);

        List<Program> programs = roleRightsMapper.getProgramWithGivenRightForAUserAndFacility(Right.CREATE_REQUISITION, user.getUserName(), dummyFacility.getCode());

        assertEquals(1, programs.size());
        assertEquals(program.getCode(), programs.get(0).getCode());

    }

    private void insertProgramSupportedForFacility(Program program, Facility facility) {
        programSupportedMapper.addSupportedProgram(new ProgramSupported(facility.getCode(), program.getCode(), true));
    }

    private Program insertProgram() {
        Program program = new Program("dummyProgramCode", "dummyProgramName", "dummyProgramDescription", true);
        programMapper.insert(program);
        return program;
    }

    private Facility insertFacility() {
        Facility dummyFacility = make(a(defaultFacility,
                with(code, "FacilityDummy101"),
                with(name, "Dummy Facility 101"),
                with(type, "warehouse"),
                with(geographicZone, 1)));
        facilityMapper.insert(dummyFacility);
        return dummyFacility;
    }

    private Role insertRoleRightsAndAssignments(Program program, User user) {
        Role role = new Role("SuperRole", "random description");
        roleRightsMapper.insertRole(role);
        roleRightsMapper.createRoleRight(role.getId(), Right.CREATE_REQUISITION);

        roleRightsMapper.createRoleAssignment(user, role, program);

        return role;
    }

    private User insertUser() {
        User user = new User("random123123", "pwd");
        userMapper.insert(user);
        return user;
    }
}
