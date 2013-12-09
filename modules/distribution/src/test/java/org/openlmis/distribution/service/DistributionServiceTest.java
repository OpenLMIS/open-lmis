/*
 *
 *  * This program is part of the OpenLMIS logistics management information system platform software.
 *  * Copyright © 2013 VillageReach
 *  *
 *  * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  *  
 *  * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 *
 */

package org.openlmis.distribution.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.distribution.domain.Distribution;
import org.openlmis.distribution.domain.FacilityDistributionData;
import org.openlmis.distribution.domain.FacilityVisit;
import org.openlmis.distribution.repository.DistributionRepository;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class DistributionServiceTest {

  @InjectMocks
  DistributionService service;

  @Mock
  FacilityVisitService facilityVisitService;

  @Mock
  FacilityDistributionDataService facilityDistributionDataService;

  @Mock
  DistributionRepository repository;

  @Test
  public void shouldCreateDistribution() throws Exception {
    Distribution distribution = new Distribution();
    Distribution expectedDistribution = new Distribution();
    when(repository.create(distribution)).thenReturn(expectedDistribution);
    List<FacilityDistributionData> facilityDistributions = new ArrayList<>();
    when(facilityDistributionDataService.getFor(expectedDistribution)).thenReturn(facilityDistributions);

    Distribution initiatedDistribution = service.create(distribution);

    verify(repository).create(distribution);
    assertThat(initiatedDistribution, is(expectedDistribution));
    assertThat(initiatedDistribution.getFacilityDistributions(), is(facilityDistributions));
  }

  @Test
  public void shouldSyncFacilityDistributionDataAndReturnSyncStatus() {
    FacilityVisit facilityVisit = new FacilityVisit();

    FacilityDistributionData facilityDistributionData = mock(FacilityDistributionData.class);
    when(facilityDistributionData.getFacilityVisit()).thenReturn(facilityVisit);
    when(facilityVisitService.save(facilityVisit)).thenReturn("Synced");
    String syncStatus = service.sync(facilityDistributionData);

    verify(facilityVisitService).save(facilityVisit);
    verify(facilityDistributionData).getFacilityVisit();
    assertThat(syncStatus, is("Synced"));
  }

  @Test
  public void shouldGetDistributionIfExists() throws Exception {
    service.get(new Distribution());

    verify(repository).get(new Distribution());
  }
}
