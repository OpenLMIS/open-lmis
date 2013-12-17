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

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.*;
import org.openlmis.core.service.FacilityService;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.distribution.domain.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@Category(IntegrationTests.class)
@RunWith(MockitoJUnitRunner.class)
public class FacilityDistributionServiceTest {

  @Mock
  FacilityService facilityService;

  @Mock
  EpiUseService epiUseService;

  @Mock
  FacilityVisitService facilityVisitService;

  @InjectMocks
  FacilityDistributionService facilityDistributionService;

  @Test
  public void shouldGetFacilityDistributionDataForADistribution() throws Exception {
    Distribution distribution = new Distribution();
    DeliveryZone deliveryZone = new DeliveryZone(1L);
    Program program = new Program(3L);
    distribution.setDeliveryZone(deliveryZone);
    distribution.setProgram(program);
    FacilityDistributionService spyFacilityDistributionService = spy(facilityDistributionService);
    Facility facility = new Facility(1234L);
    List<Facility> facilities = asList(facility);
    when(facilityService.getAllForDeliveryZoneAndProgram(1L, 3L)).thenReturn(facilities);

    FacilityDistribution facilityDistribution = new FacilityDistribution(null, null);
    doReturn(facilityDistribution).when(spyFacilityDistributionService).createDistributionData(facility, distribution);

    Map<Long, FacilityDistribution> facilityDistributionDataMap = spyFacilityDistributionService.createFor(distribution);

    assertThat(facilityDistributionDataMap.get(1234L), is(facilityDistribution));
  }

  @Test
  public void shouldGetFacilityDistributionDataForAFacilityAndDistribution() throws Exception {
    Facility facility = new Facility(2L);
    ProgramSupported programSupported = new ProgramSupported(1L, true, new Date());

    FacilityProgramProduct facilityProgramProduct1 = mock(FacilityProgramProduct.class);
    FacilityProgramProduct facilityProgramProduct2 = mock(FacilityProgramProduct.class);

    when(facilityProgramProduct1.getActiveProductGroup()).thenReturn(new ProductGroup("PG1", "PG1"));
    when(facilityProgramProduct2.getActiveProductGroup()).thenReturn(new ProductGroup("PG2", "PG2"));

    programSupported.setProgramProducts(asList(facilityProgramProduct1, facilityProgramProduct2));
    facility.setSupportedPrograms(asList(programSupported));

    Distribution distribution = new Distribution();
    distribution.setId(1L);

    FacilityDistribution distributionData = facilityDistributionService.createDistributionData(facility, distribution);

    EpiUse epiUse = distributionData.getEpiUse();

    assertThat(epiUse.getDistributionId(), is(distribution.getId()));
    assertThat(epiUse.getFacilityId(), is(facility.getId()));
    assertThat(epiUse.getLineItems().size(), is(2));
    verify(epiUseService).save(epiUse);
  }

  @Test
  public void shouldNotGetProductGroupForAllInactiveProducts() throws Exception {
    Facility facility = new Facility(2L);
    ProgramSupported programSupported = new ProgramSupported(1L, true, new Date());

    FacilityProgramProduct facilityProgramProduct1 = mock(FacilityProgramProduct.class);
    FacilityProgramProduct facilityProgramProduct2 = mock(FacilityProgramProduct.class);

    when(facilityProgramProduct1.getActiveProductGroup()).thenReturn(new ProductGroup("PG1", "PG1"));
    when(facilityProgramProduct2.getActiveProductGroup()).thenReturn(null);

    programSupported.setProgramProducts(asList(facilityProgramProduct1, facilityProgramProduct2));
    facility.setSupportedPrograms(asList(programSupported));

    Distribution distribution = new Distribution();
    distribution.setId(1L);

    FacilityDistribution distributionData = facilityDistributionService.createDistributionData(facility, distribution);

    List<EpiUseLineItem> lineItems = distributionData.getEpiUse().getLineItems();
    assertThat(lineItems.size(), is(1));
    assertThat(lineItems.get(0).getProductGroup().getCode(), is("PG1"));
    assertThat(lineItems.get(0).getProductGroup().getName(), is("PG1"));
  }

  @Test
  public void shouldSaveFacilityVisitAndEpiUse() throws Exception {
    EpiUse epiUse = new EpiUse();
    FacilityVisit facilityVisit = new FacilityVisit();
    FacilityDistribution facilityDistribution = new FacilityDistribution(facilityVisit, epiUse);

    when(facilityVisitService.save(facilityVisit)).thenReturn(true);
    boolean saveStatus = facilityDistributionService.save(facilityDistribution);

    verify(facilityVisitService).save(facilityVisit);
    verify(epiUseService).save(epiUse);
    assertTrue(saveStatus);
  }
}
