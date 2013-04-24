/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 20.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment.file.csv.handler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.upload.model.ModelClass;
import org.openlmis.upload.parser.CsvBeanReader;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.FileInputStream;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ShipmentFileReader.class)
public class ShipmentFileReaderTest {

  @Test
  public void shouldGetUniqueOrderIdsFromShipmentFile() throws Exception {
    File shipmentFile = mock(File.class);
    CsvBeanReader csvBeanReader = mock(CsvBeanReader.class);
    RawShipment rawShipment1 = new RawShipment(1);
    RawShipment rawShipment2 = new RawShipment(2);
    RawShipment rawShipment3 = new RawShipment(1);
    FileInputStream shipmentFileStream = mock(FileInputStream.class);
    ModelClass modelClass = new ModelClass(RawShipment.class, true);

    whenNew(ModelClass.class).withArguments(RawShipment.class, true).thenReturn(modelClass);
    whenNew(FileInputStream.class).withArguments(shipmentFile).thenReturn(shipmentFileStream);
    whenNew(CsvBeanReader.class).withArguments(modelClass, shipmentFileStream).thenReturn(csvBeanReader);
    when(csvBeanReader.read()).thenReturn(rawShipment1).thenReturn(rawShipment2).thenReturn(rawShipment3).thenReturn(null);

    Set<Integer> result = new ShipmentFileReader().getOrderIds(shipmentFile);

    assertThat(result.size(), is(2));
  }
}
