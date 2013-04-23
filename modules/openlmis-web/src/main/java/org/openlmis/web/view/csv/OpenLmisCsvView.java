package org.openlmis.web.view.csv;


import org.openlmis.order.domain.Order;
import org.openlmis.rnr.domain.RnrLineItem;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

import static org.openlmis.web.controller.OrderController.ORDER;

public class OpenLmisCsvView extends AbstractView {


  @Override
  protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) {
    Order order = (Order) model.get(ORDER);

    String fileName = "O" + new Date() + ".csv";
    response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

    try (BufferedWriter writer = new BufferedWriter(response.getWriter())) {
      for (RnrLineItem rnrLineItem : order.getRnr().getFullSupplyLineItems()) {
        writeCsvLineItem(order, rnrLineItem, writer);
      }
      for (RnrLineItem rnrLineItem : order.getRnr().getNonFullSupplyLineItems()) {
        writeCsvLineItem(order, rnrLineItem, writer);
      }
      writer.flush();
    } catch (IOException e) {
      e.printStackTrace();
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
