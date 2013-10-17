/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.shipment.repository;


import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.EDIConfiguration;
import org.openlmis.core.domain.EDIFileColumn;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.shipment.repository.mapper.ShipmentConfigurationMapper;
import org.openlmis.shipment.repository.mapper.ShipmentFileColumnMapper;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ShipmentTemplateRepositoryTest {

  @Mock
  ShipmentFileColumnMapper shipmentFileColumnMapper;

  @Mock
  ShipmentConfigurationMapper shipmentConfigurationMapper;

  @InjectMocks
  ShipmentTemplateRepository repository;

  @Test
  public void shouldGetAllShipmentFileColumns() {
    List<EDIFileColumn> expectedShipmentColumns = new ArrayList<>();
    when(shipmentFileColumnMapper.getAll()).thenReturn(expectedShipmentColumns);
    List<EDIFileColumn> shipmentFileColumns = repository.getAllShipmentFileColumns();
    assertThat(shipmentFileColumns, is(expectedShipmentColumns));
    verify(shipmentFileColumnMapper).getAll();
  }

  @Test
  public void shouldGetShipmentConfiguration() {
    EDIConfiguration expectedShipmentConfiguration = new EDIConfiguration();
    when(shipmentConfigurationMapper.get()).thenReturn(expectedShipmentConfiguration);

    EDIConfiguration shipmentConfiguration = repository.getShipmentConfiguration();

    assertThat(shipmentConfiguration, is(expectedShipmentConfiguration));
    verify(shipmentConfigurationMapper).get();
  }

  @Test
  public void shouldUpdateShipmentConfiguration() {
    EDIConfiguration shipmentConfiguration = new EDIConfiguration();

    repository.updateShipmentConfiguration(shipmentConfiguration);

    verify(shipmentConfigurationMapper).update(shipmentConfiguration);
  }


  @Test
  public void shouldInsertShipmentFileColumn() {
    EDIFileColumn shipmentFileColumn = new EDIFileColumn();

    repository.update(shipmentFileColumn);

    verify(shipmentFileColumnMapper).update(shipmentFileColumn);
  }
}
