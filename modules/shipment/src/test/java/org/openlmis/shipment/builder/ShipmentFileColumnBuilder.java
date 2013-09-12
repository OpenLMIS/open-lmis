/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
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
