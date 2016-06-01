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

@Category(UnitTests.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
public class StockCardEntryTest {

  @Test(expected = NullPointerException.class)
  public void shouldErrorOnNullStockCard() {
    new StockCardEntry(null, StockCardEntryType.ADJUSTMENT, 1L, null, null, null);
  }

  @Test(expected = NullPointerException.class)
  public void shouldErrorOnNullType() {
    new StockCardEntry(new StockCard(), null, 1L, null, null, null);
  }

}
