/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.db.categories.UnitTests;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;

import java.util.Arrays;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.core.builder.FacilityBuilder.*;
import static org.openlmis.core.matchers.Matchers.dataExceptionMatcher;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest({DateTime.class})
public class FacilityRepositoryTest {
  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Mock
  private FacilityMapper mapper;

  @Mock
  private GeographicZoneRepository geographicZoneRepository;

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
    repository = new FacilityRepository(mapper, null, geographicZoneRepository);
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

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Invalid reference data 'Operated By'");
    repository.save(facility);
  }

  @Test
  public void shouldSetFacilityOperatorIdWhenCodeIsValid() throws Exception {
    Facility facility = make(a(defaultFacility));
    facility.getOperatedBy().setCode("valid code");
    when(mapper.getOperatedByIdForCode("valid code")).thenReturn(1L);

    repository.save(facility);
    assertThat(facility.getOperatedBy().getId(), is(1L));
  }

  @Test
  public void shouldRaiseInvalidReferenceDataFacilityTypeError() throws Exception {
    Facility facility = make(a(defaultFacility));
    facility.getFacilityType().setCode("invalid code");
    when(mapper.getFacilityTypeForCode("invalid code")).thenReturn(null);

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Invalid reference data 'Facility Type'");
    repository.save(facility);
  }

  @Test
  public void shouldRaiseMissingMandatoryReferenceDataFacilityType() throws Exception {
    Facility facility = make(a(defaultFacility));
    facility.getFacilityType().setCode("");

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Missing mandatory reference data 'Facility Type'");
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
  public void shouldUpdateDataReportableActiveFlag() {
    Facility facility = make(a(defaultFacility));
    when(mapper.getById(facility.getId())).thenReturn(facility);

    Facility returnedFacility = repository.updateDataReportableAndActiveFor(facility);

    assertThat(returnedFacility, is(facility));
    verify(mapper).updateDataReportableAndActiveFor(facility);
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
    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Invalid Facility Code");
    repository.getIdForCode("ABC");
  }

  @Test
  public void shouldSearchFacilitiesByCodeOrName() throws Exception {
    List<Facility> facilityList = Arrays.asList(new Facility());
    when(mapper.searchFacilitiesByCodeOrName("query")).thenReturn(facilityList);

    List<Facility> returnedFacilities = repository.searchFacilitiesByCodeOrName("query");

    assertThat(returnedFacilities, is(facilityList));
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

    expectedEx.expect(dataExceptionMatcher("error.invalid.reference.data.geo.zone.code"));

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
    Facility userHomeFacility = repository.getHomeFacilityForRights(1L, Right.APPROVE_REQUISITION, Right.CREATE_REQUISITION);

    assertThat(userHomeFacility, is(expectedFacility));
    verify(mapper).getHomeFacilityWithRights(1L, "{APPROVE_REQUISITION, CREATE_REQUISITION}");
  }

}