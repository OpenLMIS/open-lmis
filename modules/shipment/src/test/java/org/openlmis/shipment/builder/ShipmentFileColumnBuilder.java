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
import org.openlmis.shipment.domain.ShipmentFileColumn;

import java.util.Date;

public class ShipmentFileColumnBuilder {

  public static Property<? super ShipmentFileColumn, String> fieldName = new Property<>();

  public static Property<? super ShipmentFileColumn, String> dataFieldLabel = new Property<>();

  public static Property<? super ShipmentFileColumn, Integer> columnPosition = new Property<>();

  public static Property<? super ShipmentFileColumn, Boolean> includeInShipmentFile = new Property<>();

  public static Property<? super ShipmentFileColumn, Boolean> mandatory = new Property<>();

  public static Property<? super ShipmentFileColumn, String> dateFormat = new Property<>();
  public static Property<? super ShipmentFileColumn, Long> modifiedById = new Property<>();
  public static Property<? super ShipmentFileColumn, Date> modifiedOnDate = new Property<>();


  public static final Instantiator<ShipmentFileColumn> mandatoryShipmentFileColumn = new Instantiator<ShipmentFileColumn>() {

    @Override
    public ShipmentFileColumn instantiate(PropertyLookup<ShipmentFileColumn> lookup) {
      ShipmentFileColumn column = new ShipmentFileColumn();
      column.setName(lookup.valueOf(fieldName, "name"));
      column.setDataFieldLabel(lookup.valueOf(dataFieldLabel, "label"));
      column.setPosition(lookup.valueOf(columnPosition, 1));
      column.setMandatory(true);
      column.setInclude(true);
      column.setDatePattern(lookup.valueOf(dateFormat, "MM/yy"));
      column.setModifiedBy(lookup.valueOf(modifiedById, 1L));
      column.setModifiedDate(lookup.valueOf(modifiedOnDate, new Date()));

      return column;
    }
  };

  public static final Instantiator<ShipmentFileColumn> defaultShipmentFileColumn = new Instantiator<ShipmentFileColumn>() {

    @Override
    public ShipmentFileColumn instantiate(PropertyLookup<ShipmentFileColumn> lookup) {
      ShipmentFileColumn column = new ShipmentFileColumn();
      column.setName(lookup.valueOf(fieldName, "name"));
      column.setDataFieldLabel(lookup.valueOf(dataFieldLabel, "label"));
      column.setPosition(lookup.valueOf(columnPosition, 1));
      column.setMandatory(lookup.valueOf(mandatory, true));
      column.setInclude(lookup.valueOf(includeInShipmentFile, true));
      column.setDatePattern(lookup.valueOf(dateFormat, "dd/MM/yy"));
      return column;
    }
  };
}
