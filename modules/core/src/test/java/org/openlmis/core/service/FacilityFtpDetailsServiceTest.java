/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.FacilityFtpDetails;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.FacilityFtpDetailsRepository;
import org.openlmis.db.categories.UnitTests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class FacilityFtpDetailsServiceTest {

  @Mock
  FacilityFtpDetailsRepository repository;

  @Mock
  FacilityService facilityService;

  @InjectMocks
  FacilityFtpDetailsService service;

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Test
  public void shouldUpdateIfExisting() throws Exception {
    FacilityFtpDetails facilityFtpDetails = new FacilityFtpDetails();
    facilityFtpDetails.setId(1L);
    Facility facility = new Facility();
    facility.setCode("F10");
    facilityFtpDetails.setFacility(facility);

    when(facilityService.getByCode(facility)).thenReturn(facility);

    service.save(facilityFtpDetails);

    verify(repository).update(facilityFtpDetails);
  }

  @Test
  public void shouldInsertIfNotExisting() throws Exception {
    FacilityFtpDetails facilityFtpDetails = new FacilityFtpDetails();
    Facility facility = new Facility();
    facility.setCode("F10");
    facilityFtpDetails.setFacility(facility);

    when(facilityService.getByCode(facility)).thenReturn(facility);

    service.save(facilityFtpDetails);

    verify(repository).insert(facilityFtpDetails);
  }

  @Test
  public void shouldThrowExceptionIfFacilityDoesNotExist() throws Exception {

    FacilityFtpDetails facilityFtpDetails = new FacilityFtpDetails();
    Facility facility = new Facility();
    facility.setCode("F10");
    facilityFtpDetails.setFacility(facility);

    when(facilityService.getByCode(facility)).thenReturn(null);

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("error.facility.code.invalid");

    service.save(facilityFtpDetails);

    verify(service, never()).insert(facilityFtpDetails);
    verify(service, never()).update(facilityFtpDetails);
  }

  @Test
  public void shouldUpdateFacilityFtpDetails() throws Exception {
    FacilityFtpDetails facilityFtpDetails = mock(FacilityFtpDetails.class);
    service.update(facilityFtpDetails);
    verify(repository).update(facilityFtpDetails);
  }

  @Test
  public void shouldInsertFacilityFtpDetails() throws Exception {
    FacilityFtpDetails facilityFtpDetails = mock(FacilityFtpDetails.class);
    service.insert(facilityFtpDetails);
    verify(repository).insert(facilityFtpDetails);
  }

  @Test
  public void shouldGetFacilityFtpDetailsByFacilityCode() throws Exception {
    FacilityFtpDetails facilityFtpDetails = mock(FacilityFtpDetails.class);
    Facility facility = new Facility();
    facility.setCode("F10");
    facilityFtpDetails.setFacility(facility);
    when(facilityService.getByCode(facility)).thenReturn(facility);
    when(repository.getByFacilityId(facility)).thenReturn(facilityFtpDetails);
    FacilityFtpDetails result = service.getByFacilityCode(facility);
    assertThat(result, is(facilityFtpDetails));
    verify(repository).getByFacilityId(facility);
  }


}
