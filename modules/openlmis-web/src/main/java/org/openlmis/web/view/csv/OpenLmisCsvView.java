package org.openlmis.web.view.csv;


import org.openlmis.order.domain.Order;
import org.openlmis.rnr.domain.RnrLineItem;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.openlmis.web.controller.OrderController.ORDER;

public class OpenLmisCsvView extends AbstractView {


  @Override
  protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) {
    Order order = (Order) model.get(ORDER);

    String fileName = "O" + System.currentTimeMillis() + ".csv";
    response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

    try (BufferedWriter writer = new BufferedWriter(response.getWriter())) {
      String header = "Order Number, Facility Code, Product Code, Quantity Ordered ,Pack Size";
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
