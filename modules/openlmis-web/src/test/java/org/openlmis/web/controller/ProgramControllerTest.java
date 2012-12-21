package org.openlmis.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.RoleAssignment;
import org.openlmis.core.service.ProgramService;
import org.openlmis.core.service.RoleRightsService;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

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
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER;
import static org.openlmis.core.domain.Right.CREATE_REQUISITION;

public class ProgramControllerTest {

    @Mock
    @SuppressWarnings("unused")
    private ProgramService programService;

    @Mock
    @SuppressWarnings("unused")
    private RoleRightsService roleRightsService;

    ProgramController controller;
    private MockHttpServletRequest httpServletRequest;

    @Before
    public void init() {
        initMocks(this);
        controller = new ProgramController(programService, roleRightsService);
        httpServletRequest = new MockHttpServletRequest();
        MockHttpSession mockHttpSession = new MockHttpSession();
        httpServletRequest.setSession(mockHttpSession);
        mockHttpSession.setAttribute(USER,USER);

    }

    @Test
    public void shouldGetListOfPrograms() throws Exception {
        List<Program> expectedPrograms = new ArrayList<Program>();

        when(programService.getAllActive()).thenReturn(expectedPrograms);

        List<Program> result = controller.getAllActivePrograms();

        verify(programService).getAllActive();
        assertThat(result, is(equalTo(expectedPrograms)));
    }

    @Test
    public void shouldGetListOfUserSupportedProgramsForAFacilityForCreateRequisitionsOperation() {

        Program program = new Program();
        program.setCode("programCode");
        List<Program> programs = new ArrayList<>(Arrays.asList(program));

        MockHttpServletRequest request = new MockHttpServletRequest();
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("USER")).thenReturn("dummyUser");
        request.setSession(session);

        RoleAssignment roleAssignment = new RoleAssignment(1, 2, program.getId());
        List<RoleAssignment> roleAssignments = new ArrayList<>(Arrays.asList(roleAssignment));
        when(roleRightsService.getRoleAssignments(CREATE_REQUISITION, "dummyUser")).thenReturn(roleAssignments);
        Integer facilityId = 12345;
        when(programService.filterActiveProgramsAndFacility(roleAssignments, facilityId)).thenReturn(programs);

        assertEquals(programs, controller.getUserSupportedProgramsToCreateRequisition(facilityId, request));

    }

    @Test
    public void shouldGetListOfActiveProgramsForAUserWithCreateRequisitionRight() throws Exception {

        List<Program> expectedPrograms = new ArrayList<Program>();

        when(programService.getUserSupervisedActivePrograms(USER, CREATE_REQUISITION)).thenReturn(expectedPrograms);

        List<Program> result = controller.getUserSupervisedActiveProgramsForCreateRequisition(httpServletRequest);

        verify(programService).getUserSupervisedActivePrograms(USER, CREATE_REQUISITION);
        assertThat(result, is(equalTo(expectedPrograms)));
    }

}
