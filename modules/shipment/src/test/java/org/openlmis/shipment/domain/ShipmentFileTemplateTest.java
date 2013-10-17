/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.shipment.domain;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.openlmis.core.domain.EDIConfiguration;
import org.openlmis.core.domain.EDIFileColumn;
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
    EDIConfiguration shipmentConfiguration = new EDIConfiguration();
    List<EDIFileColumn> shipmentFileColumns = new ArrayList<>();
    EDIFileColumn shipmentFileColumn = new EDIFileColumn("test_column1", "Test Column 1", true, true, 1, "dd/MM/yy");
    shipmentFileColumns.add(shipmentFileColumn);
    shipmentFileColumn = new EDIFileColumn("test_column2", "Test Column 2", true, true, 1, "dd/MM/yy");
    shipmentFileColumns.add(shipmentFileColumn);
    ShipmentFileTemplate fileTemplate = new ShipmentFileTemplate(shipmentConfiguration, shipmentFileColumns);

    exException.expect(DataException.class);
    exException.expectMessage("shipment.file.duplicate.position");
    fileTemplate.validateAndSetModifiedBy(1l);
  }

  @Test
  public void shouldValidateShipmentFileTemplateAndThrowErrorIfMandatoryColumnIsNotIncluded() {
    EDIConfiguration shipmentConfiguration = new EDIConfiguration();
    List<EDIFileColumn> shipmentFileColumns = new ArrayList<>();
    EDIFileColumn shipmentFileColumn = new EDIFileColumn("productCode", "Product Code", false, true, 1, "dd/MM/yy");
    shipmentFileColumns.add(shipmentFileColumn);
    shipmentFileColumn = new EDIFileColumn("test_column2", "Test Column 2", true, true, 1, "dd/MM/yy");
    shipmentFileColumns.add(shipmentFileColumn);
    ShipmentFileTemplate fileTemplate = new ShipmentFileTemplate(shipmentConfiguration, shipmentFileColumns);

    exException.expect(DataException.class);
    exException.expectMessage("shipment.file.mandatory.columns.not.included");
    fileTemplate.validateAndSetModifiedBy(1l);
  }

  @Test
  public void shouldSetModifiedByIfNoError() {
    EDIConfiguration shipmentConfiguration = new EDIConfiguration();
    List<EDIFileColumn> shipmentFileColumns = new ArrayList<>();
    EDIFileColumn shipmentFileColumn = new EDIFileColumn("productCode", "Product Code", true, true, 1, "dd/MM/yy");
    shipmentFileColumns.add(shipmentFileColumn);
    shipmentFileColumn = new EDIFileColumn("test_column2", "Test Column 2", true, true, 2, "dd/MM/yy");
    shipmentFileColumns.add(shipmentFileColumn);
    ShipmentFileTemplate fileTemplate = new ShipmentFileTemplate(shipmentConfiguration, shipmentFileColumns);

    fileTemplate.validateAndSetModifiedBy(1l);

    assertThat(shipmentConfiguration.getModifiedBy(), is(1L));
    assertThat(shipmentFileColumns.get(0).getModifiedBy(), is(1L));
    assertThat(shipmentFileColumns.get(1).getModifiedBy(), is(1L));
  }
}
