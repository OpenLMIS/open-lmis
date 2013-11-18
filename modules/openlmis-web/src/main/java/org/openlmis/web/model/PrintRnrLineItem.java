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
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.rnr.calculation.RnrCalculationStrategy;
import org.openlmis.rnr.domain.Column;
import org.openlmis.rnr.domain.LossesAndAdjustmentsType;
import org.openlmis.rnr.domain.ProgramRnrTemplate;
import org.openlmis.rnr.domain.RnrLineItem;

import java.util.List;

import static org.openlmis.rnr.domain.ProgramRnrTemplate.QUANTITY_DISPENSED;
import static org.openlmis.rnr.domain.ProgramRnrTemplate.STOCK_IN_HAND;

@Data
@AllArgsConstructor
public class PrintRnrLineItem {

  private RnrLineItem rnrLineItem;

  public void calculate(RnrCalculationStrategy calcStrategy, ProcessingPeriod period,
                        List<? extends Column> rnrColumns,
                        List<LossesAndAdjustmentsType> lossesAndAdjustmentsTypes) {
    ProgramRnrTemplate template = new ProgramRnrTemplate(rnrColumns);
    if (template.columnsCalculated(STOCK_IN_HAND)) calculateStockInHand(calcStrategy);
    if (template.columnsCalculated(QUANTITY_DISPENSED)) rnrLineItem.calculateQuantityDispensed(calcStrategy);
    calculateNormalizedConsumption(calcStrategy);
    calculateAmc(calcStrategy, period);
    calculateMaxStockQuantity(calcStrategy);
    calculateLossesAndAdjustments(calcStrategy, lossesAndAdjustmentsTypes);
    rnrLineItem.calculateOrderQuantity(calcStrategy);

    rnrLineItem.calculatePacksToShip(calcStrategy);
  }

  private void calculateStockInHand(RnrCalculationStrategy calcStrategy) {
    try {
      rnrLineItem.calculateStockInHand(calcStrategy);
    } catch (NullPointerException e) {
      rnrLineItem.setStockInHand(null);
    }
  }

  private void calculateMaxStockQuantity(RnrCalculationStrategy calcStrategy) {
    try {
      rnrLineItem.calculateMaxStockQuantity(calcStrategy);
    } catch (NullPointerException e) {
      rnrLineItem.setMaxStockQuantity(null);
    }
  }

  private void calculateAmc(RnrCalculationStrategy calcStrategy, ProcessingPeriod period) {
    try {
      rnrLineItem.calculateAmc(calcStrategy, period);
    } catch (NullPointerException e) {
      rnrLineItem.setAmc(null);
    }
  }

  private void calculateNormalizedConsumption(RnrCalculationStrategy calcStrategy) {
    try {
      rnrLineItem.calculateNormalizedConsumption(calcStrategy);
    } catch (NullPointerException e) {
      rnrLineItem.setNormalizedConsumption(null);
    }
  }

  private void calculateLossesAndAdjustments(RnrCalculationStrategy calcStrategy, List<LossesAndAdjustmentsType> lossesAndAdjustmentsTypes) {
    try {
      rnrLineItem.calculateTotalLossesAndAdjustments(calcStrategy, lossesAndAdjustmentsTypes);
    } catch (NullPointerException e) {
      rnrLineItem.setTotalLossesAndAdjustments(null);
    }
  }

}
