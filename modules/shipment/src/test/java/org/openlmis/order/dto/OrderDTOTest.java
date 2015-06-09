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
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.SupplyLine;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.order.domain.Order;
import org.openlmis.rnr.builder.RequisitionBuilder;
import org.openlmis.rnr.dto.RnrDTO;
import org.openlmis.shipment.domain.ShipmentFileInfo;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.rnr.builder.RequisitionBuilder.defaultRequisition;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest(RnrDTO.class)
public class OrderDTOTest {

  @Test
  public void shouldGetOrdersForView() throws Exception {
    mockStatic(RnrDTO.class);

    SupplyLine supplyLine = new SupplyLine();
    final Order order1 = new Order();
    Date createdDate = new Date();
    order1.setCreatedDate(createdDate);
    order1.setRnr(make(a(defaultRequisition)));
    order1.setShipmentFileInfo(new ShipmentFileInfo("1.csv", false));
    order1.setSupplyLine(supplyLine);
    order1.setOrderNumber("OrdHIV00000001R");
    final Order order2 = new Order();
    order2.setRnr(make(a(defaultRequisition, with(RequisitionBuilder.periodId, 2L), with(RequisitionBuilder.program, new Program(11L)))));
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
    assertThat(orderDTOs.get(0).getStringCreatedDate(), is(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(createdDate)));
    assertThat(orderDTOs.get(0).getShipmentError(), is(false));
    assertThat(orderDTOs.get(0).getSupplyLine(), is(supplyLine));
    assertThat(orderDTOs.get(0).getOrderNumber(), is(order1.getOrderNumber()));
  }
}
