/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.web.controller.vaccine;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.RightName;
import org.openlmis.core.domain.User;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.core.service.UserService;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.vaccine.service.reports.VaccineReportService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({Date.class})
public class VaccineDemographicProgramControllerTest {


    @Mock
    VaccineReportService service;

    @Mock
    ProgramService programService;

    @Mock
    FacilityService facilityService;

    @Mock
    UserService userService;

    @InjectMocks
    VaccineProgramController controller;

    private MockHttpSession session;
    private MockHttpServletRequest httpServletRequest;

    @Before
    public void setUp() throws Exception {
        httpServletRequest = new MockHttpServletRequest();
        session = new MockHttpSession();
        httpServletRequest.setSession(session);
        session.setAttribute(UserAuthenticationSuccessHandler.USER_ID, 1L);
    }

    @Test
    public void shouldGetProgramsForConfiguration() throws Exception {
        List<Program> programs = new ArrayList<>();

        when(programService.getAllIvdPrograms()).thenReturn(programs);
        ResponseEntity<OpenLmisResponse> response = controller.getProgramsForConfiguration();

        verify(programService).getAllIvdPrograms();
        assertThat(programs, is(response.getBody().getData().get("programs")));
    }


    @Test
    public void shouldGetProgramsForIVDForm() throws Exception {
        List<Program> programs = new ArrayList<>();
        User user = new User();
        user.setFacilityId(1L);

        when(programService.getIvdProgramsSupportedByUserHomeFacilityWithRights(1L, 1L, RightName.CREATE_IVD, RightName.APPROVE_IVD, RightName.VIEW_IVD)).thenReturn(programs);
        when(userService.getById(1L)).thenReturn(user);
        ResponseEntity<OpenLmisResponse> response = controller.getProgramForIvdFormHomeFacility(httpServletRequest);

        verify(programService).getIvdProgramsSupportedByUserHomeFacilityWithRights(1L, 1L, RightName.CREATE_IVD, RightName.APPROVE_IVD, RightName.VIEW_IVD);
        assertThat(programs, is(response.getBody().getData().get("programs")));
    }

    @Test
    public void shouldGetFacilities() throws Exception {
        List<Facility> facilities = new ArrayList<>();
        when(facilityService.getUserSupervisedFacilities(1L, 1L, RightName.CREATE_IVD, RightName.APPROVE_IVD, RightName.VIEW_IVD)).thenReturn(facilities);
        ResponseEntity<OpenLmisResponse> response = controller.getFacilities(1L, httpServletRequest);

        assertThat(facilities, is(response.getBody().getData().get("facilities")));
    }

    @Test
    public void shouldGetProgramsForDemographicEstimates() throws Exception {

    }
}