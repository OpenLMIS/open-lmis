/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.upload;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.FacilityFtpDetails;
import org.openlmis.core.service.FacilityFtpDetailsService;
import org.openlmis.db.categories.UnitTests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class FacilityFtpDetailsPersistenceHandlerTest {

  @Mock
  FacilityFtpDetailsService facilityFtpDetailsService;

  @InjectMocks
  FacilityFtpDetailsPersistenceHandler persistenceHandler;

  @Test
  public void shouldGetExisting() throws Exception {
    FacilityFtpDetails facilityFtpDetails = new FacilityFtpDetails();
    Facility facility = new Facility();
    facility.setCode("F10");
    facilityFtpDetails.setFacility(facility);

    when(facilityFtpDetailsService.getByFacilityCode(facility)).thenReturn(facilityFtpDetails);

    BaseModel result = persistenceHandler.getExisting(facilityFtpDetails);

    assertThat((FacilityFtpDetails)result, is(facilityFtpDetails));
  }

  @Test
  public void shouldSaveFacilityFtpDetails() throws Exception {

    FacilityFtpDetails facilityFtpDetails = mock(FacilityFtpDetails.class);

    persistenceHandler.save(facilityFtpDetails);

    verify(facilityFtpDetailsService).save(facilityFtpDetails);
  }

}
