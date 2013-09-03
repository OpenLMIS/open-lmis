package org.openlmis.shipment.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.shipment.domain.ShipmentFileColumn;

public class ShipmentFileColumnBuilder {

  public static Property<? super ShipmentFileColumn, String> dataFieldLabel = new Property<>();

  public static Property<? super ShipmentFileColumn, Integer> columnPosition = new Property<>();

  public static Property<? super ShipmentFileColumn, Boolean> includeInShipmentFile = new Property<>();

  public static Property<? super ShipmentFileColumn, Boolean> mandatory = new Property<>();

  public static Property<? super ShipmentFileColumn, String> dateFormat = new Property<>();


  public static final Instantiator<ShipmentFileColumn> mandatoryShipmentFileColumn = new Instantiator<ShipmentFileColumn>() {

    @Override
    public ShipmentFileColumn instantiate(PropertyLookup<ShipmentFileColumn> lookup) {
      ShipmentFileColumn column = new ShipmentFileColumn();

      column.setDataFieldLabel(lookup.valueOf(dataFieldLabel, "label"));
      column.setPosition(lookup.valueOf(columnPosition, 1));
      column.setMandatory(true);
      column.setIncludedInShipmentFile(true);
      column.setDatePattern(null);
      return column;
    }
  };
}
