/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.distribution.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Refrigerator;
import org.openlmis.core.service.RefrigeratorService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.distribution.domain.DistributionRefrigerators;
import org.openlmis.distribution.domain.FacilityVisit;
import org.openlmis.distribution.domain.RefrigeratorReading;
import org.openlmis.distribution.repository.DistributionRefrigeratorsRepository;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class DistributionRefrigeratorsServiceTest {

  @Mock
  private RefrigeratorService refrigeratorService;

  @Mock
  private DistributionRefrigeratorsRepository repository;

  @Mock
  private FacilityVisitService facilityVisitService;

  @InjectMocks
  private DistributionRefrigeratorsService service;

  private Long facilityId;
  private Long facilityVisitId;
  private Long refrigeratorId;
  private Long createdBy;

  private FacilityVisit facilityVisit;

  @Before
  public void setUp() throws Exception {
    facilityId = 1L;
    facilityVisitId = 2L;
    refrigeratorId = 3L;
    createdBy = 4L;
    facilityVisit = new FacilityVisit();
    facilityVisit.setId(facilityVisitId);
    when(facilityVisitService.getById(facilityVisitId)).thenReturn(facilityVisit);

  }

  @Test
  public void shouldDisableAllRefrigeratorsForAFacilityBeforeSynchronizingTheRefrigerators() {
    Refrigerator refrigerator = new Refrigerator();
    RefrigeratorReading refrigeratorReading = new RefrigeratorReading(refrigerator);
    DistributionRefrigerators distributionRefrigerators = new DistributionRefrigerators(facilityVisit, asList(refrigeratorReading));

    service.save(facilityId, distributionRefrigerators);

    verify(refrigeratorService).disableAllFor(facilityId);
  }


  @Test
  public void shouldGetAllRefrigeratorsByFacilityBeforeSync() {
    Refrigerator refrigerator = new Refrigerator();
    RefrigeratorReading refrigeratorReading = new RefrigeratorReading(refrigerator);
    DistributionRefrigerators distributionRefrigerators = new DistributionRefrigerators(facilityVisit, asList(refrigeratorReading));

    service.save(facilityId, distributionRefrigerators);

    verify(refrigeratorService).getAllBy(facilityId);
  }

  @Test
  public void shouldSetIdIfRefrigeratorAlreadyExists() {
    Refrigerator refrigerator = new Refrigerator("serialNumber");
    Refrigerator existingRefrigerator = new Refrigerator("serialNumber");
    existingRefrigerator.setId(refrigeratorId);
    RefrigeratorReading refrigeratorReading = new RefrigeratorReading(refrigerator);
    DistributionRefrigerators distributionRefrigerators = new DistributionRefrigerators(facilityVisit, asList(refrigeratorReading));
    distributionRefrigerators.setCreatedBy(createdBy);
    when(refrigeratorService.getAllBy(facilityId)).thenReturn(asList(existingRefrigerator));

    service.save(facilityId, distributionRefrigerators);

    assertThat(refrigerator.getId(), is(refrigeratorId));
    verify(refrigeratorService).save(refrigerator);
  }

  @Test
  public void shouldNotSetIdIfSynchronizingANewRefrigerator() throws Exception {
    Refrigerator refrigerator = new Refrigerator("serialNumberNew");
    Refrigerator existingRefrigerator = new Refrigerator("serialNumber");
    existingRefrigerator.setId(refrigeratorId);
    RefrigeratorReading refrigeratorReading = new RefrigeratorReading(refrigerator);
    DistributionRefrigerators distributionRefrigerators = new DistributionRefrigerators(facilityVisit, asList(refrigeratorReading));
    distributionRefrigerators.setCreatedBy(createdBy);
    when(refrigeratorService.getAllBy(facilityId)).thenReturn(asList(existingRefrigerator));

    service.save(facilityId, distributionRefrigerators);

    assertThat(refrigerator.getId(), is(nullValue()));
    assertThat(refrigerator.getFacilityId(), is(facilityId));
    verify(refrigeratorService).save(refrigerator);
  }


}
