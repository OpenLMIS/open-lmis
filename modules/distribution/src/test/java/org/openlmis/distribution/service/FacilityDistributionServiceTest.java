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
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.RefrigeratorService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.distribution.domain.*;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.List;
import java.util.Map;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.*;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest(FacilityDistributionService.class)
public class FacilityDistributionServiceTest {

  @Mock
  private FacilityService facilityService;

  @Mock
  private EpiUseService epiUseService;

  @Mock
  private EpiInventoryService epiInventoryService;

  @Mock
  private FacilityVisitService facilityVisitService;

  @Mock
  private DistributionRefrigeratorsService distributionRefrigeratorsService;

  @Mock
  private RefrigeratorService refrigeratorService;

  @Mock
  private VaccinationCoverageService vaccinationCoverageService;

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
    FacilityDistribution facilityDistribution = new FacilityDistribution(null, new EpiUse(), null, null, null, null);

    Refrigerator refrigerator = new Refrigerator("LG", "S. No.", "Model", 2L, true);
    List<Refrigerator> refrigerators = asList(refrigerator);

    when(facilityService.getAllForDeliveryZoneAndProgram(1L, 3L)).thenReturn(facilities);
    when(refrigeratorService.getRefrigeratorsForADeliveryZoneAndProgram(1L, 3L)).thenReturn(refrigerators);
    when(vaccinationCoverageService.getVaccinationProducts(true)).thenReturn(null);
    when(vaccinationCoverageService.getProductVials()).thenReturn(null);

    doReturn(facilityDistribution).when(spyFacilityDistributionService).createDistributionData(facility, distribution, refrigerators, null, null);

    Map<Long, FacilityDistribution> facilityDistributionDataMap = spyFacilityDistributionService.createFor(distribution);

