package org.openlmis.admin.controller;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.RoleAssignment;
import org.openlmis.core.service.ProgramService;
import org.openlmis.core.service.RoleRightsService;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.client.MockHttpRequest;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ProgramControllerTest {

    @Mock
    @SuppressWarnings("unused")
    private ProgramService programService;

    @Mock
    private RoleRightsService roleRightsService;


    @Before
    public void init(){
        initMocks(this);
    }

    @Test
    public void shouldGetListOfPrograms() throws Exception {
        ProgramController controller = new ProgramController(programService, roleRightsService);
        List<Program> expectedPrograms = new ArrayList<Program>();

        when(programService.getAllActive()).thenReturn(expectedPrograms);

        List<Program> result = controller.getAllActivePrograms();

        verify(programService).getAllActive();
        assertThat(result, is(equalTo(expectedPrograms)));
    }

    @Test
    public void shouldGetListOfUserSupportedProgramsForAFacilityForCreateRequisitionsOperation() {
        ProgramController controller = new ProgramController(programService, roleRightsService);

        Program program = new Program();
        program.setCode("programCode");
        List<Program> programs = new ArrayList<>(Arrays.asList(program));

        MockHttpServletRequest request = new MockHttpServletRequest();
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("USER")).thenReturn("dummyUser");
        request.setSession(session);

        RoleAssignment roleAssignment = new RoleAssignment(1, 2, program.getCode());
        List<RoleAssignment> roleAssignments = new ArrayList<>(Arrays.asList(roleAssignment));
        when(roleRightsService.getProgramWithGivenRightForAUser(Right.CREATE_REQUISITION, "dummyUser")).thenReturn(roleAssignments);
        when(programService.filterActiveProgramsAndFacility(roleAssignments, "dummyFacilityCode")).thenReturn(programs);

        assertEquals(programs, controller.getUserSupportedProgramsToCreateRequisition("dummyFacilityCode", request));

    }

}
