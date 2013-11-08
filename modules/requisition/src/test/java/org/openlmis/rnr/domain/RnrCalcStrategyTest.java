/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.domain;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.db.categories.UnitTests;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
@Category(UnitTests.class)
public class RnrCalcStrategyTest {
  private RnrCalcStrategy calcStrategy;
  private ProcessingPeriod period;
  private ProgramRnrTemplate template;
  private List<LossesAndAdjustmentsType> lossesAndAdjustmentsList;

  @Before
  public void setup() {
    calcStrategy = new RnrCalcStrategy();
    period = new ProcessingPeriod() {{
      setNumberOfMonths(1);
    }};
    template = new ProgramRnrTemplate();
    LossesAndAdjustmentsType additive1 = new LossesAndAdjustmentsType("TRANSFER_IN", "TRANSFER IN", true, 1);
    LossesAndAdjustmentsType additive2 = new LossesAndAdjustmentsType("additive2", "Additive 2", true, 2);
    LossesAndAdjustmentsType subtractive1 = new LossesAndAdjustmentsType("subtractive1", "Subtractive 1", false, 3);
    LossesAndAdjustmentsType subtractive2 = new LossesAndAdjustmentsType("subtractive2", "Subtractive 2", false, 4);

    lossesAndAdjustmentsList = asList(
      new LossesAndAdjustmentsType[]{additive1, additive2, subtractive1, subtractive2});

  }

  @Test
  public void shouldRecalculateNormalizedConsumption() throws Exception {
    assertThat(calcStrategy.calculateNormalizedConsumption(3, 10, 3, 30, 10), is(37));
  }

  @Test
  public void shouldCalculateAmcWhenNumberOfMonthsInPeriodIsThree() throws Exception {
    period.setNumberOfMonths(3);
    assertThat(calcStrategy.calculateAmc(period, 45, Collections.<Integer>emptyList()), is(15));
  }

  @Test
  public void shouldCalculateAmcWhenNumberOfMonthsInPeriodIsTwo() throws Exception {
    period.setNumberOfMonths(2);
    assertThat(calcStrategy.calculateAmc(period, 45, asList(12)), is(14));
  }

  @Test
  public void shouldCalculateAmcWhenNumberOfMonthsInPeriodIsOne() throws Exception {
    assertThat(calcStrategy.calculateAmc(period, 45, asList(12, 13)), is(23));
  }

  @Test
  public void shouldCalculateAmcWhenNumberOfMonthsInPeriodIsOneAndOnlyOnePreviousConsumptionIsAvailable() throws Exception {
    assertThat(calcStrategy.calculateAmc(period, 45, asList(12)), is(29));
  }

  @Test
  public void shouldCalculateAmcWhenNumberOfMonthsInPeriodIsOneAndOnlyNoPreviousConsumptionsAreAvailable() throws Exception {
    assertThat(calcStrategy.calculateAmc(period, 45, Collections.<Integer>emptyList()), is(45));
  }

  @Test
  public void shouldCalculateAmcWhenNumberOfMonthsInPeriodIsTwoAndPreviousConsumptionIsNotAvailable() throws Exception {
    period.setNumberOfMonths(2);

    assertThat(calcStrategy.calculateAmc(period, 45, Collections.<Integer>emptyList()), is(23));
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
    assertThat(calcStrategy.calculateDefaultApprovedQuantity(true, 1, 6), is(6));
  }

  @Test
  public void shouldSetRequestedQuantityAsApprovedQuantityForNonFullSupplyItems() throws Exception {
    assertThat(calcStrategy.calculateDefaultApprovedQuantity(false, 1, 6), is(6));
  }
}
