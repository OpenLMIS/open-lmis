/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.rnr.domain;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.openlmis.core.domain.ProcessingPeriod;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

import static java.lang.Math.floor;
import static java.math.MathContext.DECIMAL64;
import static java.math.RoundingMode.HALF_UP;
import static org.openlmis.rnr.domain.RnrLineItem.MULTIPLIER;
import static org.openlmis.rnr.domain.RnrLineItem.NUMBER_OF_DAYS;


public class RnrCalcStrategy {

    public static final MathContext MATH_CONTEXT = new MathContext(12, HALF_UP);

    public Integer calculatePacksToShip(Integer orderQuantity, Integer packSize, Integer packRoundingThreshold, Boolean roundToZero) {
        Integer packsToShip = null;
        if (orderQuantity != null && packSize != null) {
            packsToShip = ((orderQuantity == 0) ? 0 : round(packSize, packRoundingThreshold, roundToZero, orderQuantity));
        }
        return packsToShip;
    }

    public Integer calculateAmc(ProcessingPeriod period, Integer normalizedConsumption,
                                List<Integer> previousNormalizedConsumptions, BigDecimal sumOfPreviousNormalizedConsumptions) {
        int denominator = period.getNumberOfMonths() * (1 + previousNormalizedConsumptions.size());
        return (new BigDecimal(normalizedConsumption).add(sumOfPreviousNormalizedConsumptions)).
                divide(new BigDecimal(denominator), DECIMAL64).setScale(0, HALF_UP).intValue();

    }

    public Integer calculateMaxStockQuantity(Integer maxMonthsOfStock, Integer amc) {
        return maxMonthsOfStock * amc;
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

    public Integer calcuclateOrderQuantity(Integer maxStockQuantity, Integer stockInHand) {
        Integer calculatedOrderQuantity = null;
        if (!isAnyNull(maxStockQuantity, stockInHand)) {
            calculatedOrderQuantity = (maxStockQuantity - stockInHand < 0) ? 0 : maxStockQuantity - stockInHand;
        }
        return calculatedOrderQuantity;
    }

    private boolean isAnyNull(Integer... fields) {
        for (Integer field : fields) {
            if (field == null) return true;
        }
        return false;
    }

    public Integer calculateNormalizedConsumption(Integer stockOutDays, Integer quantityDispensed, Integer newPatientCount, Integer dosesPerMonth, Integer dosesPerDispensingUnit) {
        BigDecimal stockOut = new BigDecimal(stockOutDays);
        BigDecimal dispensedQuantity = new BigDecimal(quantityDispensed);
        BigDecimal consumptionAdjustedWithStockOutDays =
                (RnrLineItem.MULTIPLIER.multiply(NUMBER_OF_DAYS)).subtract(stockOut).equals(new BigDecimal(0)) ?
                        dispensedQuantity :
                        (dispensedQuantity.multiply((MULTIPLIER.multiply(NUMBER_OF_DAYS)).divide(((MULTIPLIER
                                .multiply(NUMBER_OF_DAYS)).subtract(stockOut)), DECIMAL64))
                                .setScale(0, HALF_UP));

        BigDecimal adjustmentForNewPatients =
                (new BigDecimal(newPatientCount).multiply(new BigDecimal(dosesPerMonth)
                        .divide(new BigDecimal(dosesPerDispensingUnit), MATH_CONTEXT))).multiply(MULTIPLIER);

        return (consumptionAdjustedWithStockOutDays.add(adjustmentForNewPatients)).intValue();
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

}
