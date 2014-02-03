/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.distribution.domain;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.Facility;
import org.openlmis.db.categories.UnitTests;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest(VaccinationChildCoverage.class)
@Category(UnitTests.class)
public class VaccinationChildCoverageTest {

  @Test
  public void shouldCreate12VaccinationChildCoverageLineItems() throws Exception {
    Facility facility = mock(Facility.class);
    FacilityVisit facilityVisit = mock(FacilityVisit.class);

    VaccinationProduct vaccinationProduct = new VaccinationProduct();
    vaccinationProduct.setVaccination("bcg");
    List<VaccinationProduct> vaccinationProducts = asList(vaccinationProduct);
    ChildCoverageLineItem lineItem = new ChildCoverageLineItem();

    whenNew(ChildCoverageLineItem.class).withArguments(facilityVisit, facility, vaccinationProduct, "BCG").thenReturn(lineItem);
    VaccinationChildCoverage vaccinationChildCoverage = new VaccinationChildCoverage(facilityVisit, facility, vaccinationProducts);

    assertThat(vaccinationChildCoverage.getChildCoverageLineItems().size(), is(12));
  }

  @Test
  public void shouldCreate12ChildCoverageLineItemsWithVaccinationAsNullForInvalidVaccinationProduct() throws Exception {
    Facility facility = mock(Facility.class);
    FacilityVisit facilityVisit = mock(FacilityVisit.class);

    VaccinationProduct invalidVaccination = new VaccinationProduct();
    invalidVaccination.setVaccination("BCG1234");
    List<VaccinationProduct> vaccinationProducts = asList(invalidVaccination);

    ChildCoverageLineItem lineItem = new ChildCoverageLineItem();
    lineItem.setVaccination("BCG");

    whenNew(ChildCoverageLineItem.class).withArguments(facilityVisit, facility, null, "BCG").thenReturn(lineItem);

    VaccinationChildCoverage vaccinationChildCoverage = new VaccinationChildCoverage(facilityVisit, facility, vaccinationProducts);

    assertThat(vaccinationChildCoverage.getChildCoverageLineItems().size(), is(12));
    assertTrue(vaccinationChildCoverage.getChildCoverageLineItems().get(0).getVaccination().equals("BCG"));
    verifyNew(ChildCoverageLineItem.class).withArguments(facilityVisit, facility, null, "BCG");
  }

  @Test
  public void shouldCreate12ChildCoverageLineItemsAlthoughMoreThan12VaccinationProductsExists() throws Exception {
    Facility facility = mock(Facility.class);
    FacilityVisit facilityVisit = mock(FacilityVisit.class);

    List<VaccinationProduct> vaccinationProducts = new ArrayList<>();
    VaccinationProduct invalidVaccination;
    for (int i = 0; i < 13; i++) {
      invalidVaccination = new VaccinationProduct();
      invalidVaccination.setVaccination("invalid" + i);
      vaccinationProducts.add(invalidVaccination);
    }

    ChildCoverageLineItem lineItem = new ChildCoverageLineItem();
    whenNew(ChildCoverageLineItem.class).withAnyArguments().thenReturn(lineItem);

    VaccinationChildCoverage vaccinationChildCoverage = new VaccinationChildCoverage(facilityVisit, facility, vaccinationProducts);

    assertThat(vaccinationChildCoverage.getChildCoverageLineItems().size(), is(12));
  }
}
