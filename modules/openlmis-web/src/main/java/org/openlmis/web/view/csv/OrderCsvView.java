/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.web.view.csv;

import org.openlmis.core.exception.DataException;
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

/**
 * This entity represents view to be shown in csv report for order.
 */
@Component
public class OrderCsvView extends AbstractView {

  @Autowired
  private OrderCsvHelper csvHelper;

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
