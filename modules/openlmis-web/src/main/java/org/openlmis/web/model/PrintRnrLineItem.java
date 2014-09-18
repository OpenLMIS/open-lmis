/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.openlmis.rnr.domain.*;

import java.util.List;

import static java.util.Arrays.asList;
import static org.openlmis.rnr.domain.ProgramRnrTemplate.QUANTITY_DISPENSED;
import static org.openlmis.rnr.domain.ProgramRnrTemplate.STOCK_IN_HAND;

/**
 * This class abstracts the calculation logic from RnrLineItem for printing purpose.
 */

@Data
@AllArgsConstructor
public class PrintRnrLineItem {

  private static List<RnrStatus> statusList = asList(RnrStatus.AUTHORIZED, RnrStatus.APPROVED, RnrStatus.IN_APPROVAL, RnrStatus.RELEASED);
  private RnrLineItem rnrLineItem;

  public void calculate(List<? extends Column> rnrColumns,
                        List<LossesAndAdjustmentsType> lossesAndAdjustmentsTypes, Integer numberOfMonths, RnrStatus status) {
    ProgramRnrTemplate template = new ProgramRnrTemplate(rnrColumns);
    if (template.columnsCalculated(STOCK_IN_HAND)) calculateStockInHand(status);
    if (template.columnsCalculated(QUANTITY_DISPENSED) && !statusList.contains(status))
      rnrLineItem.calculateQuantityDispensed();
    calculateNormalizedConsumption(template, status);
    calculatePeriodNormalizedConsumption(numberOfMonths);
    calculateAmc(numberOfMonths);
    calculateMaxStockQuantity(template);
    calculateLossesAndAdjustments(lossesAndAdjustmentsTypes);
    rnrLineItem.calculateOrderQuantity();

    rnrLineItem.calculatePacksToShip();
  }

  private void calculatePeriodNormalizedConsumption(Integer numberOfMonths) {
    try {
      rnrLineItem.calculatePeriodNormalizedConsumption(numberOfMonths);
    } catch (NullPointerException e) {
      rnrLineItem.setPeriodNormalizedConsumption(null);
    }
  }

  private void calculateStockInHand(RnrStatus status) {

    try {
      if (!statusList.contains(status)) {
        rnrLineItem.calculateStockInHand();
      }
    } catch (NullPointerException e) {
      rnrLineItem.setStockInHand(null);
    }
  }

  private void calculateMaxStockQuantity(ProgramRnrTemplate template) {
    try {
      rnrLineItem.calculateMaxStockQuantity(template);
    } catch (NullPointerException e) {
      rnrLineItem.setMaxStockQuantity(null);
    }
  }

  private void calculateAmc(Integer numberOfMonths) {
    try {
      rnrLineItem.calculateAmc(numberOfMonths);
    } catch (NullPointerException e) {
      rnrLineItem.setAmc(null);
    }
  }

  private void calculateNormalizedConsumption(ProgramRnrTemplate template, RnrStatus status) {
    try {
      if (!statusList.contains(status)) {
        rnrLineItem.calculateNormalizedConsumption(template);
      }
    } catch (NullPointerException e) {
      rnrLineItem.setNormalizedConsumption(null);
    }
  }

  private void calculateLossesAndAdjustments(List<LossesAndAdjustmentsType> lossesAndAdjustmentsTypes) {
    try {
      rnrLineItem.calculateTotalLossesAndAdjustments(lossesAndAdjustmentsTypes);
    } catch (NullPointerException e) {
      rnrLineItem.setTotalLossesAndAdjustments(null);
    }
  }

}
