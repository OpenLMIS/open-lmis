/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.helper.CommaSeparator;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.db.categories.UnitTests;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.core.builder.FacilityBuilder.*;
import static org.openlmis.core.matchers.Matchers.dataExceptionMatcher;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@Category(UnitTests.class)
@PrepareForTest({DateTime.class})
public class FacilityRepositoryTest {

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Mock
  private FacilityMapper mapper;

  @Mock
  private GeographicZoneRepository geographicZoneRepository;

  @Mock
  private CommaSeparator commaSeparator;

  @InjectMocks
  private FacilityRepository repository;

  private DateTime now;
  private GeographicLevel defaultGeographicLevel = new GeographicLevel(1L, "levelCode", "levelName", 4);

  @Before
  public void setUp() {
    mockStatic(DateTime.class);
    now = new DateTime(2012, 10, 10, 8, 0);
    when(DateTime.now()).thenReturn(now);

    GeographicZone geographicZone = new GeographicZone();
    geographicZone.setLevel(defaultGeographicLevel);
    when(geographicZoneRepository.getByCode(GEOGRAPHIC_ZONE_CODE)).thenReturn(geographicZone);
    when(geographicZoneRepository.getLowestGeographicLevel()).thenReturn(4);
    when(mapper.getFacilityTypeForCode(FacilityBuilder.FACILITY_TYPE_CODE)).thenReturn(new FacilityType(FACILITY_TYPE_ID));
  }

  @Test
  public void shouldInsertFacility() throws Exception {
    Facility facility = make(a(defaultFacility));

    when(mapper.insert(facility)).thenReturn(1);

    repository.save(facility);

    verify(mapper).insert(facility);
  }

  @Test
  public void shouldRaiseDuplicateFacilityCodeError() throws Exception {
    Facility facility = make(a(defaultFacility));
    expectedEx.expect(dataExceptionMatcher("error.duplicate.facility.code"));

    doThrow(new DuplicateKeyException("")).when(mapper).insert(facility);

    repository.save(facility);
  }

  @Test
  public void shouldRaiseIncorrectReferenceDataError() throws Exception {
    Facility facility = make(a(defaultFacility));
    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("");
    doThrow(new DataIntegrityViolationException("foreign key")).when(mapper).insert(facility);
    repository.save(facility);
  }

  @Test
  public void shouldRaiseMissingReferenceDataError() throws Exception {
    Facility facility = make(a(defaultFacility));
    expectedEx.expect(dataExceptionMatcher("error.reference.data.missing"));

    doThrow(new DataIntegrityViolationException("violates not-null constraint")).when(mapper).insert(facility);

    repository.save(facility);
  }

  @Test
  public void shouldRaiseIncorrectDataValueError() throws Exception {
    Facility facility = make(a(defaultFacility));
    expectedEx.expect(dataExceptionMatcher("error.incorrect.length"));

    doThrow(new DataIntegrityViolationException("value too long")).when(mapper).insert(facility);

    repository.save(facility);
  }

  @Test
  public void shouldRaiseInvalidReferenceDataOperatedByError() throws Exception {
    Facility facility = make(a(defaultFacility));
    facility.getOperatedBy().setCode("invalid code");
    when(mapper.getOperatedByIdForCode("invalid code")).thenReturn(null);

    expectedEx.expect(dataExceptionMatcher("error.reference.data.invalid.operated.by"));
    repository.save(facility);
  }

  @Test
  public void shouldSetFacilityOperatorIdWhenCodeIsValid() throws Exception {
    Facility facility = make(a(defaultFacility));
    facility.getOperatedBy().setCode("valid code");
    Long facilityOperatorId = 1L;
    String operatedByCode = "operatedBy";
    String operatedByName = "operatedByName";
    when(mapper.getOperatedByIdForCode("valid code")).thenReturn(facilityOperatorId);
    FacilityOperator facilityOperator = new FacilityOperator();
    facilityOperator.setId(1l);
    facilityOperator.setCode(operatedByCode);
    facilityOperator.setText(operatedByName);
    when(mapper.getFacilityOperatorById(1l)).thenReturn(facilityOperator);

    repository.save(facility);
    assertThat(facility.getOperatedBy().getId(), is(facilityOperatorId));
    assertThat(facility.getOperatedBy().getCode(), is(operatedByCode));
    assertThat(facility.getOperatedBy().getText(), is(operatedByName));
  }

