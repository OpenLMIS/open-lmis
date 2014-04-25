/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.order.dto;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.order.domain.Order;
import org.openlmis.rnr.domain.Rnr;

import java.text.SimpleDateFormat;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.order.domain.OrderStatus.READY_TO_PACK;
import static org.openlmis.rnr.builder.RequisitionBuilder.defaultRequisition;

@Category(UnitTests.class)
public class OrderStatusFeedDTOTest {
  @Test
  public void shouldPopulateFeedFromRequisition() throws Exception {
    Rnr rnr = make(a(defaultRequisition));
    Order order = new Order(rnr);
    order.setStatus(READY_TO_PACK);
    order.setOrderNumber("1");
    OrderStatusFeedDTO feed = new OrderStatusFeedDTO(order);

    long startDate = rnr.getPeriod().getStartDate().getTime();
    long endDate = rnr.getPeriod().getEndDate().getTime();

    assertThat(feed.getRequisitionId(), is(rnr.getId()));
    assertThat(feed.getRequisitionStatus(), is(rnr.getStatus()));
    assertThat(feed.isEmergency(), is(rnr.isEmergency()));
    assertThat(feed.getStartDate(), is(startDate));
    assertThat(feed.getEndDate(), is(endDate));
    assertThat(feed.getOrderId(), is(order.getOrderNumber()));
    assertThat(feed.getOrderStatus(), is(order.getStatus()));
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    String stringStartDate = dateFormat.format(rnr.getPeriod().getStartDate());
    String stringEndDate = dateFormat.format(rnr.getPeriod().getEndDate());

    assertThat(feed.getSerializedContents(), is("{\"requisitionId\":1,\"requisitionStatus\":\"INITIATED\",\"emergency\":false,\"startDate\":" + startDate + ",\"endDate\":" + endDate + ",\"stringStartDate\":\"" + stringStartDate + "\",\"stringEndDate\":\"" + stringEndDate + "\",\"orderId\":\"1\",\"orderStatus\":\"READY_TO_PACK\"}"));
  }
}
