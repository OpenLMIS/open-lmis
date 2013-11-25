/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.rnr.domain;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.rnr.calculation.DefaultStrategy;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
@Category(UnitTests.class)
public class DefaultStrategyTest {

  private DefaultStrategy calcStrategy;
  private List<LossesAndAdjustmentsType> lossesAndAdjustmentsList;

  @Before
  public void setup() {
    calcStrategy = new DefaultStrategy();
    LossesAndAdjustmentsType additive1 = new LossesAndAdjustmentsType("TRANSFER_IN", "TRANSFER IN", true, 1);
    LossesAndAdjustmentsType additive2 = new LossesAndAdjustmentsType("additive2", "Additive 2", true, 2);
    LossesAndAdjustmentsType subtractive1 = new LossesAndAdjustmentsType("subtractive1", "Subtractive 1", false, 3);
    LossesAndAdjustmentsType subtractive2 = new LossesAndAdjustmentsType("subtractive2", "Subtractive 2", false, 4);

    lossesAndAdjustmentsList = asList(additive1, additive2, subtractive1, subtractive2);
  }

  @Test
  public void shouldRecalculateNormalizedConsumption() throws Exception {
    assertThat(calcStrategy.calculateNormalizedConsumption(3, 10, 3, 30, 10, null), is(37));
  }

  @Test
  public void shouldCalculateAmcWhenNumberOfMonthsInPeriodIsThree() throws Exception {
    assertThat(calcStrategy.calculateAmc(45, Collections.<Integer>emptyList()), is(45));
  }

  @Test
  public void shouldCalculateAmcWhenNumberOfMonthsInPeriodIsTwo() throws Exception {
    assertThat(calcStrategy.calculateAmc(45, asList(12)), is(29));
  }

  @Test
  public void shouldCalculateAmcWhenNumberOfMonthsInPeriodIsOne() throws Exception {
    assertThat(calcStrategy.calculateAmc(45, asList(12, 13)), is(23));
  }

  @Test
  public void shouldRecalculateMaxStockQuantityBasedOnCalculatedAMC() throws Exception {
    assertThat(calcStrategy.calculateMaxStockQuantity(10, 37), is(370));
  }

  @Test
  public void shouldCalculateOrderQuantityBasedOnCalculatedMaxStockQuantityAndStockInHand() throws Exception {
    assertThat(calcStrategy.calculateOrderQuantity(370, 4), is(366));
  }

  @Test
  public void shouldCalculateOrderQuantityAsZeroIfMaxStockQuantityLessThanStockInHand() throws Exception {
    assertThat(calcStrategy.calculateOrderQuantity(370, 400), is(0));
  }

  @Test
  public void shouldRecalculateTotalLossesAndAdjustments() throws Exception {
    LossesAndAdjustmentsType additive = new LossesAndAdjustmentsType();
    additive.setName("TRANSFER_IN");
    LossesAndAdjustmentsType subtractive = new LossesAndAdjustmentsType();
    subtractive.setName("subtractive1");
    LossesAndAdjustments add10 = new LossesAndAdjustments(1L, additive, 10);
    LossesAndAdjustments sub5 = new LossesAndAdjustments(1L, subtractive, 5);
    LossesAndAdjustments add20 = new LossesAndAdjustments(1L, additive, 20);

    assertThat(calcStrategy.calculateTotalLossesAndAdjustments(asList(add10, sub5, add20), lossesAndAdjustmentsList), is(25));
  }

  @Test
  public void shouldSetCalculatedOrderQuantityAsDefaultApprovedQuantityForFullSupplyItems() throws Exception {
    assertThat(calcStrategy.calculateDefaultApprovedQuantity(true, 1, 6), is(1));
  }

  @Test
  public void shouldSetRequestedQuantityAsApprovedQuantityForNonFullSupplyItems() throws Exception {
    assertThat(calcStrategy.calculateDefaultApprovedQuantity(false, 1, 6), is(6));
  }
}