  @Test
  public void shouldRaiseInvalidReferenceDataFacilityTypeError() throws Exception {
    Facility facility = make(a(defaultFacility));
    facility.getFacilityType().setCode("invalid code");
    when(mapper.getFacilityTypeForCode("invalid code")).thenReturn(null);

    expectedEx.expect(dataExceptionMatcher("error.reference.data.invalid.facility.type"));

    repository.save(facility);
  }

  @Test
  public void shouldRaiseMissingMandatoryReferenceDataFacilityType() throws Exception {
    Facility facility = make(a(defaultFacility));
    facility.getFacilityType().setCode("");

    expectedEx.expect(dataExceptionMatcher("error.reference.data.facility.type.missing"));

    repository.save(facility);
  }

  @Test
  public void shouldSetFacilityTypeIdWhenCodeIsValid() throws Exception {
    Facility facility = make(a(defaultFacility));
    facility.getFacilityType().setCode("valid code");
    FacilityType facilityType = new FacilityType("code");
    facilityType.setId(1L);
    when(mapper.getFacilityTypeForCode("valid code")).thenReturn(facilityType);

    repository.save(facility);
    assertThat(facility.getFacilityType().getId(), is(1L));
  }

  @Test
  public void shouldGetFacilityById() throws Exception {
    Long facilityId = 1L;
    Facility facility = new Facility();
    facility.setId(facilityId);

    when(mapper.getById(facilityId)).thenReturn(facility);

    Facility returnedFacility = repository.getById(facilityId);

    assertThat(returnedFacility, is(facility));
  }

  @Test
  public void shouldUpdateFacilityIfIDIsSet() throws Exception {
    Facility facility = make(a(defaultFacility));
    facility.setId(1L);

    repository.save(facility);
    verify(mapper).update(facility);
    verify(mapper, never()).insert(facility);
  }

  @Test
  public void shouldNotUpdateFacilityIfIDIsNotSet() throws Exception {
    Facility facility = make(a(defaultFacility));
    facility.setId(null);
    repository.save(facility);
    verify(mapper, never()).update(facility);
  }

  @Test
  public void shouldUpdateEnabledActiveFlag() {
    Facility facility = make(a(defaultFacility));
    when(mapper.getById(facility.getId())).thenReturn(facility);

    Facility returnedFacility = repository.updateEnabledAndActiveFor(facility);

    assertThat(returnedFacility, is(facility));
    verify(mapper).updateEnabledAndActiveFor(facility);
    verify(mapper).getById(facility.getId());
  }

  @Test
  public void shouldReturnIdForTheGivenCode() {
    when(mapper.getIdForCode("ABC")).thenReturn(10L);
    assertThat(repository.getIdForCode("ABC"), is(10L));
  }

  @Test
  public void shouldThrowExceptionWhenCodeDoesNotExist() {
    Mockito.when(mapper.getIdForCode("ABC")).thenReturn(null);

    expectedEx.expect(dataExceptionMatcher("error.facility.code.invalid"));

    repository.getIdForCode("ABC");
  }

  @Test
  public void shouldSetGeographicZoneFromCodeAfterValidation() throws Exception {
    Facility facility = make(a(defaultFacility));
    GeographicZone existingZone = new GeographicZone();
    Long existingId = 1L;
    existingZone.setId(existingId);
    existingZone.setLevel(defaultGeographicLevel);
    when(geographicZoneRepository.getByCode(facility.getGeographicZone().getCode())).thenReturn(existingZone);

    repository.save(facility);

    assertThat(facility.getGeographicZone(), is(existingZone));
    verify(geographicZoneRepository).getByCode(GEOGRAPHIC_ZONE_CODE);
  }

