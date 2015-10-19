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

import org.apache.commons.collections.Predicate;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.lang.StringEscapeUtils;
import org.openlmis.core.domain.ConfigurationSettingKey;
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.order.domain.Order;
import org.openlmis.order.domain.OrderFileColumn;
import org.openlmis.order.dto.OrderFileTemplateDTO;
import org.openlmis.rnr.domain.LineItemComparator;
import org.openlmis.rnr.domain.RnrLineItem;
import org.springframework.beans.factory.annotation.Autowired;
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
public class OrderCsvHelper {


  public static final String STRING = "string";
  public static final String LINE_NO = "line_no";
  public static final String ORDER = "order";

  private String lineSeparator = "\r\n";

  private Boolean configurationLoaded = false;

  @Autowired
  ConfigurationSettingService configSettingService;

  public OrderCsvHelper(){

  }

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
    int counter = 1;
    for (RnrLineItem rnrLineItem : fullSupplyLineItems) {
      writeCsvLineItem(order, rnrLineItem, orderFileColumns, writer, counter ++);
      writer.write(lineSeparator);
    }
  }

  private void writeCsvLineItem(Order order, RnrLineItem rnrLineItem, List<OrderFileColumn> orderFileColumns, Writer writer, int counter) throws IOException {
    boolean encloseValuesWithQuotes = false;
    if(!configurationLoaded){
      // allow the user to control what line separator to use from the administrative pages
      // this value was different between a windows and linux target systems.
      // this could have been written better.
      lineSeparator = StringEscapeUtils.unescapeJava(configSettingService.getConfigurationStringValue(ConfigurationSettingKey.CSV_LINE_SEPARATOR));
      // setting to enclose or not to enclose values in quotes.
      encloseValuesWithQuotes = configSettingService.getBoolValue(ConfigurationSettingKey.CSV_APPLY_QUOTES);
      configurationLoaded = true;
    }
    JXPathContext orderContext = JXPathContext.newContext(order);
    JXPathContext lineItemContext = JXPathContext.newContext(rnrLineItem);
    for (OrderFileColumn orderFileColumn : orderFileColumns) {
      if (orderFileColumn.getNested() == null || orderFileColumn.getNested().isEmpty()) {
        if (orderFileColumns.indexOf(orderFileColumn) < orderFileColumns.size() - 1)
          writer.write(",");
        continue;
      }
      Object columnValue = getColumnValue(counter, orderContext, lineItemContext, orderFileColumn);

      if (columnValue instanceof Date) {
        columnValue = forPattern(orderFileColumn.getFormat()).print(((Date) columnValue).getTime());
      }
      if(encloseValuesWithQuotes) {
        writer.write("\"" + (columnValue).toString() + "\"");
      }else{
        writer.write((columnValue).toString());
      }
      if (orderFileColumns.indexOf(orderFileColumn) < orderFileColumns.size() - 1)
        writer.write(",");
    }
  }

  private Object getColumnValue(int counter, JXPathContext orderContext, JXPathContext lineItemContext, OrderFileColumn orderFileColumn) {
    Object columnValue;

    switch (orderFileColumn.getNested()) {
      case STRING:
        columnValue = orderFileColumn.getKeyPath();
        break;
      case LINE_NO:
        columnValue = counter;
        break;
      case ORDER:
        columnValue = orderContext.getValue(orderFileColumn.getKeyPath());
        break;
      default:
        columnValue = lineItemContext.getValue(orderFileColumn.getKeyPath());
        break;
    }
    return columnValue;
  }
}
