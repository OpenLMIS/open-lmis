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

import java.math.BigDecimal;

import static java.math.RoundingMode.HALF_UP;
import static org.openlmis.rnr.domain.RnrLineItem.NUMBER_OF_DAYS;

public class RegularRnrCalcStrategy extends RnrCalculationStrategy {

  @Override
  public Integer calculateNormalizedConsumption(Integer stockOutDays,
                                                Integer quantityDispensed,
                                                Integer newPatientCount,
                                                Integer dosesPerMonth,
                                                Integer dosesPerDispensingUnit,
                                                Integer D) {
    BigDecimal reportingDays = new BigDecimal(D);
    BigDecimal stockOut = new BigDecimal(stockOutDays);
    BigDecimal dispensedQuantity = new BigDecimal(quantityDispensed);
    dosesPerDispensingUnit = Math.max(1, dosesPerDispensingUnit);

    BigDecimal consumptionAdjustedWithStockOutDays =
      (reportingDays.subtract(stockOut).compareTo(BigDecimal.ZERO) <= 0) ?
        dispensedQuantity :
        (dispensedQuantity.multiply(NUMBER_OF_DAYS.divide((reportingDays.subtract(stockOut)), MATH_CONTEXT))
          .setScale(0, HALF_UP));

    BigDecimal adjustmentForNewPatients =
      (new BigDecimal(newPatientCount).multiply(new BigDecimal(dosesPerMonth)
        .divide(new BigDecimal(dosesPerDispensingUnit), MATH_CONTEXT))).setScale(0, HALF_UP);

    return (consumptionAdjustedWithStockOutDays.add(adjustmentForNewPatients)).intValue();
  }
}