    assertThat(facilityDistributionDataMap.get(1234L), is(facilityDistribution));
  }

  @Test
  public void shouldGetFacilityDistributionDataForAFacilityAndDistribution() throws Exception {
    Facility facility = new Facility(2L);

    Refrigerator refrigerator = new Refrigerator("LG", "S. No.", "Model", 2L, true);
    List<Refrigerator> refrigerators = asList(refrigerator);
    RefrigeratorReading refrigeratorReading = new RefrigeratorReading(refrigerator);

    VaccinationProduct vaccinationProduct = new VaccinationProduct("BCG", "BCG", true);
    List<VaccinationProduct> vaccinationProducts = asList(vaccinationProduct);

    List<ProductVial> productVials = asList(new ProductVial("BCG", "BCG"));

    Distribution distribution = new Distribution();
    distribution.setId(1L);
    distribution.setPeriod(new ProcessingPeriod());

    FacilityVisit facilityVisit = new FacilityVisit();
    whenNew(FacilityVisit.class).withArguments(facility, distribution).thenReturn(facilityVisit);
    whenNew(FacilityDistribution.class).withArguments(facilityVisit, facility, distribution, asList(refrigeratorReading), vaccinationProducts, productVials).thenReturn(mock(FacilityDistribution.class));

    FacilityDistribution distributionData = facilityDistributionService.createDistributionData(facility, distribution, refrigerators, vaccinationProducts, productVials);

    verify(epiUseService).save(distributionData.getEpiUse());
    verify(facilityVisitService).save(facilityVisit);
    verify(epiInventoryService).save(distributionData.getEpiInventory());
    verify(vaccinationCoverageService).saveChildCoverage(distributionData.getChildCoverage());
    verifyNew(FacilityVisit.class).withArguments(facility, distribution);
    verifyNew(FacilityDistribution.class).withArguments(facilityVisit, facility, distribution, asList(refrigeratorReading), vaccinationProducts, productVials);
  }

  @Test
  public void shouldSaveInventoryDataForAFDD() throws Exception {
    Facility facility = new Facility();
    Distribution distribution = new Distribution();
    EpiInventory epiInventory = new EpiInventory();
    List<Refrigerator> refrigerators = emptyList();
    List<VaccinationProduct> vaccinationProducts = emptyList();
    List<ProductVial> productVials = emptyList();
    FacilityDistribution distributionData = mock(FacilityDistribution.class);

    FacilityVisit facilityVisit = new FacilityVisit();
    whenNew(FacilityDistribution.class).withArguments(facilityVisit, facility, distribution, refrigerators, vaccinationProducts, productVials).thenReturn(distributionData);
    when(distributionData.getEpiInventory()).thenReturn(epiInventory);

    facilityDistributionService.createDistributionData(facility, distribution, refrigerators, vaccinationProducts, productVials);

    verify(epiInventoryService).save(epiInventory);
  }

  @Test
  public void shouldFetchAllRefrigeratorsAndPopulateInFacilities() throws Exception {
    Distribution distribution = new Distribution();
    distribution.setDeliveryZone(new DeliveryZone(4L));
    distribution.setProgram(new Program(16L));
    List<Refrigerator> refrigerators = asList(new Refrigerator(), new Refrigerator());
    Facility facility1 = new Facility(9l);
    Facility facility2 = new Facility(12L);
    FacilityDistribution facilityDistribution1 = new FacilityDistribution(new FacilityVisit(), new EpiUse(), null, null, new VaccinationFullCoverage(), new VaccinationChildCoverage());
    FacilityDistribution facilityDistribution2 = new FacilityDistribution(new FacilityVisit(), new EpiUse(), null, null, new VaccinationFullCoverage(), new VaccinationChildCoverage());
    FacilityDistributionService service = spy(facilityDistributionService);

    when(refrigeratorService.getRefrigeratorsForADeliveryZoneAndProgram(4L, 16L)).thenReturn(refrigerators);
    when(facilityService.getAllForDeliveryZoneAndProgram(4L, 16L)).thenReturn(asList(facility1, facility2));
    when(vaccinationCoverageService.getVaccinationProducts(true)).thenReturn(null);
    when(vaccinationCoverageService.getProductVials()).thenReturn(null);

    doReturn(facilityDistribution1).when(service).createDistributionData(facility1, distribution, refrigerators, null, null);
    doReturn(facilityDistribution2).when(service).createDistributionData(facility2, distribution, refrigerators, null, null);

    Map<Long, FacilityDistribution> facilityDistributions = service.createFor(distribution);

    assertThat(facilityDistributions.get(9L), is(facilityDistribution1));
    assertThat(facilityDistributions.get(12L), is(facilityDistribution2));
  }

  @Test
  public void shouldFilterListOfRefrigeratorsForAFacilityAndPopulateFacilityDistributionsWithThem() throws Exception {
    Facility facility = new Facility(5L);
    Distribution distribution = new Distribution();
    Refrigerator nonFacilityRefrigerator = new Refrigerator();
    nonFacilityRefrigerator.setFacilityId(54L);
    Refrigerator facilityRefrigerator = new Refrigerator();
    facilityRefrigerator.setFacilityId(5L);
    RefrigeratorReading facilityRefReading = new RefrigeratorReading(facilityRefrigerator);
    List<Refrigerator> refrigerators = asList(nonFacilityRefrigerator, facilityRefrigerator);
    FacilityDistribution expectedFacilityDistribution = new FacilityDistribution();
    expectedFacilityDistribution.setEpiUse(new EpiUse());
    FacilityVisit facilityVisit = new FacilityVisit();
    whenNew(FacilityVisit.class).withArguments(facility, distribution).thenReturn(facilityVisit);
    whenNew(FacilityDistribution.class).withArguments(facilityVisit, facility, distribution, asList(facilityRefReading), null, null).thenReturn(expectedFacilityDistribution);

    FacilityDistribution facilityDistribution = facilityDistributionService.createDistributionData(facility, distribution, refrigerators, null, null);

    verifyNew(FacilityDistribution.class).withArguments(facilityVisit, facility, distribution, asList(facilityRefReading), null, null);
    assertThat(facilityDistribution, is(expectedFacilityDistribution));
  }

  @Test
  public void shouldSaveFacilityDistributionForms() throws Exception {
    EpiUse epiUse = new EpiUse();
    FacilityVisit facilityVisit = new FacilityVisit();
    DistributionRefrigerators distributionRefrigerators = new DistributionRefrigerators();
    EpiInventory epiInventory = new EpiInventory();
    VaccinationFullCoverage vaccinationFullCoverage = new VaccinationFullCoverage();
    FacilityDistribution facilityDistribution = new FacilityDistribution(facilityVisit, epiUse, distributionRefrigerators, epiInventory, vaccinationFullCoverage, null);

    facilityDistributionService.save(facilityDistribution);

    verify(facilityVisitService).save(facilityVisit);
    verify(epiUseService).save(epiUse);
    verify(vaccinationCoverageService).saveFullCoverage(vaccinationFullCoverage);
    verify(epiInventoryService).save(epiInventory);
  }

  @Test
  public void shouldSyncFacilityVisit() throws Exception {
    FacilityDistribution facilityDistribution = new FacilityDistribution();
    FacilityVisit facilityVisit = new FacilityVisit();
    facilityDistribution.setFacilityVisit(facilityVisit);

    facilityDistributionService.setSynced(facilityDistribution);

    verify(facilityVisitService).setSynced(facilityVisit);
  }

  @Test
  public void shouldGetDataForADistribution() throws Exception {
    Distribution distribution = new Distribution();
    DeliveryZone zone = new DeliveryZone(5L);
    Program program = new Program(3L);
    distribution.setDeliveryZone(zone);
    distribution.setProgram(program);

    when(facilityService.getById(2L)).thenReturn(make(a(FacilityBuilder.defaultFacility)));

    FacilityVisit facilityVisit = new FacilityVisit();
    facilityVisit.setId(1L);
    facilityVisit.setFacilityId(2L);

    List<FacilityVisit> facilityVisits = asList(facilityVisit);
    when(facilityVisitService.getUnSyncedFacilities(distribution.getId())).thenReturn(facilityVisits);

    EpiUse epiUse = new EpiUse();
    when(epiUseService.getBy(facilityVisit.getId())).thenReturn(epiUse);

    EpiInventory epiInventory = new EpiInventory();
    when(epiInventoryService.getBy(facilityVisit.getId())).thenReturn(epiInventory);

    VaccinationFullCoverage vaccinationFullCoverage = new VaccinationFullCoverage();
    when(vaccinationCoverageService.getFullCoverageBy(facilityVisit.getId())).thenReturn(vaccinationFullCoverage);

    VaccinationChildCoverage vaccinationChildCoverage = new VaccinationChildCoverage();
    when(vaccinationCoverageService.getChildCoverageBy(facilityVisit.getId())).thenReturn(vaccinationChildCoverage);

    Refrigerator refrigerator = new Refrigerator();
    refrigerator.setFacilityId(2L);
    when(refrigeratorService.getRefrigeratorsForADeliveryZoneAndProgram(distribution.getDeliveryZone().getId(), distribution.getProgram().getId())).thenReturn(asList(refrigerator));

    RefrigeratorReading refrigeratorReading = new RefrigeratorReading(refrigerator);
    DistributionRefrigerators distributionRefrigerators = new DistributionRefrigerators();
    whenNew(DistributionRefrigerators.class).withArguments(asList(refrigeratorReading)).thenReturn(distributionRefrigerators);

    FacilityDistribution facilityDistribution = new FacilityDistribution();
    whenNew(FacilityDistribution.class).withArguments(facilityVisit, epiUse, distributionRefrigerators, epiInventory, vaccinationFullCoverage, vaccinationChildCoverage).thenReturn(facilityDistribution);

    Map<Long, FacilityDistribution> facilityDistributionMap = facilityDistributionService.get(distribution);

    assertThat(facilityDistributionMap.size(), is(1));
    assertThat(facilityDistributionMap.get(facilityVisit.getFacilityId()), is(facilityDistribution));
  }
}
