package org.openlmis.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.Right;
import org.openlmis.core.service.ProgramService;
import org.openlmis.core.service.RoleRightsService;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER;
import static org.openlmis.core.domain.Right.AUTHORIZE_REQUISITION;
import static org.openlmis.core.domain.Right.CREATE_REQUISITION;

public class ProgramControllerTest {

  public static final Integer USER_ID = 1;
  @Mock
    @SuppressWarnings("unused")
    private ProgramService programService;

    ProgramController controller;
    private MockHttpServletRequest httpServletRequest;

    @Before
    public void init() {
        initMocks(this);
        controller = new ProgramController(programService);
        httpServletRequest = new MockHttpServletRequest();
        MockHttpSession mockHttpSession = new MockHttpSession();
        httpServletRequest.setSession(mockHttpSession);
        mockHttpSession.setAttribute(USER, USER);
        mockHttpSession.setAttribute(UserAuthenticationSuccessHandler.USER_ID,USER_ID);

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
        List<Program> programs = new ArrayList<>(Arrays.asList(program));

        Integer facilityId = 12345;
        when(programService.getProgramsSupportedByFacilityForUserWithRight(facilityId, USER_ID, Right.CREATE_REQUISITION, Right.AUTHORIZE_REQUISITION)).thenReturn(programs);

        assertEquals(programs, controller.getUserSupportedProgramsToCreateOrAuthorizeRequisition(facilityId, httpServletRequest));

    }

    @Test
    public void shouldGetListOfActiveProgramsForAUserWithCreateRequisitionRight() throws Exception {

        List<Program> expectedPrograms = new ArrayList<Program>();

        when(programService.getUserSupervisedActiveProgramsWithRights(USER_ID, CREATE_REQUISITION, AUTHORIZE_REQUISITION)).thenReturn(expectedPrograms);

        List<Program> result = controller.getUserSupervisedActiveProgramsForCreateAndAuthorizeRequisition(httpServletRequest);

        verify(programService).getUserSupervisedActiveProgramsWithRights(USER_ID, CREATE_REQUISITION, AUTHORIZE_REQUISITION);
        assertThat(result, is(equalTo(expectedPrograms)));
    }

}
