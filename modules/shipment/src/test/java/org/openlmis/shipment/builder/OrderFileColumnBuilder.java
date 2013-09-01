package org.openlmis.shipment.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.order.domain.OrderFileColumn;

public class OrderFileColumnBuilder {

  public static Property<? super OrderFileColumn, String> columnLabel = new Property<>();

  public static Property<? super OrderFileColumn, String> dataFieldLabel = new Property<>();

  public static Property<? super OrderFileColumn, String> keyPath = new Property<>();

  public static Property<? super OrderFileColumn, String> nested = new Property<>();

  public static Property<? super OrderFileColumn, String> format = new Property<>();

  public static Property<? super OrderFileColumn, Boolean> includeInOrderFile = new Property<>();

  public static final Instantiator<OrderFileColumn> defaultColumn = new Instantiator<OrderFileColumn>() {

    @Override
    public OrderFileColumn instantiate(PropertyLookup<OrderFileColumn> lookup) {
      OrderFileColumn column = new OrderFileColumn();
      column.setColumnLabel(lookup.valueOf(columnLabel, "label"));
      column.setDataFieldLabel(lookup.valueOf(dataFieldLabel, "dataFieldLabel"));
      column.setKeyPath(lookup.valueOf(keyPath, ""));
      column.setFormat(lookup.valueOf(format, ""));
      column.setNested(lookup.valueOf(nested, "order"));
      column.setIncludeInOrderFile(lookup.valueOf(includeInOrderFile, true));
      return column;
    }
  };
}
