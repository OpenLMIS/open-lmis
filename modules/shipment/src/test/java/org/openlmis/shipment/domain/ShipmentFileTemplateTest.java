/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment.domain;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.openlmis.core.exception.DataException;
import org.openlmis.db.categories.UnitTests;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@Category(UnitTests.class)
public class ShipmentFileTemplateTest {

  @Rule
  public ExpectedException exException = ExpectedException.none();

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

  @Test
  public void shouldValidateShipmentFileTemplateAndThrowErrorIfMandatoryColumnIsNotIncluded() {
    ShipmentConfiguration shipmentConfiguration = new ShipmentConfiguration();
    List<ShipmentFileColumn> shipmentFileColumns = new ArrayList<>();
    ShipmentFileColumn shipmentFileColumn = new ShipmentFileColumn("productCode", "Product Code", 1, false, true, "dd/MM/yy");
    shipmentFileColumns.add(shipmentFileColumn);
    shipmentFileColumn = new ShipmentFileColumn("test_column2", "Test Column 2", 1, true, true, "dd/MM/yy");
    shipmentFileColumns.add(shipmentFileColumn);
    ShipmentFileTemplate fileTemplate = new ShipmentFileTemplate(shipmentConfiguration, shipmentFileColumns);

    exException.expect(DataException.class);
    exException.expectMessage("shipment.file.mandatory.columns.not.included");
    fileTemplate.validateAndSetModifiedBy(1l);
  }

  @Test
  public void shouldSetModifiedByIfNoError() {
    ShipmentConfiguration shipmentConfiguration = new ShipmentConfiguration();
    List<ShipmentFileColumn> shipmentFileColumns = new ArrayList<>();
    ShipmentFileColumn shipmentFileColumn = new ShipmentFileColumn("productCode", "Product Code", 1, true, true, "dd/MM/yy");
    shipmentFileColumns.add(shipmentFileColumn);
    shipmentFileColumn = new ShipmentFileColumn("test_column2", "Test Column 2", 2, true, true, "dd/MM/yy");
    shipmentFileColumns.add(shipmentFileColumn);
    ShipmentFileTemplate fileTemplate = new ShipmentFileTemplate(shipmentConfiguration, shipmentFileColumns);

    fileTemplate.validateAndSetModifiedBy(1l);

    assertThat(shipmentConfiguration.getModifiedBy(), is(1L));
    assertThat(shipmentFileColumns.get(0).getModifiedBy(), is(1L));
    assertThat(shipmentFileColumns.get(1).getModifiedBy(), is(1L));
  }
}
