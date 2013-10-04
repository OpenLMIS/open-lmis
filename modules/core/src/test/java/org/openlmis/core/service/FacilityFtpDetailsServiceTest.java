/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
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
