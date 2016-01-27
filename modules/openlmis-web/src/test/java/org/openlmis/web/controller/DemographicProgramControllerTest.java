/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.core.domain.Program;
import org.openlmis.core.service.ProgramService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.core.web.OpenLmisResponse;
import org.springframework.http.ResponseEntity;
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
import static org.openlmis.core.domain.RightName.*;
import static org.openlmis.web.controller.ProgramController.PROGRAM;
import static org.openlmis.web.controller.ProgramController.PROGRAMS;

@Category(UnitTests.class)
public class DemographicProgramControllerTest {

    public static final Long USER_ID = 1L;
    @InjectMocks
    ProgramController controller;
    @Mock
    private ProgramService programService;
    private MockHttpServletRequest httpServletRequest;

    @Before
    public void init() {
        initMocks(this);
        httpServletRequest = new MockHttpServletRequest();
        MockHttpSession mockHttpSession = new MockHttpSession();
        httpServletRequest.setSession(mockHttpSession);
        mockHttpSession.setAttribute(USER, USER);
        mockHttpSession.setAttribute(UserAuthenticationSuccessHandler.USER_ID, USER_ID);

    }

    @Test
    public void shouldGetListOfUserSupportedProgramsForAFacilityForGivenRights() {
        Program program = new Program();
        program.setPush(false);
        List<Program> programs = new ArrayList<>(Arrays.asList(program));

        Long facilityId = 12345L;

        when(programService.getProgramsForUserByFacilityAndRights(facilityId, USER_ID, VIEW_REQUISITION)).thenReturn(programs);

        assertThat(programs, is(controller.getProgramsToViewRequisitions(facilityId, httpServletRequest)));

    }

    @Test
    public void shouldGetListOfActiveProgramsForAUserWithCreateRequisitionRight() throws Exception {

        List<Program> expectedPrograms = new ArrayList<>();

        when(programService.getProgramForSupervisedFacilities(USER_ID, CREATE_REQUISITION, AUTHORIZE_REQUISITION)).thenReturn(expectedPrograms);

        List<Program> result = controller.getProgramsForCreateOrAuthorizeRequisition(null, httpServletRequest);

        verify(programService).getProgramForSupervisedFacilities(USER_ID, CREATE_REQUISITION, AUTHORIZE_REQUISITION);
        assertThat(result, is(equalTo(expectedPrograms)));
    }

    @Test
    public void shouldGetListOfAllPullPrograms() throws Exception {
        List<Program> expectedPrograms = new ArrayList<>();

        when(programService.getAllPullPrograms()).thenReturn(expectedPrograms);

        ResponseEntity<OpenLmisResponse> response = controller.getAllPullPrograms();

        verify(programService).getAllPullPrograms();
        List<Program> actual = (List<Program>) response.getBody().getData().get(PROGRAMS);
        assertThat(actual, is(equalTo(expectedPrograms)));
    }

    @Test
    public void shouldGetListOfAllPrograms() throws Exception {
        List<Program> expectedPrograms = new ArrayList<>();

        when(programService.getAll()).thenReturn(expectedPrograms);

        ResponseEntity<OpenLmisResponse> response = controller.getAllPrograms();

        verify(programService).getAll();
        List<Program> actual = (List<Program>) response.getBody().getData().get(PROGRAMS);
        assertThat(actual, is(equalTo(expectedPrograms)));
    }

    @Test
    public void shouldGetProgramsForViewRightAndFacilityForUser() throws Exception {

        List<Program> expectedPrograms = new ArrayList<>();
        Program pushProgram = new Program();
        pushProgram.setPush(true);
        Program pullProgram = new Program();
        pullProgram.setPush(false);
        expectedPrograms.add(pullProgram);
        expectedPrograms.add(pushProgram);
        when(programService.getProgramsForUserByFacilityAndRights(1L, USER_ID, VIEW_REQUISITION)).thenReturn(expectedPrograms);

        List<Program> result = controller.getProgramsToViewRequisitions(1L, httpServletRequest);


        verify(programService).getProgramsForUserByFacilityAndRights(1L, USER_ID, VIEW_REQUISITION);
        expectedPrograms.remove(pushProgram);
        assertThat(result, is(equalTo(expectedPrograms)));
    }

    @Test
    public void shouldGetProgramById() throws Exception {
        Program expectedProgram = new Program();
        when(programService.getById(1L)).thenReturn(expectedProgram);
        ResponseEntity<OpenLmisResponse> response = controller.get(1L);

        assertThat((Program) response.getBody().getData().get(PROGRAM), is(expectedProgram));
        verify(programService).getById(1L);
    }
}
