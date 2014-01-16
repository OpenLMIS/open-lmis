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
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.db.categories.UnitTests;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ChildCoverageLineItemTest {

  @Test
  public void shouldCreateChildCoverageLineItem() throws Exception {
    Facility facility = mock(Facility.class);
    VaccinationProduct vaccinationProduct = new VaccinationProduct("BCG", "BCG", true);

    when(facility.getWhoRatioFor("BCG")).thenReturn(2.4);
    when(facility.getCatchmentPopulation()).thenReturn((long) 8);

    ChildCoverageLineItem childCoverageLineItem = new ChildCoverageLineItem(facility, vaccinationProduct);

    assertThat(childCoverageLineItem.getVaccination(), is("BCG"));
    assertThat(childCoverageLineItem.getTargetGroup(), is(19));
  }

  @Test
  public void shouldRoundTargetGroupCorrectly() throws Exception {
    Facility facility = mock(Facility.class);
    VaccinationProduct vaccinationProduct = new VaccinationProduct("BCG", "BCG", true);

    when(facility.getWhoRatioFor("BCG")).thenReturn(2.6);
    when(facility.getCatchmentPopulation()).thenReturn((long) 8);

    ChildCoverageLineItem childCoverageLineItem = new ChildCoverageLineItem(facility, vaccinationProduct);

    assertThat(childCoverageLineItem.getVaccination(), is("BCG"));
    assertThat(childCoverageLineItem.getTargetGroup(), is(21));
  }

  @Test
  public void shouldSetTargetGroupNullIfWhoRatioIsNull() throws Exception {
    Facility facility = mock(Facility.class);
    VaccinationProduct vaccinationProduct = new VaccinationProduct("BCG", "BCG", true);

    when(facility.getWhoRatioFor("BCG")).thenReturn(null);
    when(facility.getCatchmentPopulation()).thenReturn((long) 8);

    ChildCoverageLineItem childCoverageLineItem = new ChildCoverageLineItem(facility, vaccinationProduct);

    assertThat(childCoverageLineItem.getVaccination(), is("BCG"));
    assertThat(childCoverageLineItem.getTargetGroup(), is(nullValue()));
  }

  @Test
  public void shouldSetTargetGroupNullIfCatchmentPopulationIsNull() throws Exception {
    Facility facility = mock(Facility.class);
    VaccinationProduct vaccinationProduct = new VaccinationProduct("BCG", "BCG", true);

    when(facility.getWhoRatioFor("BCG")).thenReturn(Double.valueOf(2));
    when(facility.getCatchmentPopulation()).thenReturn(null);

    ChildCoverageLineItem childCoverageLineItem = new ChildCoverageLineItem(facility, vaccinationProduct);

    assertThat(childCoverageLineItem.getVaccination(), is("BCG"));
    assertThat(childCoverageLineItem.getTargetGroup(), is(nullValue()));
  }
}
