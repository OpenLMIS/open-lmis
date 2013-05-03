/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.order.dto;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.Program;
import org.openlmis.order.domain.Order;
import org.openlmis.rnr.builder.RequisitionBuilder;
import org.openlmis.rnr.dto.RnrDTO;
import org.openlmis.shipment.domain.ShipmentFileInfo;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.MakeItEasy.with;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.rnr.builder.RequisitionBuilder.defaultRnr;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;


@RunWith(PowerMockRunner.class)
@PrepareForTest(RnrDTO.class)
public class OrderDTOTest {

  @Test
  public void shouldGetOrdersForView() throws Exception {
    mockStatic(RnrDTO.class);

    final Order order1 = new Order();
    Date createdDate = new Date();
    order1.setCreatedDate(createdDate);
    order1.setRnr(make(a(defaultRnr)));
    order1.setShipmentFileInfo(new ShipmentFileInfo("1.csv", false));
    final Order order2 = new Order();
    order2.setRnr(make(a(defaultRnr, with(RequisitionBuilder.periodId, 2L), with(RequisitionBuilder.program, new Program(11L, "name")))));
    order2.setShipmentFileInfo(new ShipmentFileInfo("2.csv", true));
    final RnrDTO dtoForOrder1 = new RnrDTO();
    dtoForOrder1.setId(1L);
    final RnrDTO dtoForOrder2 = new RnrDTO();
    dtoForOrder2.setId(2L);

    when(RnrDTO.prepareForOrderView(order1.getRnr())).thenReturn(dtoForOrder1);
    when(RnrDTO.prepareForOrderView(order2.getRnr())).thenReturn(dtoForOrder2);

    List<Order> orders = new ArrayList<Order>() {{
      add(order1);
      add(order2);
    }};

    List<OrderDTO> orderDTOs = OrderDTO.getOrdersForView(orders);

    assertThat(orderDTOs.get(0).getRnr(), is(dtoForOrder1));
    assertThat(orderDTOs.get(1).getRnr(), is(dtoForOrder2));
    assertThat(orderDTOs.get(0).getCreatedDate(), is(createdDate));
    assertThat(orderDTOs.get(0).getShipmentError(), is(false));
  }
}
