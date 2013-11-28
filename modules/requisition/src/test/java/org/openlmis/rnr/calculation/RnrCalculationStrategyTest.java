/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 *  Copyright © 2013 VillageReach
 *
 *   This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *   You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.rnr.calculation;

import org.junit.Test;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class RnrCalculationStrategyTest {

  private RnrCalculationStrategy calculationStrategy = new RnrCalculationStrategy();


  @Test
  public void shouldCalculatePacksToShipWhenPackRoundingThresholdIsSmallerThanRemainder() throws Exception {
    assertThat(calculationStrategy.calculatePacksToShip(26, 10, 4, false), is(3));
  }

  @Test
  public void shouldCalculatePacksToShipWhenPackRoundingThresholdIsGreaterThanRemainder() throws Exception {
    assertThat(calculationStrategy.calculatePacksToShip(26, 10, 7, false), is(2));
  }

  @Test
  public void shouldCalculatePacksToShipWhenCanRoundToZero() throws Exception {
    assertThat(calculationStrategy.calculatePacksToShip(6, 10, 7, true), is(0));
  }

  @Test
  public void shouldCalculatePacksToShipWhenCanNotRoundToZero() throws Exception {
    assertThat(calculationStrategy.calculatePacksToShip(6, 10, 7, false), is(1));
  }

  @Test
  public void shouldReturnNullPacksToShipIfPackSizeIsNull() throws Exception {
    assertThat(calculationStrategy.calculatePacksToShip(6, null, 7, true), is(nullValue()));
  }

  @Test
  public void shouldCalculateMaxStockQuantity() throws Exception {
    assertThat(calculationStrategy.calculateMaxStockQuantity(2, 5), is(10));
  }

  @Test
  public void shouldReturnOrderedQuantityZeroIfStockInHandExceedsMaxStockQuantity() throws Exception {
    assertThat(calculationStrategy.calculateOrderQuantity(10, 11), is(0));
  }

  @Test
  public void shouldReturnOrderedQuantityIfStockInHandIsLessThanMaxStockQuantity() throws Exception {
    assertThat(calculationStrategy.calculateOrderQuantity(11, 10), is(1));
  }

  @Test
  public void shouldReturnNullOrderedQuantityIfStockInHandNull() throws Exception {
    assertThat(calculationStrategy.calculateOrderQuantity(11, null), is(nullValue()));
  }

  @Test
  public void shouldReturnApprovedQuantityAsOrderedQuantityIfFullSupply() throws Exception {
    assertThat(calculationStrategy.calculateDefaultApprovedQuantity(true, 10, 20), is(10));
  }

  @Test
  public void shouldReturnApprovedQuantityAsRequestedQuantityIfNonFullSupply() throws Exception {
    assertThat(calculationStrategy.calculateDefaultApprovedQuantity(false, 10, 20), is(20));
  }

  @Test
  public void shouldCalculateQuantityDispensedIfAllInputPresent() throws Exception {
    assertThat(calculationStrategy.calculateQuantityDispensed(1, 2, 3, 4), is(2));
  }

  @Test
  public void shouldReturnNullQuantityDispensedIfAnyInputIsNull() throws Exception {
    assertThat(calculationStrategy.calculateQuantityDispensed(1, 2, 3, null), is(nullValue()));
  }

  @Test
  public void shouldCalculateStockInHand() throws Exception {
    assertThat(calculationStrategy.calculateStockInHand(1, 2, 3, 4), is(2));
  }

  @Test
  public void shouldCalculateAmc() throws Exception {
    assertThat(calculationStrategy.calculateAmc(10, asList(10, 20)), is(13));
  }

  @Test
  public void shouldCalculateNormalizedConsumption() throws Exception {
    assertThat(calculationStrategy.calculateNormalizedConsumption(1, 2, 1, 30, 10, 30), is(5));
  }

  @Test
  public void shouldCalculateNCIfReportingDaysAreLessThanZero() throws Exception {
    assertThat(calculationStrategy.calculateNormalizedConsumption(30, 1, 1, 30, 10, 30), is(4));
  }

  @Test
  public void shouldCalculateNCIfGIsZero() throws Exception {
    assertThat(calculationStrategy.calculateNormalizedConsumption(30, 1, 1, 30, 0, 30), is(31));
  }
}
