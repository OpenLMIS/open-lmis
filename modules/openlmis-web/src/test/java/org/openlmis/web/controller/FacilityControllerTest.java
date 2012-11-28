package org.openlmis.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProgramService;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import static org.mockito.Mockito.*;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER;

public class FacilityControllerTest {

    ProgramService programService;
    FacilityService facilityService;

    @Before
    public void setUp() throws Exception {
        programService = mock(ProgramService.class);
        facilityService = mock(FacilityService.class);
    }

    @Test
    public void shouldFetchRequiredReferenceDataForFacility() throws Exception {

        new FacilityController(facilityService,programService).getReferenceData();

        verify(facilityService).getAllOperators();
        verify(facilityService).getAllTypes();
        verify(facilityService).getAllZones();
        verify(programService).getAll();
    }

    @Test
    public void shouldSaveFacility() throws Exception {
        Facility facility = mock(Facility.class);
        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        MockHttpSession mockHttpSession = new MockHttpSession();
        httpServletRequest.setSession(mockHttpSession);
        mockHttpSession.setAttribute(USER,USER);

        new FacilityController(facilityService,programService).addFacility(facility,httpServletRequest);

        verify(facilityService).save(facility);
        verify(facility).setModifiedBy(USER);
    }
}
