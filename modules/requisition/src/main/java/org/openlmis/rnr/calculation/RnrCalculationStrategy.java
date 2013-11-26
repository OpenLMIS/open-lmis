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

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.openlmis.core.service.ProcessingScheduleService;
import org.openlmis.rnr.domain.LossesAndAdjustments;
import org.openlmis.rnr.domain.LossesAndAdjustmentsType;
import org.openlmis.rnr.repository.RequisitionRepository;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.floor;
import static java.math.RoundingMode.HALF_UP;
import static org.openlmis.rnr.domain.RnrLineItem.NUMBER_OF_DAYS;

@AllArgsConstructor
@NoArgsConstructor
public abstract class RnrCalculationStrategy {

  public static final MathContext MATH_CONTEXT = new MathContext(3, HALF_UP);

  ProcessingScheduleService processingScheduleService;

  RequisitionRepository requisitionRepository;

  public Integer calculatePacksToShip(Integer orderQuantity, Integer packSize, Integer packRoundingThreshold, Boolean roundToZero) {
    Integer packsToShip = null;
    if (orderQuantity != null && packSize != null) {
      packsToShip = ((orderQuantity == 0) ? 0 : round(packSize, packRoundingThreshold, roundToZero, orderQuantity));
    }
    return packsToShip;
  }

  public Integer calculateMaxStockQuantity(Integer maxMonthsOfStock, Integer amc) {
    return maxMonthsOfStock * amc;
  }

  public Integer calculateOrderQuantity(Integer maxStockQuantity, Integer stockInHand) {
    Integer calculatedOrderQuantity = null;
    if (!isAnyNull(maxStockQuantity, stockInHand)) {
      calculatedOrderQuantity = (maxStockQuantity - stockInHand < 0) ? 0 : maxStockQuantity - stockInHand;
    }
    return calculatedOrderQuantity;
  }

  public Integer calculateDefaultApprovedQuantity(boolean fullSupply, Integer calculatedOrderQuantity, Integer quantityRequested) {
    return fullSupply ? calculatedOrderQuantity : quantityRequested;
  }

  public Integer calculateQuantityDispensed(Integer beginningBalance, Integer quantityReceived, Integer totalLossesAndAdjustments, Integer stockInHand) {
    Integer quantityDispensed = null;
    if (!isAnyNull(beginningBalance, quantityReceived, totalLossesAndAdjustments, stockInHand)) {
      quantityDispensed = beginningBalance + quantityReceived + totalLossesAndAdjustments - stockInHand;
    }
    return quantityDispensed;
  }

  public Integer calculateStockInHand(Integer beginningBalance, Integer quantityReceived, Integer totalLossesAndAdjustments, Integer quantityDispensed) {
    return beginningBalance + quantityReceived + totalLossesAndAdjustments - quantityDispensed;
  }

  public Integer calculateTotalLossesAndAdjustments(List<LossesAndAdjustments> lossesAndAdjustments, List<LossesAndAdjustmentsType> lossesAndAdjustmentsTypes) {
    Integer totalLossesAndAdjustments = 0;
    for (LossesAndAdjustments lossAndAdjustment : lossesAndAdjustments) {
      if (getAdditive(lossAndAdjustment, lossesAndAdjustmentsTypes)) {
        totalLossesAndAdjustments += lossAndAdjustment.getQuantity();
      } else {
        totalLossesAndAdjustments -= lossAndAdjustment.getQuantity();
      }
    }
    return totalLossesAndAdjustments;
  }

  public Integer calculateAmc(final Integer normalizedConsumption, final List<Integer> previousNormalizedConsumptions) {
    ArrayList<Integer> normalizedConsumptions = new ArrayList<Integer>() {{
      add(normalizedConsumption);
      addAll(previousNormalizedConsumptions);
    }};
    Integer amc = getSum(normalizedConsumptions);
    return new BigDecimal(amc).divide(new BigDecimal(normalizedConsumptions.size()), MATH_CONTEXT).setScale(0, HALF_UP).intValue();
  }

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
      return quantityDispensed.add(newPatientFactor).setScale(0, HALF_UP).intValue();
    }

    BigDecimal daysSinceLastRequisition = new BigDecimal(daysSinceLastRnr);
    BigDecimal stockOutFactor = quantityDispensed.multiply(NUMBER_OF_DAYS.divide((daysSinceLastRequisition.subtract(stockOutDays)), MATH_CONTEXT));

    return stockOutFactor.add(newPatientFactor).setScale(0, HALF_UP).intValue();
  }

  private Integer getSum(List<Integer> previousNormalizedConsumptions) {
    Integer amc = 0;
    for (Integer nc : previousNormalizedConsumptions) {
      amc += nc;
    }
    return amc;
  }

  private boolean isAnyNull(Integer... fields) {
    for (Integer field : fields) {
      if (field == null) return true;
    }
    return false;
  }

  private Integer round(Integer packSize, Integer packRoundingThreshold, Boolean roundToZero, Integer orderQuantity) {
    Double packsToShip = floor(orderQuantity / packSize);
    Integer remainderQuantity = orderQuantity % packSize;
    if (remainderQuantity >= packRoundingThreshold) {
      packsToShip += 1;
    }

    if (packsToShip == 0 && !roundToZero) {
      packsToShip = 1d;
    }
    return packsToShip.intValue();
  }

  private Boolean getAdditive(final LossesAndAdjustments lossAndAdjustment, List<LossesAndAdjustmentsType> lossesAndAdjustmentsTypes) {
    Predicate predicate = new Predicate() {
      @Override
      public boolean evaluate(Object o) {
        return lossAndAdjustment.getType().getName().equals(((LossesAndAdjustmentsType) o).getName());
      }
    };

    LossesAndAdjustmentsType lossAndAdjustmentTypeFromList = (LossesAndAdjustmentsType) CollectionUtils.find(
        lossesAndAdjustmentsTypes, predicate);

    return lossAndAdjustmentTypeFromList.getAdditive();
  }

}
