/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.MessageService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.web.response.OpenLmisResponse;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
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
import static org.openlmis.core.domain.Right.*;
import static org.openlmis.web.model.FacilityReferenceData.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest(Facility.class)
public class FacilityControllerTest {

  public static final Long userId = 1L;
  @Mock
  private ProgramService programService;
  @Mock
  private FacilityService facilityService;

  @Mock
  private MessageService messageService;

  @InjectMocks
  private FacilityController facilityController;
  private MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();

  @Before
  public void setUp() throws Exception {
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
    when(programService.getAllPullPrograms()).thenReturn(allPrograms);


    Map referenceData = facilityController.getReferenceData();

    verify(facilityService).getAllOperators();
    assertThat((List<FacilityOperator>) referenceData.get(FACILITY_OPERATORS), is(equalTo(facilityOperators)));
    verify(facilityService).getAllTypes();
    assertThat((List<FacilityType>) referenceData.get(FACILITY_TYPES), is(equalTo(facilityTypes)));
    verify(facilityService).getAllZones();
    assertThat((List<GeographicZone>) referenceData.get(GEOGRAPHIC_ZONES), is(equalTo(allZones)));
    verify(programService).getAll();
    assertThat((List<Program>) referenceData.get(PROGRAMS), is(equalTo(allPrograms)));
  }

  @Test
  public void shouldInsertFacilityAndTagWithModifiedBy() throws Exception {
    Facility facility = new Facility();
    facility.setName("test facility");

    when(messageService.message("message.facility.created.success",facility.getName())).thenReturn("Facility 'test facility' created successfully");

    ResponseEntity responseEntity = facilityController.insert(facility, httpServletRequest);
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
    OpenLmisResponse response = (OpenLmisResponse) responseEntity.getBody();
    assertThat(response.getSuccessMsg(), is("Facility 'test facility' created successfully"));
    verify(facilityService).insert(facility);
    assertThat(facility.getModifiedBy(), is(userId));
  }

  @Test
  public void
  shouldUpdateFacilityAndTagWithModifiedByAndModifiedDate() throws Exception {
    Facility facility = new Facility();
    facility.setName("test facility");
    when(messageService.message("message.facility.updated.success",facility.getName())).thenReturn("Facility 'test facility' updated successfully");
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
    Long id = 1L;
    facilityController.getFacility(id);
    verify(facilityService).getById(id);
  }

  @Test
  public void shouldReturnUserSupervisedFacilitiesForAProgram() {
    Long programId = 1L;
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

    assertThat(returnedFacilities, is(facilities));
  }

  @Test
  public void shouldGetListOfFacilityForUserForViewing() throws Exception {
    List<Facility> facilities = new ArrayList<>();
    when(facilityService.getForUserAndRights(userId, VIEW_REQUISITION)).thenReturn(facilities);

    ResponseEntity<OpenLmisResponse> response = facilityController.listForViewing(httpServletRequest);

    assertThat((List<Facility>) response.getBody().getData().get("facilities"), is(facilities));
    verify(facilityService).getForUserAndRights(userId, VIEW_REQUISITION);
  }

  @Test
  public void shouldSoftDeleteFacility() throws Exception {
    Facility facility = new Facility();
    facility.setName("Test Facility");
    facility.setCode("Test Code");
    mockStatic(Facility.class);
    when(Facility.createFacilityToBeDeleted(1L, 1L)).thenReturn(facility);

    when(facilityService.updateDataReportableAndActiveFor(facility)).thenReturn(facility);
    when(messageService.message("delete.facility.success",facility.getName(),facility.getCode())).thenReturn("\"Test Facility\" / \"Test Code\" deleted successfully");

    ResponseEntity<OpenLmisResponse> responseEntity = facilityController.softDelete(httpServletRequest, 1L);

    assertThat(responseEntity.getBody().getSuccessMsg(), is("\"Test Facility\" / \"Test Code\" deleted successfully"));
    assertThat((Facility) responseEntity.getBody().getData().get("facility"), is(facility));
    verify(facilityService).updateDataReportableAndActiveFor(facility);
  }

  @Test
  public void shouldRestoreFacilityAndSetActiveToTrue() throws Exception {
    Facility facility = new Facility();
    facility.setName("Test Facility");
    facility.setCode("Test Code");
    mockStatic(Facility.class);
    when(Facility.createFacilityToBeRestored(1L, 1L, true)).thenReturn(facility);

    when(facilityService.updateDataReportableAndActiveFor(facility)).thenReturn(facility);
    when(messageService.message("restore.facility.success",facility.getName(),facility.getCode())).thenReturn("\"Test Facility\" / \"Test Code\" restored successfully");

    ResponseEntity<OpenLmisResponse> responseEntity = facilityController.restore(httpServletRequest, 1L, true);

    assertThat(responseEntity.getBody().getSuccessMsg(), is("\"Test Facility\" / \"Test Code\" restored successfully"));
    assertThat((Facility) responseEntity.getBody().getData().get("facility"), is(facility));
    verify(facilityService).updateDataReportableAndActiveFor(facility);
  }

  private MockHttpServletRequest httpRequest() {
    MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
    MockHttpSession mockHttpSession = new MockHttpSession();
    httpServletRequest.setSession(mockHttpSession);
    mockHttpSession.setAttribute(USER, USER);
    return httpServletRequest;
  }


}
