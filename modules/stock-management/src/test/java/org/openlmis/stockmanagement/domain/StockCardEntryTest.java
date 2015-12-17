/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.stockmanagement.domain;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.openlmis.db.categories.UnitTests;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@Category(UnitTests.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
public class StockCardEntryTest {

  @Test(expected = NullPointerException.class)
  public void shouldErrorOnNullStockCard() {
    new StockCardEntry(null, StockCardEntryType.ADJUSTMENT, 1L, null, null);
  }

  @Test(expected = NullPointerException.class)
  public void shouldErrorOnNullType() {
    new StockCardEntry(new StockCard(), null, 1L, null, null);
  }

  @Test
  public void shouldGetNullCustomPropsFromEmptyKeyValues() {
    StockCardEntry entry = new StockCardEntry(new StockCard(), StockCardEntryType.ADJUSTMENT, 1L, null, null);

    Map<String, String> customProps = entry.getCustomProps();

    assertNull(customProps);
  }

  @Test
  public void shouldGetCustomPropsFromKeyValues() {
    List<StockCardEntryKV> keyValues = new ArrayList<>();
    keyValues.add(new StockCardEntryKV("testkey1", "testvalue1", new Date()));
    keyValues.add(new StockCardEntryKV("testkey2", "testvalue2", new Date()));
    StockCardEntry entry = new StockCardEntry(new StockCard(), StockCardEntryType.ADJUSTMENT, 1L, null, null);
    entry.setKeyValues(keyValues);

    Map<String, String> customProps = entry.getCustomProps();

    assertEquals(customProps.get("testkey1"), "testvalue1");
    assertEquals(customProps.get("testkey2"), "testvalue2");
  }
}
