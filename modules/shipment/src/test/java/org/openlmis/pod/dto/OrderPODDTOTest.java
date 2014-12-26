/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.pod.dto;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.builder.SupplyLineBuilder;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.order.domain.Order;
import org.openlmis.order.domain.OrderStatus;
import org.openlmis.rnr.builder.RequisitionBuilder;
import org.openlmis.rnr.domain.Rnr;

import java.text.SimpleDateFormat;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.openlmis.core.builder.FacilityBuilder.name;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.core.builder.SupplyLineBuilder.defaultSupplyLine;
import static org.openlmis.rnr.builder.RequisitionBuilder.id;
import static org.openlmis.rnr.builder.RequisitionBuilder.program;

@Category(UnitTests.class)
public class OrderPODDTOTest {
  @Test
  public void shouldGetOrderDetailsForPOD() throws Exception {
    Rnr rnr = make(a(RequisitionBuilder.defaultRequisition, with(id, 2L), with(program, make(a(defaultProgram, with(ProgramBuilder.programName, "HIV"))))));
    Order order = new Order(rnr);
    order.setOrderNumber("OYELL_FVR00000001R");
    order.setSupplyLine(make(a(defaultSupplyLine, with(SupplyLineBuilder.facility, make(a(FacilityBuilder.defaultFacility, with(name, "F10")))))));
    order.setStatus(OrderStatus.IN_ROUTE);
    OrderPODDTO orderPODDTO = OrderPODDTO.getOrderDetailsForPOD(order);

    assertThat(orderPODDTO.getFacilityCode(), is(rnr.getFacility().getCode()));
    assertThat(orderPODDTO.getPeriodStartDate(), is(rnr.getPeriod().getStringStartDate()));
    String createdDate = order.getCreatedDate() == null ? null : new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(order.getCreatedDate());
    assertThat(orderPODDTO.getStringCreatedDate(), is(createdDate));
    assertThat(orderPODDTO.getId(), is(order.getId()));
    assertThat(orderPODDTO.getEmergency(), is(rnr.isEmergency()));
    assertThat(orderPODDTO.getAlreadyReceived(), is(false));
    assertThat(orderPODDTO.getOrderNumber(), is("OYELL_FVR00000001R"));
  }

  @Test
  public void shouldSetOrderStatusFlagTrueIfOrderIsReceived() throws Exception {
    Rnr rnr = make(a(RequisitionBuilder.defaultRequisition, with(id, 2L), with(program, make(a(defaultProgram, with(ProgramBuilder.programName, "HIV"))))));
    Order order = new Order(rnr);
    order.setSupplyLine(make(a(defaultSupplyLine, with(SupplyLineBuilder.facility, make(a(FacilityBuilder.defaultFacility, with(name, "F10")))))));

    order.setStatus(OrderStatus.IN_ROUTE);
    assertFalse(OrderPODDTO.getOrderDetailsForPOD(order).getAlreadyReceived());

    order.setStatus(OrderStatus.RECEIVED);
    assertTrue(OrderPODDTO.getOrderDetailsForPOD(order).getAlreadyReceived());
  }
}
