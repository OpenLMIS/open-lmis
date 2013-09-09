/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.order.helper;


import org.apache.commons.collections.Predicate;
import org.apache.commons.jxpath.JXPathContext;
import org.openlmis.order.domain.Order;
import org.openlmis.order.domain.OrderFileColumn;
import org.openlmis.order.dto.OrderFileTemplateDTO;
import org.openlmis.rnr.domain.RnrLineItem;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.filter;
import static org.joda.time.format.DateTimeFormat.forPattern;

@Component
public class OrderCsvHelper {

  public void writeCsvFile(Order order, OrderFileTemplateDTO orderFileTemplateDTO, Writer writer) throws IOException {
    List<OrderFileColumn> orderFileColumns = orderFileTemplateDTO.getOrderFileColumns();
    removeExcludedColumns(orderFileColumns);
    if (orderFileTemplateDTO.getOrderConfiguration().getHeaderInFile()) {
      writeHeader(orderFileColumns, writer);
    }
    writeLineItems(order, order.getRnr().getFullSupplyLineItems(), orderFileColumns, writer);
    writeLineItems(order, order.getRnr().getNonFullSupplyLineItems(), orderFileColumns, writer);
  }

  private void removeExcludedColumns(List<OrderFileColumn> orderFileColumns) {
    filter(orderFileColumns, new Predicate() {
      @Override
      public boolean evaluate(Object o) {
        return ((OrderFileColumn) o).getIncludeInOrderFile();
      }
    });
  }

  private void writeHeader(List<OrderFileColumn> orderFileColumns, Writer writer) throws IOException {
    for (OrderFileColumn column : orderFileColumns) {
      String columnLabel = column.getColumnLabel();
      if (columnLabel == null) columnLabel = "";
      writer.write(columnLabel);
      if (orderFileColumns.indexOf(column) == (orderFileColumns.size() - 1)) {
        writer.write(System.getProperty("line.separator"));
        break;
      }
      writer.write(",");
    }
  }

  private void writeLineItems(Order order, List<RnrLineItem> fullSupplyLineItems, List<OrderFileColumn> orderFileColumns, Writer writer) throws IOException {
    for (RnrLineItem rnrLineItem : fullSupplyLineItems) {
      writeCsvLineItem(order, rnrLineItem, orderFileColumns, writer);
      writer.write(System.getProperty("line.separator"));
    }
  }

  private void writeCsvLineItem(Order order, RnrLineItem rnrLineItem, List<OrderFileColumn> orderFileColumns, Writer writer) throws IOException {
    JXPathContext orderContext = JXPathContext.newContext(order);
    JXPathContext lineItemContext = JXPathContext.newContext(rnrLineItem);
    for (OrderFileColumn orderFileColumn : orderFileColumns) {
      if (orderFileColumn.getNested() == null || orderFileColumn.getNested().isEmpty()) {
        writer.write(",");
        continue;
      }
      Object columnValue;
      if (orderFileColumn.getNested().equals("order")) {
        columnValue = orderContext.getValue(orderFileColumn.getKeyPath());
      } else {
        columnValue = lineItemContext.getValue(orderFileColumn.getKeyPath());
      }
      if (columnValue instanceof Date) {
        columnValue = forPattern(orderFileColumn.getFormat()).print(((Date) columnValue).getTime());
      }
      writer.write((columnValue).toString());
      if (orderFileColumns.indexOf(orderFileColumn) < orderFileColumns.size() - 1)
        writer.write(",");
    }
  }
}
