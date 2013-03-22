/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.web.model.FacilityReferenceData;
import org.openlmis.web.response.OpenLmisResponse;
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
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER_ID;
import static org.openlmis.core.domain.Right.AUTHORIZE_REQUISITION;
import static org.openlmis.core.domain.Right.CREATE_REQUISITION;
import static org.openlmis.core.domain.Right.VIEW_REQUISITION;

public class FacilityControllerTest {

  public static final Integer userId = 1;
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
    mockHttpSession.setAttribute(USER, USER);
    mockHttpSession.setAttribute(USER_ID, userId);
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
    assertThat((List<FacilityOperator>) referenceData.get(FacilityReferenceData.FACILITY_OPERATORS), is(equalTo(facilityOperators)));
    verify(facilityService).getAllTypes();
    assertThat((List<FacilityType>) referenceData.get(FacilityReferenceData.FACILITY_TYPES), is(equalTo(facilityTypes)));
    verify(facilityService).getAllZones();
    assertThat((List<GeographicZone>) referenceData.get(FacilityReferenceData.GEOGRAPHIC_ZONES), is(equalTo(allZones)));
    verify(programService).getAll();
    assertThat((List<Program>) referenceData.get(FacilityReferenceData.PROGRAMS), is(equalTo(allPrograms)));
  }

  @Test
  public void shouldInsertFacilityAndTagWithModifiedBy() throws Exception {
    Facility facility = new Facility();
    facility.setName("test facility");
    ResponseEntity responseEntity = facilityController.insert(facility, httpServletRequest);
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
    OpenLmisResponse response = (OpenLmisResponse) responseEntity.getBody();
    assertThat(response.getSuccessMsg(), is("Facility 'test facility' created successfully"));
    verify(facilityService).insert(facility);
    assertThat(facility.getModifiedBy(), is(userId));
  }

  @Test
  public void shouldUpdateFacilityAndTagWithModifiedBy() throws Exception {
    Facility facility = new Facility();
    facility.setName("test facility");
    ResponseEntity responseEntity = facilityController.update(facility, httpServletRequest);
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
    OpenLmisResponse response = (OpenLmisResponse) responseEntity.getBody();
    assertThat(response.getSuccessMsg(), is("Facility 'test facility' updated successfully"));
    verify(facilityService).update(facility);
    assertThat(facility.getModifiedBy(), is(userId));
  }

  @Test
  public void shouldReturnErrorMessageIfInsertFails() throws Exception {
    Facility facility = new Facility();
    doThrow(new DataException("error message")).when(facilityService).insert(facility);
    ResponseEntity responseEntity = facilityController.insert(facility, httpServletRequest);
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    OpenLmisResponse response = (OpenLmisResponse) responseEntity.getBody();
    assertThat(response.getErrorMsg(), is("error message"));
    MockHttpServletRequest httpServletRequest = httpRequest();

    facilityController.insert(facility, httpServletRequest);
  }

  @Test
  public void shouldReturnErrorMessageIfUpdateFails() throws Exception {
    Facility facility = new Facility();
    doThrow(new DataException("error message")).when(facilityService).update(facility);
    ResponseEntity responseEntity = facilityController.update(facility, httpServletRequest);
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    OpenLmisResponse response = (OpenLmisResponse) responseEntity.getBody();
    assertThat(response.getErrorMsg(), is("error message"));
    MockHttpServletRequest httpServletRequest = httpRequest();

    facilityController.update(facility, httpServletRequest);
  }

  @Test
  public void shouldReturnHomeFacilityForTheUser() {
    Facility facility = mock(Facility.class);
    when(facilityService.getHomeFacility(userId)).thenReturn(facility);

    List<Facility> facilities = facilityController.getHomeFacility(httpServletRequest);
    assertTrue(facilities.contains(facility));
  }

  @Test
  public void shouldGetFacilityById() throws Exception {
    Integer Id = 1;
    facilityController.getFacility(Id);
    verify(facilityService).getById(Id);
  }

  @Test
  public void shouldUpdateDataReportableAndActiveForFacilityDelete() throws Exception {
    Facility facility = new Facility();
    facility.setId(123);
    facility.setName("Test Facility");
    facility.setCode("Test Code");
    when(facilityService.getById(123)).thenReturn(facility);

    ResponseEntity responseEntity = facilityController.updateDataReportableAndActive(facility, "delete", httpServletRequest);
    OpenLmisResponse response = (OpenLmisResponse) responseEntity.getBody();

    assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
    assertThat(response.getSuccessMsg(), is("\"Test Facility\" / \"Test Code\" deleted successfully"));
    verify(facilityService).updateDataReportableAndActiveFor(facility);
    assertThat(facility.getModifiedBy(), is(userId));
    assertThat(facility.getDataReportable(), is(false));
    assertThat(facility.getActive(), is(false));
  }

  @Test
  public void shouldUpdateDataReportableAndActiveForFacilityRestore() throws Exception {
    MockHttpServletRequest httpServletRequest = httpRequest();
    Facility facility = new Facility();
    facility.setId(123);
    facility.setName("Test Facility");
    facility.setCode("Test Code");
    when(facilityService.getById(123)).thenReturn(facility);

    ResponseEntity responseEntity = facilityController.updateDataReportableAndActive(facility, "restore", httpServletRequest);
    OpenLmisResponse response = (OpenLmisResponse) responseEntity.getBody();

    assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
    assertThat(response.getSuccessMsg(), is("\"Test Facility\" / \"Test Code\" restored successfully"));
    verify(facilityService).updateDataReportableAndActiveFor(facility);
    assertThat(facility.getDataReportable(), is(true));
  }

  @Test
  public void shouldReturnUserSupervisedFacilitiesForAProgram() {
    Integer programId = 1;
    List<Facility> facilities = new ArrayList<>();
    when(facilityService.getUserSupervisedFacilities(userId, programId, CREATE_REQUISITION, AUTHORIZE_REQUISITION)).thenReturn(facilities);
    ResponseEntity<ModelMap> responseEntity = facilityController.getUserSupervisedFacilitiesSupportingProgram(programId, httpServletRequest);
    verify(facilityService).getUserSupervisedFacilities(userId, programId, CREATE_REQUISITION, AUTHORIZE_REQUISITION);
    ModelMap map = responseEntity.getBody();
    assertThat((List<Facility>) map.get("facilities"), is(facilities));

  }

  @Test
  public void shouldSearchFacilitiesByCodeOrName() throws Exception {
    List<Facility> facilities = Arrays.asList(new Facility());
    when(facilityService.searchFacilitiesByCodeOrName("searchParam")).thenReturn(facilities);

    List<Facility> returnedFacilities = facilityController.get("searchParam");

    assertThat(returnedFacilities,is(facilities));
  }

  @Test
  public void shouldGetListOfFacilityForUserForViewing() throws Exception {
    List<Facility> facilities = new ArrayList<>();
    when(facilityService.getForUserAndRights(userId, VIEW_REQUISITION)).thenReturn(facilities);

    ResponseEntity<OpenLmisResponse> response = facilityController.listForViewing(httpServletRequest);

    assertThat((List<Facility>) response.getBody().getData().get("facilities"), is(facilities));
    verify(facilityService).getForUserAndRights(userId, VIEW_REQUISITION);
  }

  private MockHttpServletRequest httpRequest() {
    MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
    MockHttpSession mockHttpSession = new MockHttpSession();
    httpServletRequest.setSession(mockHttpSession);
    mockHttpSession.setAttribute(USER, USER);
    return httpServletRequest;
  }


}
