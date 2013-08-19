/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openlmis.core.upload;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.BaseModel;
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
    facilityFtpDetails.setFacilityCode("F10");

    when(facilityFtpDetailsService.getByFacilityCode("F10")).thenReturn(facilityFtpDetails);

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
