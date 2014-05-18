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
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
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
    when(messageService.message("message.facility.created.success",
      facility.getName())).thenReturn("Facility 'test facility' created successfully");

    ResponseEntity responseEntity = facilityController.insert(facility, httpServletRequest);

    assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
    OpenLmisResponse response = (OpenLmisResponse) responseEntity.getBody();
    assertThat(response.getSuccessMsg(), is("Facility 'test facility' created successfully"));
    verify(facilityService).update(facility);
  }

  @Test
  public void
  shouldUpdateFacilityAndTagWithModifiedByAndModifiedDate() throws Exception {
    Facility facility = new Facility(1234L);
    facility.setName("test facility");
    when(messageService.message("message.facility.updated.success", facility.getName())).thenReturn(
      "Facility 'test facility' updated successfully");
    ResponseEntity responseEntity = facilityController.update(1234L, facility, httpServletRequest);
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
    OpenLmisResponse response = (OpenLmisResponse) responseEntity.getBody();
    assertThat(response.getSuccessMsg(), is("Facility 'test facility' updated successfully"));
    verify(facilityService).update(facility);
    assertThat(facility.getModifiedBy(), is(userId));
  }

  @Test
  public void shouldReturnErrorMessageIfInsertFails() throws Exception {
    Facility facility = new Facility();
    doThrow(new DataException("error message")).when(facilityService).update(facility);
    ResponseEntity responseEntity = facilityController.insert(facility, httpServletRequest);
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    OpenLmisResponse response = (OpenLmisResponse) responseEntity.getBody();
    assertThat(response.getErrorMsg(), is("error message"));
    MockHttpServletRequest httpServletRequest = httpRequest();

    facilityController.insert(facility, httpServletRequest);
  }

  @Test
  public void shouldReturnErrorMessageIfUpdateFails() throws Exception {
    Facility facility = new Facility(1234L);
    doThrow(new DataException("error message")).when(facilityService).update(facility);

    ResponseEntity responseEntity = facilityController.update(1234L, facility, httpServletRequest);

    assertThat(responseEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    OpenLmisResponse response = (OpenLmisResponse) responseEntity.getBody();
    assertThat(response.getErrorMsg(), is("error message"));
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
    Facility facility = new Facility();
    when(facilityService.getById(id)).thenReturn(facility);

    ResponseEntity<OpenLmisResponse> responseEntity = facilityController.getFacility(id);

    assertThat((Facility) responseEntity.getBody().getData().get("facility"), is(facility));
    verify(facilityService).getById(id);
  }

  @Test
  public void shouldReturnUserSupervisedFacilitiesForAProgram() {
    Long programId = 1L;
    List<Facility> facilities = new ArrayList<>();
    when(facilityService.getUserSupervisedFacilities(userId, programId, CREATE_REQUISITION,
      AUTHORIZE_REQUISITION)).thenReturn(facilities);
    ResponseEntity<ModelMap> responseEntity = facilityController.getUserSupervisedFacilitiesSupportingProgram(programId,
      httpServletRequest);
    verify(facilityService).getUserSupervisedFacilities(userId, programId, CREATE_REQUISITION, AUTHORIZE_REQUISITION);
    ModelMap map = responseEntity.getBody();
    assertThat((List<Facility>) map.get("facilities"), is(facilities));

  }

  @Test
  public void shouldGetAllFacilitiesIfSearchParamMissing() throws Exception {
    List<Facility> facilities = asList(new Facility());
    when(facilityService.getAll()).thenReturn(facilities);

    ResponseEntity<OpenLmisResponse> responseEntity = facilityController.get(null, false, "0");

    verify(facilityService).getAll();
    assertThat((List<Facility>) responseEntity.getBody().getData().get("facilityList"), is(facilities));
  }

  @Test
  public void shouldReturnSearchedFacilitiesIfLessThanLimit() throws Exception {
    Boolean virtualFacility = false;
    String searchParam = "searchParam";
    Integer count = 1;
    List<Facility> facilities = asList(new Facility());
    when(facilityService.getTotalSearchedFacilitiesByCodeOrName(searchParam)).thenReturn(count);
    when(facilityService.searchFacilitiesByCodeOrNameAndVirtualFacilityFlag(searchParam, virtualFacility)).thenReturn(
      facilities);

    ResponseEntity<OpenLmisResponse> responseEntity = facilityController.get(searchParam, virtualFacility, "2");

    assertThat((List<Facility>) responseEntity.getBody().getData().get("facilityList"), is(facilities));
    verify(facilityService).getTotalSearchedFacilitiesByCodeOrName(searchParam);
    verify(facilityService).searchFacilitiesByCodeOrNameAndVirtualFacilityFlag(searchParam, virtualFacility);
    verify(facilityService, never()).getAll();
  }

  @Test
  public void shouldNotReturnSearchedFacilitiesIfMoreThanLimit() throws Exception {
    Boolean virtualFacility = false;
    String searchParam = "searchParam";
    Integer count = 3;
    List<Facility> facilities = asList(new Facility());
    when(facilityService.getTotalSearchedFacilitiesByCodeOrName(searchParam)).thenReturn(count);

    ResponseEntity<OpenLmisResponse> responseEntity = facilityController.get(searchParam, virtualFacility, "2");

    assertThat((String) responseEntity.getBody().getData().get("message"), is("too.many.results.found"));
    verify(facilityService).getTotalSearchedFacilitiesByCodeOrName(searchParam);
    verify(facilityService, never()).searchFacilitiesByCodeOrNameAndVirtualFacilityFlag(searchParam, virtualFacility);
    verify(facilityService, never()).getAll();
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
    facility.setId(1L);
    facility.setName("Test Facility");
    facility.setCode("Test Code");
    mockStatic(Facility.class);
    when(Facility.createFacilityToBeDeleted(1L, 1L)).thenReturn(facility);

    when(facilityService.getById(facility.getId())).thenReturn(facility);
    when(messageService.message("disable.facility.success", facility.getName(), facility.getCode())).thenReturn(
      "\"Test Facility\" / \"Test Code\" deleted successfully");

    ResponseEntity<OpenLmisResponse> responseEntity = facilityController.softDelete(httpServletRequest, 1L);

    assertThat(responseEntity.getBody().getSuccessMsg(), is("\"Test Facility\" / \"Test Code\" deleted successfully"));
    assertThat((Facility) responseEntity.getBody().getData().get("facility"), is(facility));
    verify(facilityService).updateEnabledAndActiveFor(facility);
  }

  @Test
  public void shouldRestoreFacilityAndSetActiveToTrue() throws Exception {
    Facility facility = new Facility();
    facility.setId(1L);
    facility.setName("Test Facility");
    facility.setCode("Test Code");
    mockStatic(Facility.class);
    when(Facility.createFacilityToBeRestored(1L, 1L)).thenReturn(facility);
    when(facilityService.getById(facility.getId())).thenReturn(facility);
    when(messageService.message("enable.facility.success", facility.getName(), facility.getCode())).thenReturn(
      "\"Test Facility\" / \"Test Code\" restored successfully");

    ResponseEntity<OpenLmisResponse> responseEntity = facilityController.restore(httpServletRequest, 1L);

    assertThat(responseEntity.getBody().getSuccessMsg(), is("\"Test Facility\" / \"Test Code\" restored successfully"));
    assertThat((Facility) responseEntity.getBody().getData().get("facility"), is(facility));
    verify(facilityService).updateEnabledAndActiveFor(facility);
  }

  @Test
  public void shouldGetFacilitiesForDeliveryZoneAndProgram() throws Exception {
    mockStatic(Facility.class);
    List<Facility> facilities = new ArrayList<>();
    Mockito.when(facilityService.getAllForDeliveryZoneAndProgram(1l, 1l)).thenReturn(facilities);
    when(Facility.filterForActiveProducts(facilities)).thenReturn(facilities);

    ResponseEntity<OpenLmisResponse> responseEntity = facilityController.getFacilitiesForDeliveryZoneAndProgram(1l, 1l);

    verify(facilityService).getAllForDeliveryZoneAndProgram(1l, 1l);
    assertThat((List<Facility>) responseEntity.getBody().getData().get("facilities"), is(facilities));
  }

  @Test
  public void shouldGetWarehouses() throws Exception {

    List<Facility> facilities = new ArrayList<>();
    facilities.add(new Facility());
    facilities.add(new Facility());

    when(facilityService.getEnabledWarehouses()).thenReturn(facilities);

    ResponseEntity<OpenLmisResponse> warehouses = facilityController.getEnabledWarehouses();

    verify(facilityService).getEnabledWarehouses();
    assertThat((List<Facility>) warehouses.getBody().getData().get("enabledWarehouses"), is(facilities));

  }

  private MockHttpServletRequest httpRequest() {
    MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
    MockHttpSession mockHttpSession = new MockHttpSession();
    httpServletRequest.setSession(mockHttpSession);
    mockHttpSession.setAttribute(USER, USER);
    return httpServletRequest;
  }
}
