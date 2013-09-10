/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.view.csv;


import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.MessageService;
import org.openlmis.order.domain.Order;
import org.openlmis.order.dto.OrderFileTemplateDTO;
import org.openlmis.order.helper.OrderCsvHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;

import static org.openlmis.web.controller.OrderController.ORDER;
import static org.openlmis.web.controller.OrderController.ORDER_FILE_TEMPLATE;

@Component
public class OrderCsvView extends AbstractView {

  MessageService messageService;
  private OrderCsvHelper csvHelper;

  @Autowired
  public OrderCsvView(MessageService messageService, OrderCsvHelper csvHelper) {
    this.messageService = messageService;
    this.csvHelper = csvHelper;
  }

  @Override
  protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) {
    Order order = (Order) model.get(ORDER);
    OrderFileTemplateDTO orderFileTemplateDTO = (OrderFileTemplateDTO) model.get(ORDER_FILE_TEMPLATE);

    String fileName = orderFileTemplateDTO.getOrderConfiguration().getFilePrefix() + order.getId() + ".csv";
    response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

    try (BufferedWriter writer = new BufferedWriter(response.getWriter())) {
      csvHelper.writeCsvFile(order, orderFileTemplateDTO, writer);
      writer.flush();
    } catch (IOException e) {
      throw new DataException(e.getMessage());
    }
  }

}
