/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *   
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.rnr.calculation;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.db.categories.UnitTests;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@Category(UnitTests.class)
public class VirtualFacilityStrategyTest {

  @Test
  public void shouldUseQuantityDispensedAsStockOutFactorWhenDaysSinceLastRnrLessThanStockOutDays() throws Exception {
    VirtualFacilityStrategy strategy = new VirtualFacilityStrategy();
    Integer stockOutDays = 3;
    Integer quantityDispensed = 4;
    Integer newPatientCount = 5;
    Integer dosesPerMonth = 6;
    Integer dosesPerDispensingUnit = 7;
    Integer daysSinceLastRnr = 2;

    Integer normalizedConsumption = strategy.calculateNormalizedConsumption(stockOutDays, quantityDispensed,
        newPatientCount, dosesPerMonth, dosesPerDispensingUnit, daysSinceLastRnr);

    assertThat(normalizedConsumption, is(9));
  }

  @Test
  public void shouldUseQuantityDispensedAsStockOutFactorWhenDaysSinceLastRnrEqualsStockOutDays() throws Exception {
    VirtualFacilityStrategy strategy = new VirtualFacilityStrategy();
    Integer stockOutDays = 3;
    Integer quantityDispensed = 4;
    Integer newPatientCount = 5;
    Integer dosesPerMonth = 6;
    Integer dosesPerDispensingUnit = 7;
    Integer daysSinceLastRnr = 3;

    Integer normalizedConsumption = strategy.calculateNormalizedConsumption(stockOutDays, quantityDispensed,
        newPatientCount, dosesPerMonth, dosesPerDispensingUnit, daysSinceLastRnr);

    assertThat(normalizedConsumption, is(9));
  }

  @Test
  public void shouldSumNewPatientFactorAndStockOutFactorWhenDaysSinceLastRnrMoreThanStockOutDays() throws Exception {
    VirtualFacilityStrategy strategy = new VirtualFacilityStrategy();
    Integer stockOutDays = 3;
    Integer quantityDispensed = 4;
    Integer newPatientCount = 5;
    Integer dosesPerMonth = 6;
    Integer dosesPerDispensingUnit = 7;
    Integer daysSinceLastRnr = 8;

    Integer normalizedConsumption = strategy.calculateNormalizedConsumption(stockOutDays, quantityDispensed,
        newPatientCount, dosesPerMonth, dosesPerDispensingUnit, daysSinceLastRnr);

    assertThat(normalizedConsumption, is(29));
  }

  @Test
  public void shouldUseQuantityDispensedAsStockOutFactorWhenDaysSinceLastRnrIsNull() throws Exception {
    VirtualFacilityStrategy strategy = new VirtualFacilityStrategy();
    Integer stockOutDays = 3;
    Integer quantityDispensed = 4;
    Integer newPatientCount = 5;
    Integer dosesPerMonth = 6;
    Integer dosesPerDispensingUnit = 7;
    Integer daysSinceLastRnr = null;

    Integer normalizedConsumption = strategy.calculateNormalizedConsumption(stockOutDays, quantityDispensed,
        newPatientCount, dosesPerMonth, dosesPerDispensingUnit, daysSinceLastRnr);

    assertThat(normalizedConsumption, is(9));
  }

  @Test
  public void shouldUseDosesPerDispensingUnitAsOneIfZero() throws Exception {
    VirtualFacilityStrategy strategy = new VirtualFacilityStrategy();
    Integer stockOutDays = 3;
    Integer quantityDispensed = 4;
    Integer newPatientCount = 5;
    Integer dosesPerMonth = 6;
    Integer dosesPerDispensingUnit = 0;
    Integer daysSinceLastRnr = 8;

    Integer normalizedConsumption = strategy.calculateNormalizedConsumption(stockOutDays, quantityDispensed,
        newPatientCount, dosesPerMonth, dosesPerDispensingUnit, daysSinceLastRnr);

    assertThat(normalizedConsumption, is(54));

  }

  @Test
  public void shouldCalculateAMCForTwoPreviousConsumptions() throws Exception {
    Integer normalizedConsumption = 4;
    List<Integer> previousNormalizedConsumption = asList(99, 8);

    Integer amc = new VirtualFacilityStrategy().calculateAmc(new ProcessingPeriod(), normalizedConsumption, previousNormalizedConsumption);

    assertThat(amc, is(37));
  }

  @Test
  public void shouldCalculateAMCForNoPreviousConsumptions() throws Exception {
    Integer normalizedConsumption = 4;
    List<Integer> previousNormalizedConsumption = asList();

    Integer amc = new VirtualFacilityStrategy().calculateAmc(new ProcessingPeriod(), normalizedConsumption, previousNormalizedConsumption);

    assertThat(amc, is(4));
  }

  @Test
  public void shouldCalculateAMCForOnePreviousConsumption() throws Exception {
    Integer normalizedConsumption = 44;
    List<Integer> previousNormalizedConsumption = asList(66);

    Integer amc = new VirtualFacilityStrategy().calculateAmc(new ProcessingPeriod(), normalizedConsumption, previousNormalizedConsumption);

    assertThat(amc, is(55));
  }

  @Test
  public void shouldCalculateAMCAndRoundHALFUPForOnePreviousConsumption() throws Exception {
    Integer normalizedConsumption = 44;
    List<Integer> previousNormalizedConsumption = asList(67);

    Integer amc = new VirtualFacilityStrategy().calculateAmc(new ProcessingPeriod(), normalizedConsumption, previousNormalizedConsumption);

    assertThat(amc, is(56));
  }

  @Test
  public void shouldCalculateAMCAndRoundHALFDOWNForTwoPreviousConsumption() throws Exception {
    Integer normalizedConsumption = 4;
    List<Integer> previousNormalizedConsumption = asList(6, 3);

    Integer amc = new VirtualFacilityStrategy().calculateAmc(new ProcessingPeriod(), normalizedConsumption, previousNormalizedConsumption);

    assertThat(amc, is(4));
  }
}
