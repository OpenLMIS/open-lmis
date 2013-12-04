/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.rnr.dto;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.rnr.domain.RnrLineItem;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.rnr.builder.RnrLineItemBuilder.defaultRnrLineItem;

@Category(UnitTests.class)
public class RnrLineItemDTOTest {

  @Test
  public void shouldPopulateItselfFromLineItem() throws Exception {
    RnrLineItem lineItem = make(a(defaultRnrLineItem));

    RnrLineItemDTO lineItemDTO = new RnrLineItemDTO(lineItem);

    assertThat(lineItemDTO.getReasonForRequestedQuantity(), is(lineItem.getReasonForRequestedQuantity()));
    assertThat(lineItemDTO.getProductCode(), is(lineItem.getProductCode()));
    assertThat(lineItemDTO.getBeginningBalance(), is(lineItem.getBeginningBalance()));
    assertThat(lineItemDTO.getQuantityReceived(), is(lineItem.getQuantityReceived()));
    assertThat(lineItemDTO.getQuantityApproved(), is(lineItem.getQuantityApproved()));
    assertThat(lineItemDTO.getQuantityDispensed(), is(lineItem.getQuantityDispensed()));
    assertThat(lineItemDTO.getTotalLossesAndAdjustments(), is(lineItem.getTotalLossesAndAdjustments()));
    assertThat(lineItemDTO.getStockInHand(), is(lineItem.getStockInHand()));
    assertThat(lineItemDTO.getNewPatientCount(), is(lineItem.getNewPatientCount()));
    assertThat(lineItemDTO.getStockOutDays(), is(lineItem.getStockOutDays()));
    assertThat(lineItemDTO.getCalculatedOrderQuantity(), is(lineItem.getCalculatedOrderQuantity()));
    assertThat(lineItemDTO.getSkipped(), is(lineItem.getSkipped()));
    assertThat(lineItemDTO.getRemarks(), is(lineItem.getRemarks()));
  }
}