  @Test
  public void shouldGiveErrorIfGeographicZoneDoesNotExist() throws Exception {
    Facility facility = make(a(defaultFacility));
    Mockito.when(geographicZoneRepository.getByCode(facility.getGeographicZone().getCode())).thenReturn(null);

    expectedEx.expect(dataExceptionMatcher("error.reference.data.invalid.geo.zone.code"));

    repository.save(facility);
  }

  @Test
  public void shouldGiveErrorIfGeographicZoneIsNotAtLowestLevel() throws Exception {
    Facility facility = make(a(defaultFacility));
    GeographicLevel geographicLevel = new GeographicLevel();
    geographicLevel.setLevelNumber(2);

    GeographicZone geographicZone = new GeographicZone();
    geographicZone.setLevel(geographicLevel);

    Mockito.when(geographicZoneRepository.getByCode(facility.getGeographicZone().getCode())).thenReturn(geographicZone);
    Mockito.when(geographicZoneRepository.getLowestGeographicLevel()).thenReturn(3);

    expectedEx.expect(dataExceptionMatcher("error.geo.zone.not.at.lowest.level"));

    repository.save(facility);
  }

  @Test
  public void shouldGetHomeFacilityForUserWithRight() throws Exception {
    Facility expectedFacility = new Facility();
    when(mapper.getHomeFacilityWithRights(1L, "{APPROVE_REQUISITION, CREATE_REQUISITION}")).thenReturn(expectedFacility);
    Facility userHomeFacility = repository.getHomeFacilityForRights(1L, RightName.APPROVE_REQUISITION, RightName.CREATE_REQUISITION);

    assertThat(userHomeFacility, is(expectedFacility));
    verify(mapper).getHomeFacilityWithRights(1L, "{APPROVE_REQUISITION, CREATE_REQUISITION}");
  }

  @Test
  public void shouldGetAllFacilitiesInDeliveryZoneForSupportedProgram() throws Exception {
    List<Facility> memberFacilities = new ArrayList<>();
    Long deliveryZoneId = 1l;
    Long programId = 1l;
    when(mapper.getAllInDeliveryZoneFor(deliveryZoneId, programId)).thenReturn(memberFacilities);

    List<Facility> facilities = repository.getAllInDeliveryZoneFor(deliveryZoneId, programId);

    assertThat(facilities, is(memberFacilities));
    verify(mapper).getAllInDeliveryZoneFor(deliveryZoneId, programId);
  }

  @Test
  public void shouldGetAllWarehouses() throws Exception {
    List<Facility> expectedWarehouses = new ArrayList<>();
    when(mapper.getEnabledWarehouses()).thenReturn(expectedWarehouses);

    List<Facility> warehouses = repository.getEnabledWarehouses();

    verify(mapper).getEnabledWarehouses();
    assertThat(warehouses, is(expectedWarehouses));
  }

  @Test
  public void shouldGetAllFacilitiesByModifiedDate() throws Exception {
    List<Facility> expectedFacilities = new ArrayList<>();
    Date dateModified = new Date();
    when(mapper.getAllByProgramSupportedModifiedDate(dateModified)).thenReturn(expectedFacilities);

    List<Facility> facilities = repository.getAllByProgramSupportedModifiedDate(dateModified);

    assertThat(facilities, is(expectedFacilities));
    verify(mapper).getAllByProgramSupportedModifiedDate(dateModified);

  }

  @Test
  public void shouldGetChildFacilities() throws Exception {
    Facility facility = new Facility(1L);
    List<Facility> expectedFacilities = asList(new Facility(5L));
    when(mapper.getChildFacilities(facility)).thenReturn(expectedFacilities);

    List<Facility> childFacilities = repository.getChildFacilities(facility);
    verify(mapper).getChildFacilities(facility);
    assertThat(childFacilities, is(expectedFacilities));
  }
}