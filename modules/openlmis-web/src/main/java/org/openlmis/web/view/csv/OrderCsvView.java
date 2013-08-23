package org.openlmis.web.view.csv;


import org.apache.commons.collections.Predicate;
import org.apache.commons.jxpath.JXPathContext;
import org.openlmis.core.domain.OrderConfiguration;
import org.openlmis.core.service.MessageService;
import org.openlmis.order.domain.Order;
import org.openlmis.order.domain.OrderFileColumn;
import org.openlmis.order.dto.OrderFileTemplateDTO;
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

import static org.apache.commons.collections.CollectionUtils.filter;
import static org.openlmis.web.controller.OrderController.ORDER;
import static org.openlmis.web.controller.OrderController.ORDER_FILE_TEMPLATE;

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
    List<OrderFileColumn> orderFileColumns = ((OrderFileTemplateDTO) model.get(ORDER_FILE_TEMPLATE)).getOrderFileColumns();
    removeExcludedColumns(orderFileColumns);

    OrderConfiguration orderConfiguration = ((OrderFileTemplateDTO) model.get(ORDER_FILE_TEMPLATE)).getOrderConfiguration();

    try (BufferedWriter writer = new BufferedWriter(response.getWriter())) {
      if (orderConfiguration.getHeaderInFile()) {
        writeHeader(orderFileColumns, writer);
      }
      writeLineItems(order, order.getRnr().getFullSupplyLineItems(), orderFileColumns, writer);
      writeLineItems(order, order.getRnr().getNonFullSupplyLineItems(), orderFileColumns, writer);
      writer.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void removeExcludedColumns(List<OrderFileColumn> orderFileColumns) {
    filter(orderFileColumns, new Predicate() {
      @Override
      public boolean evaluate(Object o) {
        return ((OrderFileColumn) o).getIncludeInOrderFile();
      }
    });
  }

  private void writeHeader(List<OrderFileColumn> orderFileColumns, BufferedWriter writer) throws IOException {
    for (OrderFileColumn column : orderFileColumns) {
      writer.write(column.getColumnLabel());
      if (orderFileColumns.indexOf(column) == (orderFileColumns.size() - 1)) {
        writer.newLine();
        break;
      }
      writer.write(",");
    }
  }

  private void writeLineItems(Order order, List<RnrLineItem> fullSupplyLineItems, List<OrderFileColumn> orderFileColumns, BufferedWriter writer) throws IOException {
    for (RnrLineItem rnrLineItem : fullSupplyLineItems) {
      writeCsvLineItem(order, rnrLineItem, orderFileColumns, writer);
      writer.newLine();
    }
  }

  private void writeCsvLineItem(Order order, RnrLineItem rnrLineItem, List<OrderFileColumn> orderFileColumns, BufferedWriter writer) throws IOException {
    JXPathContext orderContext = JXPathContext.newContext(order);
    JXPathContext lineItemContext = JXPathContext.newContext(rnrLineItem);
    for (OrderFileColumn orderFileColumn : orderFileColumns) {
      if (orderFileColumn.getNested() == null || orderFileColumn.getNested().isEmpty()) {
        writer.write(",");
        continue;
      }

      if (orderFileColumn.getNested().equals("order")) {
        writer.write(orderContext.getValue(orderFileColumn.getKeyPath()).toString());
      } else {
        writer.write(lineItemContext.getValue(orderFileColumn.getKeyPath()).toString());
      }
      if (orderFileColumns.indexOf(orderFileColumn) < orderFileColumns.size() - 1)
        writer.write(",");
    }
  }
}
