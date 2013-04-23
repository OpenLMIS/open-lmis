/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.order.domain.Order;
import org.openlmis.rnr.dto.RnrDTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

  Date createdDate;
  private Integer id;
  private RnrDTO rnr;
  private Boolean fulfilled;

  public static List<OrderDTO> getOrdersForView(List<Order> orders) {
    List<OrderDTO> orderDTOs = new ArrayList<>();
    for(Order order: orders) {
      orderDTOs.add(getOrderForView(order));
    }
    return orderDTOs;
  }

  private static OrderDTO getOrderForView(Order order) {
    OrderDTO orderDTO = new OrderDTO();
    orderDTO.setId(order.getId());
    orderDTO.setRnr(RnrDTO.prepareForOrderView(order.getRnr()));
    orderDTO.setCreatedDate(order.getCreatedDate());
    orderDTO.setFulfilled(order.getFulfilled());
    return orderDTO;
  }

}
