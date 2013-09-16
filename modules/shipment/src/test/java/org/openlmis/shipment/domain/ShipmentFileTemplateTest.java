package org.openlmis.shipment.domain;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openlmis.core.exception.DataException;

import java.util.ArrayList;
import java.util.List;

public class ShipmentFileTemplateTest {

  @Rule
  public ExpectedException exException = ExpectedException.none();

  @Test
  public void shouldValidateShipmentFileTemplateAndThrowErrorIfPositionIsNull() {
    ShipmentConfiguration shipmentConfiguration = new ShipmentConfiguration();
    List<ShipmentFileColumn> shipmentFileColumns = new ArrayList<>();
    ShipmentFileColumn shipmentFileColumn = new ShipmentFileColumn("test_column", "Test Column", null, true, true, "dd/MM/yy");
    shipmentFileColumns.add(shipmentFileColumn);
    ShipmentFileTemplate fileTemplate = new ShipmentFileTemplate(shipmentConfiguration, shipmentFileColumns);

    exException.expect(DataException.class);
    exException.expectMessage("shipment.file.invalid.position");
    fileTemplate.validateAndSetModifiedBy(1l);
  }

  @Test
  public void shouldValidateShipmentFileTemplateAndThrowErrorIfPositionIsDuplicate() {
    ShipmentConfiguration shipmentConfiguration = new ShipmentConfiguration();
    List<ShipmentFileColumn> shipmentFileColumns = new ArrayList<>();
    ShipmentFileColumn shipmentFileColumn = new ShipmentFileColumn("test_column1", "Test Column 1", 1, true, true, "dd/MM/yy");
    shipmentFileColumns.add(shipmentFileColumn);
    shipmentFileColumn = new ShipmentFileColumn("test_column2", "Test Column 2", 1, true, true, "dd/MM/yy");
    shipmentFileColumns.add(shipmentFileColumn);
    ShipmentFileTemplate fileTemplate = new ShipmentFileTemplate(shipmentConfiguration, shipmentFileColumns);

    exException.expect(DataException.class);
    exException.expectMessage("shipment.file.duplicate.position");
    fileTemplate.validateAndSetModifiedBy(1l);
  }
}
