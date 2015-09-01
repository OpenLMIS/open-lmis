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
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.MessageService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.core.web.OpenLmisResponse;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ModelMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER_ID;
import static org.openlmis.core.domain.RightName.*;
import static org.openlmis.web.model.FacilityReferenceData.*;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.powermock.api.mockito.PowerMockito.when;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest({FacilityController.class, Facility.class})
public class FacilityControllerTest {

  private static final Long userId = 1L;

  @Mock
  private ProgramService programService;

  @Mock
  private FacilityService facilityService;

  @Mock
  private MessageService messageService;

  @InjectMocks
  @SuppressWarnings("unused")
  private FacilityController facilityController;

  private MockHttpServletRequest request;

  @Before
  public void setUp() {
    initMocks(this);
    request = new MockHttpServletRequest(USER, USER);
    request.getSession().setAttribute(USER_ID, userId);
  }

  @Test
  public void shouldFetchRequiredReferenceDataForFacility() {

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
  public void shouldInsertFacilityAndTagWithModifiedBy() {
    Facility facility = new Facility();
    facility.setName("test facility");
    when(messageService.message("message.facility.created.success",
      facility.getName())).thenReturn("Facility 'test facility' created successfully");

    ResponseEntity responseEntity = facilityController.insert(facility, request);

    assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
    OpenLmisResponse response = (OpenLmisResponse) responseEntity.getBody();
    assertThat(response.getSuccessMsg(), is("Facility 'test facility' created successfully"));
    verify(facilityService).update(facility);
  }

  @Test
  public void
  shouldUpdateFacilityAndTagWithModifiedByAndModifiedDate() {
    Facility facility = new Facility(1234L);
    facility.setName("test facility");
    when(messageService.message("message.facility.updated.success", facility.getName())).thenReturn(
      "Facility 'test facility' updated successfully");
    ResponseEntity responseEntity = facilityController.update(1234L, facility, request);
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
    OpenLmisResponse response = (OpenLmisResponse) responseEntity.getBody();
    assertThat(response.getSuccessMsg(), is("Facility 'test facility' updated successfully"));
    verify(facilityService).update(facility);
    assertThat(facility.getModifiedBy(), is(userId));
  }

  @Test
  public void shouldReturnErrorMessageIfInsertFails() {
    Facility facility = new Facility();
    doThrow(new DataException("error message")).when(facilityService).update(facility);
    ResponseEntity responseEntity = facilityController.insert(facility, request);
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    OpenLmisResponse response = (OpenLmisResponse) responseEntity.getBody();
    assertThat(response.getErrorMsg(), is("error message"));

    facilityController.insert(facility, request);
  }

  @Test
  public void shouldReturnErrorMessageIfUpdateFails() {
    Facility facility = new Facility(1234L);
    doThrow(new DataException("error message")).when(facilityService).update(facility);

    ResponseEntity responseEntity = facilityController.update(1234L, facility, request);

    assertThat(responseEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    OpenLmisResponse response = (OpenLmisResponse) responseEntity.getBody();
    assertThat(response.getErrorMsg(), is("error message"));
  }

  @Test
  public void shouldReturnHomeFacilityForTheUser() {
    Facility facility = mock(Facility.class);
    when(facilityService.getHomeFacility(userId)).thenReturn(facility);

    List<Facility> facilities = facilityController.getHomeFacility(request);
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
      request);
    verify(facilityService).getUserSupervisedFacilities(userId, programId, CREATE_REQUISITION, AUTHORIZE_REQUISITION);
    ModelMap map = responseEntity.getBody();
    assertThat((List<Facility>) map.get("facilities"), is(facilities));

  }

  @Test
  public void shouldSearchFacilitiesBySearchParam() throws Exception {
    String searchParam = "FAC";
    String columnName = "facility";
    Integer page = 1;
    String limit = "5";
    List<Facility> facilities = new ArrayList<>();
    Integer totalResultCount = 10;
    Pagination pagination = new Pagination(0, 0);

    whenNew(Pagination.class).withArguments(page, parseInt(limit)).thenReturn(pagination);
    when(facilityService.getTotalSearchResultCountByColumnName(searchParam, columnName)).thenReturn(totalResultCount);
    when(facilityService.searchBy(searchParam, columnName, pagination)).thenReturn(facilities);

    ResponseEntity<OpenLmisResponse> response = facilityController.get(searchParam, columnName, page, limit);

    assertThat((List<Facility>) response.getBody().getData().get(FacilityController.FACILITIES), is(facilities));
    assertThat((Pagination) response.getBody().getData().get("pagination"), is(pagination));
    assertThat(pagination.getTotalRecords(), is(totalResultCount));
    verify(facilityService).getTotalSearchResultCountByColumnName(searchParam, columnName);
    verify(facilityService).searchBy(searchParam, columnName, pagination);
  }

  @Test
  public void shouldReturnSearchedFacilitiesIfLessThanLimit() {
    String searchParam = "searchParam";
    Long facilityTypeId = 1L;
    Long geoZoneId = 2L;
    Integer count = 1;
    Boolean virtualFacility = false;
    Boolean enabled = true;
    List<Facility> facilities = asList(new Facility());
    when(facilityService.getFacilitiesCountBy(searchParam, facilityTypeId, geoZoneId, virtualFacility, enabled)).thenReturn(count);
    when(facilityService.searchFacilitiesBy(searchParam, facilityTypeId, geoZoneId, virtualFacility, enabled)).thenReturn(facilities);

    ResponseEntity<OpenLmisResponse> responseEntity = facilityController.getFilteredFacilities(
      searchParam,
      facilityTypeId, geoZoneId,
      virtualFacility, enabled, "2");

    assertThat((List<Facility>) responseEntity.getBody().getData().get("facilityList"), is(facilities));
    verify(facilityService).getFacilitiesCountBy(searchParam, facilityTypeId, geoZoneId,virtualFacility ,enabled );
    verify(facilityService).searchFacilitiesBy(searchParam, facilityTypeId, geoZoneId, virtualFacility, enabled);
  }

  @Test
  public void shouldNotReturnSearchedFacilitiesIfMoreThanLimit() {
    String searchParam = "searchParam";
    Long facilityTypeId = 1L;
    Long geoZoneId = 2L;
    Integer count = 3;
    Boolean virtualFacility = false;
    Boolean enabled = true;
    when(facilityService.getFacilitiesCountBy(searchParam, facilityTypeId, geoZoneId,virtualFacility ,enabled)).thenReturn(count);

    ResponseEntity<OpenLmisResponse> responseEntity = facilityController.getFilteredFacilities(searchParam, facilityTypeId, geoZoneId, virtualFacility, enabled, "2");

    assertThat((String) responseEntity.getBody().getData().get("message"), is("too.many.results.found"));
    verify(facilityService).getFacilitiesCountBy(searchParam, facilityTypeId, geoZoneId, virtualFacility, enabled);
    verify(facilityService, never()).searchFacilitiesBy(searchParam, facilityTypeId, geoZoneId, virtualFacility, enabled);
  }

  @Test
  public void shouldGetListOfFacilityForUserForViewing() {
    List<Facility> facilities = new ArrayList<>();
    when(facilityService.getForUserAndRights(userId, VIEW_REQUISITION)).thenReturn(facilities);

    ResponseEntity<OpenLmisResponse> response = facilityController.listForViewing(request);

    assertThat((List<Facility>) response.getBody().getData().get("facilities"), is(facilities));
    verify(facilityService).getForUserAndRights(userId, VIEW_REQUISITION);
  }

  @Test
  public void shouldSoftDeleteFacility() {
    Facility facility = new Facility();
    facility.setId(1L);
    facility.setName("Test Facility");
    facility.setCode("Test Code");
    mockStatic(Facility.class);
    when(Facility.createFacilityToBeDeleted(1L, 1L)).thenReturn(facility);

    when(facilityService.getById(facility.getId())).thenReturn(facility);
    when(messageService.message("disable.facility.success", facility.getName(), facility.getCode())).thenReturn(
      "\"Test Facility\" / \"Test Code\" deleted successfully");

    ResponseEntity<OpenLmisResponse> responseEntity = facilityController.softDelete(request, 1L);

    assertThat(responseEntity.getBody().getSuccessMsg(), is("\"Test Facility\" / \"Test Code\" deleted successfully"));
    assertThat((Facility) responseEntity.getBody().getData().get("facility"), is(facility));
    verify(facilityService).updateEnabledAndActiveFor(facility);
  }

  @Test
  public void shouldRestoreFacilityAndSetActiveToTrue() {
    Facility facility = new Facility();
    facility.setId(1L);
    facility.setName("Test Facility");
    facility.setCode("Test Code");
    mockStatic(Facility.class);
    when(Facility.createFacilityToBeRestored(1L, 1L)).thenReturn(facility);
    when(facilityService.getById(facility.getId())).thenReturn(facility);
    when(messageService.message("enable.facility.success", facility.getName(), facility.getCode())).thenReturn(
      "\"Test Facility\" / \"Test Code\" restored successfully");

    ResponseEntity<OpenLmisResponse> responseEntity = facilityController.restore(request, 1L);

    assertThat(responseEntity.getBody().getSuccessMsg(), is("\"Test Facility\" / \"Test Code\" restored successfully"));
    assertThat((Facility) responseEntity.getBody().getData().get("facility"), is(facility));
    verify(facilityService).updateEnabledAndActiveFor(facility);
  }

  @Test
  public void shouldGetFacilitiesForDeliveryZoneAndProgram() {
    mockStatic(Facility.class);
    List<Facility> facilities = new ArrayList<>();
    when(facilityService.getAllForDeliveryZoneAndProgram(1l, 1l)).thenReturn(facilities);
    when(Facility.filterForActiveProducts(facilities)).thenReturn(facilities);

    ResponseEntity<OpenLmisResponse> responseEntity = facilityController.getFacilitiesForDeliveryZoneAndProgram(1l, 1l);

    verify(facilityService).getAllForDeliveryZoneAndProgram(1l, 1l);
    assertThat((List<Facility>) responseEntity.getBody().getData().get("facilities"), is(facilities));
  }

  @Test
  public void shouldGetWarehouses() {

    List<Facility> facilities = new ArrayList<>();
    facilities.add(new Facility());
    facilities.add(new Facility());

    when(facilityService.getEnabledWarehouses()).thenReturn(facilities);

    ResponseEntity<OpenLmisResponse> warehouses = facilityController.getEnabledWarehouses();

    verify(facilityService).getEnabledWarehouses();
    assertThat((List<Facility>) warehouses.getBody().getData().get("enabledWarehouses"), is(facilities));
  }

  @Test
  public void shouldGetAllFacilityTypes() {
    List<FacilityType> types = new ArrayList<>();
    when(facilityService.getAllTypes()).thenReturn(types);

    List<FacilityType> facilityTypes = facilityController.getFacilityTypes();

    verify(facilityService).getAllTypes();
    assertThat(facilityTypes, is(types));
  }
}