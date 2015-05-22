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


import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.db.categories.UnitTests;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest(VaccinationChildCoverage.class)
@Category(UnitTests.class)
public class VaccinationAdultCoverageTest {

  private Facility facility;
  private FacilityVisit facilityVisit;
  private ProcessingPeriod period;

  @Before
  public void setUp() {
    facility = mock(Facility.class);
    facilityVisit = mock(FacilityVisit.class);
    period = mock(ProcessingPeriod.class);
  }

  @Test
  public void shouldCreateSevenVaccinationAdultCoverageLineItems() throws Exception {
    TargetGroupProduct targetGroupProduct = new TargetGroupProduct("pregnant women", null, null);
    List<TargetGroupProduct> targetGroupProducts = asList(targetGroupProduct);
    AdultCoverageLineItem lineItem = new AdultCoverageLineItem();

    when(period.getNumberOfMonths()).thenReturn(10);
    whenNew(AdultCoverageLineItem.class).withArguments(facilityVisit, facility, targetGroupProduct, "pregnant women", 10).thenReturn(
      lineItem);

    List<ProductVial> productVials = asList(new ProductVial("Tetanus", "Tetanus", false));
    VaccinationAdultCoverage vaccinationAdultCoverage = new VaccinationAdultCoverage(facilityVisit, facility, period, targetGroupProducts, productVials);

    assertThat(vaccinationAdultCoverage.getAdultCoverageLineItems().size(), is(7));
  }
}
