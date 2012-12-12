package org.openlmis.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.openlmis.core.domain.*;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.web.model.ReferenceData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ModelMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER;

public class FacilityControllerTest {

    private ProgramService programService;
    private FacilityService facilityService;
    private FacilityController facilityController;
    private MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();

    @Before
    public void setUp() throws Exception {
        programService = mock(ProgramService.class);
        facilityService = mock(FacilityService.class);
        facilityController = new FacilityController(facilityService, programService);
        MockHttpSession mockHttpSession = new MockHttpSession();
        httpServletRequest.setSession(mockHttpSession);
        mockHttpSession.setAttribute(USER,USER);
    }

    @Test
    public void shouldFetchRequiredReferenceDataForFacility() throws Exception {

        List<FacilityOperator> facilityOperators = new ArrayList<>();
        when(facilityService.getAllOperators()).thenReturn(facilityOperators);
        List<FacilityType> facilityTypes = new ArrayList<>();
        when(facilityService.getAllTypes()).thenReturn(facilityTypes);
        List<GeographicZone> allZones = new ArrayList<>();
        when(facilityService.getAllZones()).thenReturn(allZones);
        List<Program> allPrograms = new ArrayList<>();
        when(programService.getAll()).thenReturn(allPrograms);


        Map referenceData = facilityController.getReferenceData();

        verify(facilityService).getAllOperators();
        assertThat((List<FacilityOperator>) referenceData.get(ReferenceData.FACILITY_OPERATORS), is(equalTo(facilityOperators)));
        verify(facilityService).getAllTypes();
        assertThat((List<FacilityType>) referenceData.get(ReferenceData.FACILITY_TYPES), is(equalTo(facilityTypes)));
        verify(facilityService).getAllZones();
        assertThat((List<GeographicZone>) referenceData.get(ReferenceData.GEOGRAPHIC_ZONES), is(equalTo(allZones)));
        verify(programService).getAll();
        assertThat((List<Program>) referenceData.get(ReferenceData.PROGRAMS), is(equalTo(allPrograms)));
    }

    @Test
    public void shouldSaveFacility() throws Exception {
        Facility facility = new Facility();
        facility.setName("test facility");
        ResponseEntity responseEntity = facilityController.addOrUpdate(facility, httpServletRequest);
        assertThat(responseEntity.getStatusCode(),is(HttpStatus.OK));
        ModelMap modelMap = (ModelMap)responseEntity.getBody();
        assertThat((String)modelMap.get("success"),is("test facility created successfully"));
        verify(facilityService).save(facility);
        assertThat(facility.getModifiedBy(),is(USER));
    }

    @Test
    public void shouldReturnErrorMessageIfSaveFails() throws Exception {
        Facility facility = new Facility();
        doThrow(new RuntimeException("error message")).when(facilityService).save(facility);
        ResponseEntity responseEntity = facilityController.addOrUpdate(facility, httpServletRequest);
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        ModelMap body = (ModelMap) responseEntity.getBody();
        assertThat(body.get("error").toString(), is("error message"));
        MockHttpServletRequest httpServletRequest = httpRequest();

        facilityController.addOrUpdate(facility, httpServletRequest);
    }

    @Test
    public void shouldReturnAllFacilitiesForTheUser() {
        Facility facility = mock(Facility.class);
        MockHttpServletRequest httpServletRequest = httpRequest();
        when(facilityService.getAllForUser(USER)).thenReturn(Arrays.asList(facility));

        List<Facility> facilities = facilityController.getAllByUser(httpServletRequest);
        assertTrue(facilities.contains(facility));
    }

    @Test
    public void shouldGetFacilityById() throws Exception {
        long Id = 1;
        facilityController.getFacility(Id);
        verify(facilityService).getFacility(Id);
    }

    @Test
    public void shouldUpdateDataReportableAndActiveForFacilityDelete() throws Exception {
        MockHttpServletRequest httpServletRequest = httpRequest();
        Facility facility = new Facility();
        facility.setId(123l);
        facility.setName("Test Facility");
        facility.setCode("Test Code");
        when(facilityService.getFacility(123l)).thenReturn(facility);

        ResponseEntity responseEntity = facilityController.updateDataReportableAndActive(facility, "delete", httpServletRequest);
        ModelMap modelMap = (ModelMap)responseEntity.getBody();

        assertThat(responseEntity.getStatusCode(),is(HttpStatus.OK));
        assertThat((String)modelMap.get("success"),is("\"Test Facility\" / \"Test Code\" deleted successfully"));
        verify(facilityService).updateDataReportableAndActiveFor(facility);
        assertThat(facility.getModifiedBy(),is(USER));
        assertThat(facility.getDataReportable(),is(false));
        assertThat(facility.getActive(),is(false));
    }

    @Test
    public void shouldUpdateDataReportableAndActiveForFacilityRestore() throws Exception {
        MockHttpServletRequest httpServletRequest = httpRequest();
        Facility facility = new Facility();
        facility.setId(123l);
        facility.setName("Test Facility");
        facility.setCode("Test Code");
        when(facilityService.getFacility(123l)).thenReturn(facility);

        ResponseEntity responseEntity = facilityController.updateDataReportableAndActive(facility, "restore", httpServletRequest);
        ModelMap modelMap = (ModelMap) responseEntity.getBody();

        assertThat(responseEntity.getStatusCode(),is(HttpStatus.OK));
        assertThat((String)modelMap.get("success"),is("\"Test Facility\" / \"Test Code\" restored successfully"));
        verify(facilityService).updateDataReportableAndActiveFor(facility);
        assertThat(facility.getDataReportable(),is(true));
    }

    private MockHttpServletRequest httpRequest() {
        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        MockHttpSession mockHttpSession = new MockHttpSession();
        httpServletRequest.setSession(mockHttpSession);
        mockHttpSession.setAttribute(USER, USER);
        return httpServletRequest;
    }



}
