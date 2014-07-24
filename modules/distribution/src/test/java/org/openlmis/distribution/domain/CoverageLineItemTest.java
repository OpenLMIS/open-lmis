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
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.db.categories.UnitTests;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class CoverageLineItemTest {

  private ProcessingPeriod period;

  @Before
  public void setUp() {
    period = mock(ProcessingPeriod.class);
  }

  @Test
  public void shouldCreateCoverageLineItemWhenTargetGroupProductIsNotNull() {
    FacilityVisit facilityVisit = new FacilityVisit();
    facilityVisit.setId(1L);
    Facility facility = mock(Facility.class);

    when(period.getNumberOfMonths()).thenReturn(1);
    when(facility.getCatchmentPopulation()).thenReturn(100L);
    String productCode = "P10";
    TargetGroupProduct targetGroupProduct = new TargetGroupProduct("BCG", productCode, true);
    when(facility.getWhoRatioFor(productCode)).thenReturn(67D);

    CoverageLineItem coverageLineItem = new CoverageLineItem(facilityVisit, facility, targetGroupProduct, period.getNumberOfMonths());

    assertThat(coverageLineItem.getFacilityVisitId(), is(facilityVisit.getId()));
    assertThat(coverageLineItem.getTargetGroup(), is(6));
  }

  @Test
  public void shouldCreateCoverageLineItemWhenTargetGroupProductIsNull() {
    FacilityVisit facilityVisit = new FacilityVisit();
    facilityVisit.setId(1L);
    Facility facility = mock(Facility.class);

    when(period.getNumberOfMonths()).thenReturn(1);
    CoverageLineItem coverageLineItem = new CoverageLineItem(facilityVisit, facility, null, period.getNumberOfMonths());

    assertThat(coverageLineItem.getFacilityVisitId(), is(facilityVisit.getId()));
    assertThat(coverageLineItem.getTargetGroup(), is(nullValue()));
  }
}
