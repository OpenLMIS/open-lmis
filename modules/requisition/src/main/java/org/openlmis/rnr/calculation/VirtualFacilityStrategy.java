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

import org.openlmis.core.domain.ProcessingPeriod;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.math.MathContext.DECIMAL64;
import static java.math.RoundingMode.HALF_UP;
import static org.openlmis.rnr.domain.RnrLineItem.NUMBER_OF_DAYS;

public class VirtualFacilityStrategy extends RnrCalculationStrategy {

  @Override
  public Integer calculateAmc(ProcessingPeriod period, final Integer normalizedConsumption, final List<Integer> previousNormalizedConsumptions) {
    ArrayList<Integer> normalizedConsumptions = new ArrayList<Integer>() {{
      add(normalizedConsumption);
      addAll(previousNormalizedConsumptions);
    }};
    Integer amc = getSum(normalizedConsumptions);
    return new BigDecimal(amc).divide(new BigDecimal(normalizedConsumptions.size()), 0, HALF_UP).intValue();
  }

  private Integer getSum(List<Integer> previousNormalizedConsumptions) {
    Integer amc = 0;
    for (Integer nc : previousNormalizedConsumptions) {
      amc += nc;
    }
    return amc;
  }

  @Override
  public Integer calculateNormalizedConsumption(Integer stockOutDays,
                                                Integer quantityDispensed,
                                                Integer newPatientCount,
                                                Integer dosesPerMonth,
                                                Integer dosesPerDispensingUnit, Integer daysSinceLastRnr) {

    dosesPerDispensingUnit = Math.max(1, dosesPerDispensingUnit);

    return calculateNormalizedConsumption(new BigDecimal(stockOutDays),
        new BigDecimal(quantityDispensed),
        new BigDecimal(newPatientCount),
        new BigDecimal(dosesPerMonth),
        new BigDecimal(dosesPerDispensingUnit),
        daysSinceLastRnr);
  }

  private Integer calculateNormalizedConsumption(BigDecimal stockOutDays,
                                                 BigDecimal quantityDispensed,
                                                 BigDecimal newPatientCount,
                                                 BigDecimal dosesPerMonth,
                                                 BigDecimal dosesPerDispensingUnit, Integer daysSinceLastRnr) {

    BigDecimal newPatientFactor = newPatientCount.multiply(dosesPerMonth.divide(dosesPerDispensingUnit, MATH_CONTEXT).setScale(0, HALF_UP));

    if (daysSinceLastRnr == null || stockOutDays.compareTo(new BigDecimal(daysSinceLastRnr)) >= 0) {
      return quantityDispensed.add(newPatientFactor).intValue();
    }

    BigDecimal daysSinceLastRequisition = new BigDecimal(daysSinceLastRnr);
    BigDecimal stockOutFactor = quantityDispensed.multiply(NUMBER_OF_DAYS.divide(daysSinceLastRequisition.subtract(stockOutDays)), DECIMAL64);

    return stockOutFactor.add(newPatientFactor).setScale(0, HALF_UP).intValue();
  }
}
