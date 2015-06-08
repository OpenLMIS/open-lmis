/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 *
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.restapi.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.dto.FacilityFeedDTO;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProgramSupportedService;
import org.openlmis.db.categories.UnitTests;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest(RestFacilityService.class)
public class RestFacilityServiceTest {

  @Mock
  FacilityService facilityService;
  @Mock
  ProgramSupportedService programSupportedService;

  @InjectMocks
  RestFacilityService restFacilityService;


  @Test
  public void shouldGetParentFacilityOnlyIfChildNotAnOrphan() throws Exception {

    String facilityCode = "F19";
    Long facilityId = 1L;
    Long parentFacilityId = 2L;

    Facility facility = new Facility();
    facility.setId(facilityId);
    facility.setParentFacilityId(parentFacilityId);

    Facility parentFacility = new Facility();
    parentFacility.setId(parentFacilityId);

    FacilityFeedDTO expectedFacilityFeedDTO = new FacilityFeedDTO();

    when(facilityService.getFacilityByCode(facilityCode)).thenReturn(facility);
    when(facilityService.getById(parentFacilityId)).thenReturn(parentFacility);
    whenNew(FacilityFeedDTO.class).withArguments(facility, parentFacility).thenReturn(expectedFacilityFeedDTO);


    FacilityFeedDTO facilityFeedDTO = restFacilityService.getFacilityByCode(facilityCode);

    verify(facilityService).getFacilityByCode(facilityCode);
    verify(facilityService).getById(parentFacilityId);
    assertThat(facilityFeedDTO, is(expectedFacilityFeedDTO));
    verifyNew(FacilityFeedDTO.class).withArguments(facility,parentFacility);

  }

  @Test
  public void shouldNotGetParentFacilityIfChildIsAnOrphan() throws Exception {

    String facilityCode = "F19";
    Long facilityId = 1L;

    Facility facility = new Facility();
    facility.setId(facilityId);


    FacilityFeedDTO expectedFacilityFeedDTO = new FacilityFeedDTO();

    when(facilityService.getFacilityByCode(facilityCode)).thenReturn(facility);
    Facility parentFacility = null;
    whenNew(FacilityFeedDTO.class).withArguments(facility, parentFacility).thenReturn(expectedFacilityFeedDTO);


    FacilityFeedDTO facilityFeedDTO = restFacilityService.getFacilityByCode(facilityCode);

    verify(facilityService).getFacilityByCode(facilityCode);
    assertThat(facilityFeedDTO, is(expectedFacilityFeedDTO));
    verifyNew(FacilityFeedDTO.class).withArguments(facility,parentFacility);
  }
}
