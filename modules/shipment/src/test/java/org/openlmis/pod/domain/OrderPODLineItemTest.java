/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
package org.openlmis.pod.domain;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openlmis.core.exception.DataException;
import org.openlmis.rnr.builder.RnrLineItemBuilder;
import org.openlmis.rnr.domain.RnrLineItem;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class OrderPODLineItemTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void shouldThrowExceptionIfProductCodeIsEmpty() {
    OrderPODLineItem orderPodLineItem = new OrderPODLineItem(1l, null, 100);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.mandatory.fields.missing");

    orderPodLineItem.validate();
  }

  @Test
  public void shouldThrowExceptionIfQuantityReceivedIsEmpty() {
    OrderPODLineItem orderPodLineItem = new OrderPODLineItem(1l, "P100", null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.mandatory.fields.missing");

    orderPodLineItem.validate();
  }

  @Test
  public void shouldThrowExceptionIfQuantityReceivedIsNegative() {
    OrderPODLineItem orderPodLineItem = new OrderPODLineItem(1l, "P100", -100);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.invalid.received.quantity");

    orderPodLineItem.validate();
  }

  @Test
  public void shouldCreateOrderPODLineItemFromRnrLineItem() throws Exception {
    RnrLineItem rnrLineItem = make(a(RnrLineItemBuilder.defaultRnrLineItem));

    OrderPODLineItem orderPODLineItem = OrderPODLineItem.createFrom(rnrLineItem);

    assertThat(orderPODLineItem.getProductCode(), is(rnrLineItem.getProductCode()));
    assertThat(orderPODLineItem.getProductName(), is(rnrLineItem.getProduct()));
    assertThat(orderPODLineItem.getPacksToShip(), is(rnrLineItem.getPacksToShip()));
    assertThat(orderPODLineItem.getDispensingUnit(), is(rnrLineItem.getDispensingUnit()));
    assertThat(orderPODLineItem.getFullSupply(), is(rnrLineItem.getFullSupply()));
  }
}
