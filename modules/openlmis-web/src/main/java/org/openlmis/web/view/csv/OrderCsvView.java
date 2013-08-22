package org.openlmis.web.view.csv;


import org.openlmis.core.service.MessageService;
import org.openlmis.order.domain.Order;
import org.openlmis.rnr.domain.RnrLineItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.openlmis.web.controller.OrderController.ORDER;

@Component
public class OrderCsvView extends AbstractView {

  MessageService messageService;

  @Autowired
  public OrderCsvView(MessageService messageService) {
    this.messageService = messageService;
  }

  @Override
  protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) {
    Order order = (Order) model.get(ORDER);

    String fileName = "O" + order.getId() + ".csv";
    response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

    try (BufferedWriter writer = new BufferedWriter(response.getWriter())) {
      String header = messageService.message("header.order.number") + ", " + messageService.message("create.facility.code") + ", " +
        messageService.message("header.product.code") + ", " + messageService.message("header.quantity.ordered") + ", "
        + messageService.message("header.pack.size");
      writer.write(header);
      writer.newLine();
      writeLineItems(order, writer, order.getRnr().getFullSupplyLineItems());
      writeLineItems(order, writer, order.getRnr().getNonFullSupplyLineItems());
      writer.flush();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void writeLineItems(Order order, BufferedWriter writer, List<RnrLineItem> fullSupplyLineItems) throws IOException {
    for (RnrLineItem rnrLineItem : fullSupplyLineItems) {
      writeCsvLineItem(order, rnrLineItem, writer);
    }
  }

  private void writeCsvLineItem(Order order, RnrLineItem rnrLineItem, BufferedWriter writer) throws IOException {
    StringBuilder csvLineItem = new StringBuilder();
    csvLineItem.append(order.getRnr().getId() + ",");
    csvLineItem.append(order.getRnr().getFacility().getCode() + ",");
    csvLineItem.append(rnrLineItem.getProductCode() + ",");
    csvLineItem.append(rnrLineItem.getPacksToShip() + ",");
    csvLineItem.append(rnrLineItem.getPackSize());
    writer.write(csvLineItem.toString());
    writer.newLine();
  }

}
