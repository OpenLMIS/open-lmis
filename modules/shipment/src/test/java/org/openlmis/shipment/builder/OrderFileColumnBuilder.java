/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

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
