/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.order.helper;

import lombok.NoArgsConstructor;
import org.apache.commons.collections.Predicate;
import org.apache.commons.jxpath.JXPathContext;
import org.openlmis.order.domain.Order;
import org.openlmis.order.domain.OrderFileColumn;
import org.openlmis.order.dto.OrderFileTemplateDTO;
import org.openlmis.rnr.domain.LineItemComparator;
import org.openlmis.rnr.domain.RnrLineItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.filter;
import static org.joda.time.format.DateTimeFormat.forPattern;

/**
 * OrderCsvHelper provides helper methods to generate a csv file for Order entity.
 */

@Component
@NoArgsConstructor
public class OrderCsvHelper {

  // apply the windows line break
  // TODO: take this to the configuration
  String lineSeparator = "\r\n";

  public void writeCsvFile(Order order, OrderFileTemplateDTO orderFileTemplateDTO, Writer writer) throws IOException {
    List<OrderFileColumn> orderFileColumns = orderFileTemplateDTO.getOrderFileColumns();
    removeExcludedColumns(orderFileColumns);
    if (orderFileTemplateDTO.getOrderConfiguration().isHeaderInFile()) {
      writeHeader(orderFileColumns, writer);
    }
    List<RnrLineItem> nonFullSupplyLineItems = order.getRnr().getNonFullSupplyLineItems();
    Collections.sort(nonFullSupplyLineItems, new LineItemComparator());

    writeLineItems(order, order.getRnr().getFullSupplyLineItems(), orderFileColumns, writer);
    writeLineItems(order, nonFullSupplyLineItems, orderFileColumns, writer);
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
        writer.write(lineSeparator);
        break;
      }
      writer.write(",");
    }
  }

  private void writeLineItems(Order order, List<RnrLineItem> fullSupplyLineItems, List<OrderFileColumn> orderFileColumns, Writer writer) throws IOException {
    for (RnrLineItem rnrLineItem : fullSupplyLineItems) {
      writeCsvLineItem(order, rnrLineItem, orderFileColumns, writer);
      writer.write(lineSeparator);
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
      writer.write("\"" + (columnValue).toString() + "\"");
      if (orderFileColumns.indexOf(orderFileColumn) < orderFileColumns.size() - 1)
        writer.write(",");
    }
  }
}
