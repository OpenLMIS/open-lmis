/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.core.domain.Program;
import org.openlmis.core.service.ProgramService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.web.response.OpenLmisResponse;
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
import static org.openlmis.core.domain.Right.*;
import static org.openlmis.web.controller.ProgramController.PROGRAM;
import static org.openlmis.web.controller.ProgramController.PROGRAMS;
@Category(UnitTests.class)
public class ProgramControllerTest {

  public static final Long USER_ID = 1L;
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
    mockHttpSession.setAttribute(UserAuthenticationSuccessHandler.USER_ID, USER_ID);

  }

  @Test
  public void shouldGetListOfUserSupportedProgramsForAFacilityForGivenRights() {
    Program program = new Program();
    List<Program> programs = new ArrayList<>(Arrays.asList(program));

    Long facilityId = 12345L;

    when(programService.getProgramsForUserByFacilityAndRights(facilityId, USER_ID, VIEW_REQUISITION)).thenReturn(programs);

    assertEquals(programs, controller.getProgramsToViewRequisitions(facilityId, httpServletRequest));

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
  public void shouldGetListOfAllPrograms() throws Exception {
    List<Program> expectedPrograms = new ArrayList<>();

    when(programService.getAllPullPrograms()).thenReturn(expectedPrograms);

    ResponseEntity<OpenLmisResponse> response = controller.getAllPullPrograms();

    verify(programService).getAllPullPrograms();
    List<Program> actual = (List<Program>) response.getBody().getData().get(PROGRAMS);
    assertThat(actual, is(equalTo(expectedPrograms)));
  }

  @Test
  public void shouldGetProgramsForViewRightAndFacilityForUser() throws Exception {

    List<Program> expectedPrograms = new ArrayList<>();

    when(programService.getProgramsForUserByFacilityAndRights(1L, USER_ID, VIEW_REQUISITION)).thenReturn(expectedPrograms);

    List<Program> result = controller.getProgramsToViewRequisitions(1L, httpServletRequest);

    verify(programService).getProgramsForUserByFacilityAndRights(1L, USER_ID, VIEW_REQUISITION);
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
