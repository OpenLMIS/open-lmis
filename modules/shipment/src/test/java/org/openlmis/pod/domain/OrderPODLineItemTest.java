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
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.exception.DataException;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.rnr.builder.RnrLineItemBuilder;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.shipment.builder.ShipmentLineItemBuilder;
import org.openlmis.shipment.domain.ShipmentLineItem;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class OrderPODLineItemTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void shouldThrowExceptionIfProductCodeIsEmpty() {
    OrderPODLineItem orderPodLineItem = new OrderPODLineItem(1L, null, 100);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.mandatory.fields.missing");

    orderPodLineItem.validate();
  }

  @Test
  public void shouldThrowExceptionIfQuantityReceivedIsEmpty() {
    OrderPODLineItem orderPodLineItem = new OrderPODLineItem(1L, "P100", null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.mandatory.fields.missing");

    orderPodLineItem.validate();
  }

  @Test
  public void shouldThrowExceptionIfQuantityReturnedIsNegative() {
    OrderPODLineItem orderPodLineItem = new OrderPODLineItem(1L, "P100", 10);
    orderPodLineItem.setQuantityReturned(-100);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.invalid.returned.quantity");

    orderPodLineItem.validate();
  }

  @Test
  public void shouldThrowExceptionIfQuantityReceivedIsNegative() {
    OrderPODLineItem orderPodLineItem = new OrderPODLineItem(1L, "P100", -100);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.invalid.received.quantity");

    orderPodLineItem.validate();
  }

  @Test
  public void shouldCreateOrderPODLineItemFromRnrLineItem() throws Exception {
    RnrLineItem rnrLineItem = make(a(RnrLineItemBuilder.defaultRnrLineItem));
    Long createdBy = 1L;

    OrderPODLineItem orderPODLineItem = new OrderPODLineItem(rnrLineItem, createdBy);

    assertThat(orderPODLineItem.getProductCode(), is(rnrLineItem.getProductCode()));
    assertThat(orderPODLineItem.getProductCategory(), is(rnrLineItem.getProductCategory()));
    assertThat(orderPODLineItem.getProductCategoryDisplayOrder(), is(rnrLineItem.getProductCategoryDisplayOrder()));
    assertThat(orderPODLineItem.getProductDisplayOrder(), is(rnrLineItem.getProductDisplayOrder()));
    assertThat(orderPODLineItem.getProductName(), is(rnrLineItem.getProduct()));
    assertThat(orderPODLineItem.getDispensingUnit(), is(rnrLineItem.getDispensingUnit()));
    assertThat(orderPODLineItem.getPacksToShip(), is(rnrLineItem.getPacksToShip()));
    assertThat(orderPODLineItem.getFullSupply(), is(rnrLineItem.getFullSupply()));
    assertThat(orderPODLineItem.getCreatedBy(), is(createdBy));
    assertThat(orderPODLineItem.getModifiedBy(), is(createdBy));
  }

  @Test
  public void shouldCreateOrderPODLineItemFromShipmentLineItem() throws Exception {
    ShipmentLineItem shipmentLineItem = make(a(ShipmentLineItemBuilder.defaultShipmentLineItem));
    Long createdBy = 1L;

    OrderPODLineItem orderPODLineItem = new OrderPODLineItem(shipmentLineItem, createdBy);

    assertThat(orderPODLineItem.getProductCode(), is(shipmentLineItem.getProductCode()));
    assertThat(orderPODLineItem.getProductCategory(), is(shipmentLineItem.getProductCategory()));
    assertThat(orderPODLineItem.getProductCategoryDisplayOrder(),
      is(shipmentLineItem.getProductCategoryDisplayOrder()));
    assertThat(orderPODLineItem.getProductDisplayOrder(), is(shipmentLineItem.getProductDisplayOrder()));
    assertThat(orderPODLineItem.getProductName(), is(shipmentLineItem.getProductName()));
    assertThat(orderPODLineItem.getDispensingUnit(), is(shipmentLineItem.getDispensingUnit()));
    assertThat(orderPODLineItem.getPacksToShip(), is(shipmentLineItem.getPacksToShip()));
    assertThat(orderPODLineItem.getFullSupply(), is(shipmentLineItem.getFullSupply()));
    assertThat(orderPODLineItem.getQuantityShipped(), is(shipmentLineItem.getQuantityShipped()));
    assertThat(orderPODLineItem.getFullSupply(), is(shipmentLineItem.getFullSupply()));
    assertThat(orderPODLineItem.getCreatedBy(), is(createdBy));
    assertThat(orderPODLineItem.getModifiedBy(), is(createdBy));
  }

  @Test
  public void shouldCopySomeFieldsFromLineItemIntoAnother() {
    Long modifiedBy = 1L;
    String notes = "Notes";
    OrderPODLineItem lineItem = new OrderPODLineItem(1L, "P1", null);
    OrderPODLineItem otherLineItem = new OrderPODLineItem(1L, "P1", 30);
    otherLineItem.setNotes(notes);
    otherLineItem.setModifiedBy(modifiedBy);

    lineItem.copy(otherLineItem);

    assertThat(lineItem.getQuantityReceived(), is(30));
    assertThat(lineItem.getNotes(), is(notes));
    assertThat(lineItem.getModifiedBy(), is(modifiedBy));
  }
}
