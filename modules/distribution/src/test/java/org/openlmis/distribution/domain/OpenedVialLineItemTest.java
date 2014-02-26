/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.distribution.domain;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Facility;
import org.openlmis.db.categories.UnitTests;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class OpenedVialLineItemTest {

  private Long facilityVisitId;
  private FacilityVisit facilityVisit;
  private Facility facility;
  private String productVialName;

  @Before
  public void setUp() throws Exception {
    facilityVisitId = 1L;
    facilityVisit = new FacilityVisit();
    facilityVisit.setId(facilityVisitId);
    facilityVisit.setCreatedBy(33L);
    facility = mock(Facility.class);
    productVialName = "bcg";
  }

  @Test
  public void shouldCreateOpenedVialLineItemWithPackSizeWhenValidProductVialExists() {
    String productCode = "BCG";
    Integer packSize = 10;
    ProductVial productVial = new ProductVial();
    productVial.setProductCode(productCode);
    when(facility.getPackSizeFor(productCode)).thenReturn(packSize);

    OpenedVialLineItem lineItem = new OpenedVialLineItem(facilityVisit, facility, productVial, productVialName);

    assertThat(lineItem.getFacilityVisitId(), is(facilityVisitId));
    assertThat(lineItem.getProductVialName(), is(productVialName));
    assertThat(lineItem.getPackSize(), is(packSize));
    assertThat(lineItem.getCreatedBy(), is(33L));
  }

  @Test
  public void shouldCreateOpenedVialLineItemWithPackSizeNullWhenNoProductVialExists() {

    OpenedVialLineItem lineItem = new OpenedVialLineItem(facilityVisit, facility, null, productVialName);

    assertThat(lineItem.getFacilityVisitId(), is(facilityVisitId));
    assertThat(lineItem.getProductVialName(), is(productVialName));
    assertThat(lineItem.getPackSize(), is(nullValue()));
  }
}
