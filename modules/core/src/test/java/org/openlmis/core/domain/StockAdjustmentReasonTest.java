/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.domain;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openlmis.db.categories.UnitTests;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@Category(UnitTests.class)
public class StockAdjustmentReasonTest {

  @Test
  public void shouldParseDirtyCategoryString() {
    for(StockAdjustmentReason.Category c : StockAdjustmentReason.Category.values()) {
      String cStr = "   " + StringUtils.swapCase(c.toString()) + " "; // swap case and throw in some spaces
      StockAdjustmentReason.Category cParsed = StockAdjustmentReason.Category.parse(cStr);
      assertThat(cParsed, is(c));
    }
  }

  @Test
  public void shouldParseNullCategoryString() {
    assertThat(StockAdjustmentReason.Category.parse(null), nullValue());
  }

  @Test
  public void shouldParseEmptyCategoryString() {
    assertThat(StockAdjustmentReason.Category.parse(""), nullValue());
  }

  @Test
  public void shouldNotBeInAnyCategoryIfNull() {
    StockAdjustmentReason invalidReason = new StockAdjustmentReason();  // use unsafe constructor so category is null
    assertThat(invalidReason.inCategory(null), is(false));
  }

  @Test
  public void shouldNotHaveNullCategory() {
    StockAdjustmentReason invalidReason = new StockAdjustmentReason();
    assertThat(invalidReason.inCategory(StockAdjustmentReason.Category.DEFAULT), is(true));
  }
}
