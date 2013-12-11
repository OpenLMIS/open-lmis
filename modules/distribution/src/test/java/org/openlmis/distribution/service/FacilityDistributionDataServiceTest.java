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
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.service.FacilityService;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.distribution.domain.Distribution;
import org.openlmis.distribution.domain.EpiUse;
import org.openlmis.distribution.domain.EpiUseLineItem;
import org.openlmis.distribution.domain.FacilityDistributionData;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@Category(IntegrationTests.class)
@RunWith(MockitoJUnitRunner.class)
public class FacilityDistributionDataServiceTest {

  @Mock
  FacilityService facilityService;
  @Mock
  EpiUseService epiUseService;

  @InjectMocks
  FacilityDistributionDataService facilityDistributionDataService;

  @Test
  public void shouldGetFacilityDistributionDataForADistribution() throws Exception {
    Distribution distribution = new Distribution();
    DeliveryZone deliveryZone = new DeliveryZone(1L);
    Program program = new Program(3L);
    distribution.setDeliveryZone(deliveryZone);
    distribution.setProgram(program);
    FacilityDistributionDataService spyFacilityDistributionDataService = spy(facilityDistributionDataService);
    Facility facility = new Facility(1234L);
    List<Facility> facilities = asList(facility);
    when(facilityService.getAllForDeliveryZoneAndProgram(1L, 3L)).thenReturn(facilities);

    FacilityDistributionData facilityDistributionData = new FacilityDistributionData();
    when(spyFacilityDistributionDataService.createDistributionData(facility, distribution)).thenReturn(facilityDistributionData);

    Map<Long, FacilityDistributionData> facilityDistributionDataMap = spyFacilityDistributionDataService.getFor(distribution);

    assertThat(facilityDistributionDataMap.get(1234L), is(facilityDistributionData));
  }

  @Test
  public void shouldGetFacilityDistributionDataForAFacilityAndDistribution() throws Exception {
    Facility facility = new Facility(2L);
    ProgramSupported programSupported = new ProgramSupported(1L, true, new Date());

    ProgramProduct programProduct1 = new ProgramProduct();
    Product product1 = make(a(ProductBuilder.defaultProduct));
    programProduct1.setProduct(product1);
    programProduct1.setActive(true);

    ProgramProduct programProduct2 = new ProgramProduct();
    Product product2 = make(a(ProductBuilder.defaultProduct));
    product2.getProductGroup().setCode("PG2");
    programProduct2.setProduct(product2);
    programProduct2.setActive(true);

    FacilityProgramProduct facilityProgramProduct1 = new FacilityProgramProduct(programProduct1, 2L, null);
    FacilityProgramProduct facilityProgramProduct2 = new FacilityProgramProduct(programProduct2, 2L, null);
    programSupported.setProgramProducts(asList(facilityProgramProduct1, facilityProgramProduct2));
    facility.setSupportedPrograms(asList(programSupported));

    Distribution distribution = new Distribution();
    distribution.setId(1L);

    FacilityDistributionData distributionData = facilityDistributionDataService.createDistributionData(facility, distribution);

    EpiUse epiUse = distributionData.getEpiUse();
    assertThat(epiUse.getDistributionId(), is(distribution.getId()));
    assertThat(epiUse.getFacilityId(), is(facility.getId()));
    assertThat(epiUse.getLineItems().size(), is(2));
    verify(epiUseService).saveLineItems(epiUse);
  }

  @Test
  public void shouldNotGetProductGroupForAllInactiveProducts() throws Exception {
    Facility facility = new Facility(2L);
    ProgramSupported programSupported = new ProgramSupported(1L, true, new Date());

    ProgramProduct programProduct1 = new ProgramProduct();
    Product product1 = make(a(ProductBuilder.defaultProduct));
    programProduct1.setProduct(product1);
    programProduct1.setActive(true);

    ProgramProduct programProduct2 = new ProgramProduct();
    Product product2 = make(a(ProductBuilder.defaultProduct));
    product2.getProductGroup().setCode("PG2");
    product2.setActive(false);
    programProduct2.setProduct(product2);

    FacilityProgramProduct facilityProgramProduct1 = new FacilityProgramProduct(programProduct1, 2L, null);
    FacilityProgramProduct facilityProgramProduct2 = new FacilityProgramProduct(programProduct2, 2L, null);
    programSupported.setProgramProducts(asList(facilityProgramProduct1, facilityProgramProduct2));
    facility.setSupportedPrograms(asList(programSupported));

    Distribution distribution = new Distribution();
    distribution.setId(1L);

    FacilityDistributionData distributionData = facilityDistributionDataService.createDistributionData(facility, distribution);

    List<EpiUseLineItem> lineItems = distributionData.getEpiUse().getLineItems();
    assertThat(lineItems.size(), is(1));
    assertThat(lineItems.get(0).getProductGroup().getId(), is(1L));
    assertThat(lineItems.get(0).getProductGroup().getName(), is("Product Group 1"));
  }

  @Test
  public void shouldNotGetProductGroupForAllInactiveProgramProducts() throws Exception {
    Facility facility = new Facility(2L);
    ProgramSupported programSupported = new ProgramSupported(1L, true, new Date());

    ProgramProduct programProduct1 = new ProgramProduct();
    Product product1 = make(a(ProductBuilder.defaultProduct));
    programProduct1.setProduct(product1);
    programProduct1.setActive(true);

    ProgramProduct programProduct2 = new ProgramProduct();
    Product product2 = make(a(ProductBuilder.defaultProduct));
    product2.getProductGroup().setCode("PG2");
    programProduct2.setProduct(product2);
    programProduct2.setActive(false);

    FacilityProgramProduct facilityProgramProduct1 = new FacilityProgramProduct(programProduct1, 2L, null);
    FacilityProgramProduct facilityProgramProduct2 = new FacilityProgramProduct(programProduct2, 2L, null);
    programSupported.setProgramProducts(asList(facilityProgramProduct1, facilityProgramProduct2));
    facility.setSupportedPrograms(asList(programSupported));

    Distribution distribution = new Distribution();
    distribution.setId(1L);

    FacilityDistributionData distributionData = facilityDistributionDataService.createDistributionData(facility, distribution);

    List<EpiUseLineItem> lineItems = distributionData.getEpiUse().getLineItems();
    assertThat(lineItems.size(), is(1));
    assertThat(lineItems.get(0).getProductGroup().getId(), is(1L));
    assertThat(lineItems.get(0).getProductGroup().getName(), is("Product Group 1"));
  }
}
