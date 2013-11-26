/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.order.domain;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.builder.SupplyLineBuilder;
import org.openlmis.core.domain.SupplyLine;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.rnr.domain.Rnr;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.openlmis.rnr.builder.RequisitionBuilder.defaultRequisition;

@RunWith(MockitoJUnitRunner.class)
@Category(UnitTests.class)
public class OrderTest {

  @Test
  public void shouldCreateOrderFromRequisition() throws Exception {
    Rnr rnr = make(a(defaultRequisition));

    Order order = new Order(rnr);

    assertThat(order.getRnr(), is(rnr));
  }

  @Test
  public void shouldReturnSupplyingFacility() {
    Order order = new Order();
    SupplyLine supplyLine = make(a(SupplyLineBuilder.defaultSupplyLine));
    order.setSupplyLine(supplyLine);

    assertThat(order.getSupplyingFacility(), is(supplyLine.getSupplyingFacility()));
  }

  @Test
  public void shouldReturnNullAsSupplyingFacilityIfSupplyLineNotPresent() throws Exception {
    Order order = new Order();
    assertThat(order.getSupplyingFacility(), is(nullValue()));
  }
}
