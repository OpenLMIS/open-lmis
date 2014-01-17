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
import org.openlmis.core.domain.Facility;
import org.openlmis.db.categories.UnitTests;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@Category(UnitTests.class)
public class VaccinationChildCoverageTest {

  @Test
  public void shouldCreateVaccinationChildCoverage() throws Exception {
    Facility facility = mock(Facility.class);
    FacilityVisit facilityVisit = new FacilityVisit();

    VaccinationProduct vaccinationProduct = new VaccinationProduct();
    List<VaccinationProduct> vaccinationProducts = asList(vaccinationProduct);
    ChildCoverageLineItem lineItem = new ChildCoverageLineItem();

    whenNew(ChildCoverageLineItem.class).withArguments(facilityVisit, facility, vaccinationProduct).thenReturn(lineItem);
    VaccinationChildCoverage vaccinationChildCoverage = new VaccinationChildCoverage(facilityVisit, facility, vaccinationProducts);

    assertThat(vaccinationChildCoverage.getChildCoverageLineItems().size(), is(1));
  }
}
