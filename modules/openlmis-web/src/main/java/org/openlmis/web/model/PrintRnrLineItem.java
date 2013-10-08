/*
 *
 *  * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *  *
 *  * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package org.openlmis.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.rnr.domain.*;

import java.util.List;

import static org.openlmis.rnr.domain.ProgramRnrTemplate.QUANTITY_DISPENSED;
import static org.openlmis.rnr.domain.ProgramRnrTemplate.STOCK_IN_HAND;

@Data
@AllArgsConstructor
public class PrintRnrLineItem {

  private RnrLineItem rnrLineItem;

  public void calculate(RnrCalcStrategy calcStrategy, ProcessingPeriod period, List<? extends Column> rnrColumns, List<LossesAndAdjustmentsType> lossesAndAdjustmentsTypes) {
    ProgramRnrTemplate template = new ProgramRnrTemplate(rnrColumns);
    if (template.columnsCalculated(STOCK_IN_HAND)) calculateStockInHand(calcStrategy);
    if (template.columnsCalculated(QUANTITY_DISPENSED)) rnrLineItem.calculateQuantityDispensed(calcStrategy);
    calculateNormalizedConsumption(calcStrategy);
    calculateAmc(calcStrategy, period);
    calculateMaxStockQuantity(calcStrategy,template);
    calculateLossesAndAdjustments(calcStrategy, lossesAndAdjustmentsTypes);
    rnrLineItem.calculateOrderQuantity(calcStrategy);

    rnrLineItem.calculatePacksToShip(calcStrategy);
  }

  private void calculateStockInHand(RnrCalcStrategy calcStrategy) {
    try {
      rnrLineItem.calculateStockInHand(calcStrategy);
    } catch (NullPointerException e) {
      rnrLineItem.setStockInHand(null);
    }
  }

  private void calculateMaxStockQuantity(RnrCalcStrategy calcStrategy, ProgramRnrTemplate template) {
    try {
      rnrLineItem.calculateMaxStockQuantity(calcStrategy, template);
    } catch (NullPointerException e) {
      rnrLineItem.setMaxStockQuantity(null);
    }
  }

  private void calculateAmc(RnrCalcStrategy calcStrategy, ProcessingPeriod period) {
    try {
      rnrLineItem.calculateAmc(calcStrategy, period);
    } catch (NullPointerException e) {
      rnrLineItem.setAmc(null);
    }
  }

  private void calculateNormalizedConsumption(RnrCalcStrategy calcStrategy) {
    try {
      rnrLineItem.calculateNormalizedConsumption(calcStrategy);
    } catch (NullPointerException e) {
      rnrLineItem.setNormalizedConsumption(null);
    }
  }

  private void calculateLossesAndAdjustments(RnrCalcStrategy calcStrategy, List<LossesAndAdjustmentsType> lossesAndAdjustmentsTypes) {
    try {
      rnrLineItem.calculateTotalLossesAndAdjustments(calcStrategy, lossesAndAdjustmentsTypes);
    } catch (NullPointerException e) {
      rnrLineItem.setTotalLossesAndAdjustments(null);
    }
  }

}
