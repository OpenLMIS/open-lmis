/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.distribution.domain;

import org.junit.Test;
import org.openlmis.core.domain.ProductGroup;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EpiUseLineItemTest {

  @Test
  public void shouldReturnFalseIfStockAtFirstOfMonthIsNegative() throws Exception {
    EpiUseLineItem lineItem = new EpiUseLineItem(1L, new ProductGroup(), -4, 5, 6, 7, 8, "11/2020");

    assertFalse(lineItem.isValid());
  }

  @Test
  public void shouldReturnFalseIfStockAtEndOfMonthIsNegative() throws Exception {
    EpiUseLineItem lineItem = new EpiUseLineItem(1L, new ProductGroup(), 4, -5, 6, 7, 8, "11/2020");

    assertFalse(lineItem.isValid());
  }

  @Test
  public void shouldReturnFalseIfDistributedIsNegative() throws Exception {
    EpiUseLineItem lineItem = new EpiUseLineItem(1L, new ProductGroup(), 4, 5, 6, 7, -8, "11/2020");

    assertFalse(lineItem.isValid());
  }

  @Test
  public void shouldReturnFalseIfReceivedIsNegative() throws Exception {
    EpiUseLineItem lineItem = new EpiUseLineItem(1L, new ProductGroup(), 4, 5, -6, 7, 8, "11/2020");

    assertFalse(lineItem.isValid());
  }

  @Test
  public void shouldReturnFalseIfLossIsNegative() throws Exception {
    EpiUseLineItem lineItem = new EpiUseLineItem(1L, new ProductGroup(), 4, 5, 6, -7, 8, "11/2020");

    assertFalse(lineItem.isValid());
  }

  @Test
  public void shouldReturnTrueIfAllValuesValid() throws Exception {
    EpiUseLineItem lineItem = new EpiUseLineItem(1L, new ProductGroup(), 4, 5, 6, 7, 8, "11/2020");

    assertTrue(lineItem.isValid());
  }
}
