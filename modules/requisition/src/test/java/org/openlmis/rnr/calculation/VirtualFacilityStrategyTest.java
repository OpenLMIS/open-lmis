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
import org.openlmis.db.categories.UnitTests;

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
}
