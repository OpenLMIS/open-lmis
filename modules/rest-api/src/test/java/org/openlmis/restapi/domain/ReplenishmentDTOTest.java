/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 ofthe License, or (at your option) any laterversion.
 *   
 * This program is distributed in the hope that it will be useful, but WITHOUT ANYWARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR APARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this programIf not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.restapi.domain;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openlmis.core.builder.SupplyLineBuilder;
import org.openlmis.core.domain.SupplyLine;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.order.domain.Order;
import org.openlmis.order.domain.OrderStatus;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.rnr.dto.RnrLineItemDTO;

import java.text.SimpleDateFormat;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.rnr.builder.RequisitionBuilder.defaultRequisition;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@Category(UnitTests.class)
public class ReplenishmentDTOTest {
  @Test
  public void shouldPopulateRnrDTOFromRequisition() throws Exception {
    Rnr rnr = make(a(defaultRequisition));
    rnr.add(new RnrLineItem(), false);
    RnrLineItemDTO expectedLineItemDTO = new RnrLineItemDTO(new RnrLineItem());
    whenNew(RnrLineItemDTO.class).withAnyArguments().thenReturn(expectedLineItemDTO);

    Order order = new Order(rnr.getId());
    order.setStatus(OrderStatus.RELEASED);
    order.setOrderNumber("ONI101");
    SupplyLine supplyLine = make(a(SupplyLineBuilder.defaultSupplyLine));
    order.setSupplyLine(supplyLine);

    ReplenishmentDTO replenishmentDTO = ReplenishmentDTO.prepareForREST(rnr, order);

    assertThat(rnr.getId(), is(replenishmentDTO.getId()));
    assertThat(rnr.getFacility().getCode(), is(replenishmentDTO.getAgentCode()));
    assertThat(rnr.getProgram().getCode(), is(replenishmentDTO.getProgramCode()));
    assertThat(rnr.isEmergency(), is(replenishmentDTO.isEmergency()));

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    assertThat(simpleDateFormat.format(rnr.getPeriod().getStartDate()), is(replenishmentDTO.getStringPeriodStartDate()));
    assertThat(simpleDateFormat.format(rnr.getPeriod().getEndDate()), is(replenishmentDTO.getStringPeriodEndDate()));
    assertThat(rnr.getStatus().name(), is(replenishmentDTO.getRequisitionStatus()));
    assertThat(replenishmentDTO.getProducts().size(), is(2));
    assertThat(replenishmentDTO.getOrderId(), is(order.getOrderNumber()));
    assertThat(replenishmentDTO.getOrderStatus(), is(order.getStatus().name()));
    assertThat(replenishmentDTO.getSupplyingFacilityCode(), is(supplyLine.getSupplyingFacility().getCode()));
  }


}
