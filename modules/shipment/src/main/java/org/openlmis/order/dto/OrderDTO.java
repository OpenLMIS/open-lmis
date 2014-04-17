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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.SupplyLine;
import org.openlmis.order.domain.Order;
import org.openlmis.order.domain.OrderStatus;
import org.openlmis.rnr.dto.RnrDTO;
import org.openlmis.shipment.domain.ShipmentFileInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * OrderDTO holds Requisition DTO and order related attributes like order status, supply line, shipment error etc.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

  private Date createdDate;
  private String stringCreatedDate;
  private String orderNumber;
  private Long id;
  private RnrDTO rnr;
  private Boolean productsOrdered;
  private OrderStatus status;
  private String ftpComment;
  private SupplyLine supplyLine;
  private Boolean shipmentError;

  public static List<OrderDTO> getOrdersForView(List<Order> orders) {
    List<OrderDTO> orderDTOs = new ArrayList<>();
    for (Order order : orders) {
      orderDTOs.add(getOrderForView(order));
    }
    return orderDTOs;
  }

  private static OrderDTO getOrderForView(Order order) {
    OrderDTO orderDTO = new OrderDTO();
    orderDTO.setId(order.getId());
    orderDTO.setOrderNumber(order.getOrderNumber());
    orderDTO.setRnr(RnrDTO.prepareForOrderView(order.getRnr()));

    orderDTO.setCreatedDate(order.getCreatedDate());
    String createdDate = order.getCreatedDate() == null ? null : new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(order.getCreatedDate());
    orderDTO.setStringCreatedDate(createdDate);

    orderDTO.setStatus(order.getStatus());
    orderDTO.setFtpComment(order.getFtpComment());
    orderDTO.setSupplyLine(order.getSupplyLine());
    ShipmentFileInfo shipmentFileInfo = order.getShipmentFileInfo();
    if (shipmentFileInfo != null) orderDTO.setShipmentError(shipmentFileInfo.isProcessingError());
    if (order.getRnr().getFullSupplyLineItems().size() == 0 && order.getRnr().getNonFullSupplyLineItems().size() == 0) {
      orderDTO.setProductsOrdered(false);
    } else {
      orderDTO.setProductsOrdered(true);
    }
    return orderDTO;
  }
}
