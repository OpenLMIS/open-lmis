/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.model;

import lombok.Data;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.rnr.domain.LossesAndAdjustmentsType;
import org.openlmis.rnr.domain.ProgramRnrTemplate;
import org.openlmis.rnr.domain.RnrColumn;
import org.openlmis.rnr.domain.RnrLineItem;

import java.util.List;

import static org.openlmis.rnr.domain.ProgramRnrTemplate.QUANTITY_DISPENSED;
import static org.openlmis.rnr.domain.ProgramRnrTemplate.STOCK_IN_HAND;

@Data
public class PrintRnrLineItem {

  private RnrLineItem rnrLineItem;

  public PrintRnrLineItem(RnrLineItem rnrLineItem) {
    this.rnrLineItem = rnrLineItem;
  }


  public void calculate(ProcessingPeriod period, List<RnrColumn> rnrColumns, List<LossesAndAdjustmentsType> lossesAndAdjustmentsTypes) {
    ProgramRnrTemplate template = new ProgramRnrTemplate(rnrColumns);
    if (template.columnsCalculated(STOCK_IN_HAND)) calculateStockInHand();
    if (template.columnsCalculated(QUANTITY_DISPENSED)) rnrLineItem.calculateQuantityDispensed();
    calculateNormalizedConsumption();
    calculateAmc(period);
    calculateMaxStockQuantity();
    calculateLossesAndAdjustments(lossesAndAdjustmentsTypes);
    rnrLineItem.calculateOrderQuantity();

    rnrLineItem.calculatePacksToShip();
  }

  private void calculateStockInHand() {
    try {
      rnrLineItem.calculateStockInHand();
    } catch (NullPointerException e) {
      rnrLineItem.setStockInHand(null);
    }
  }

  private void calculateMaxStockQuantity() {
    try {
      rnrLineItem.calculateMaxStockQuantity();
    } catch (NullPointerException e) {
      rnrLineItem.setMaxStockQuantity(null);
    }
  }

  private void calculateAmc(ProcessingPeriod period) {
    try {
      rnrLineItem.calculateAmc(period);
    } catch (NullPointerException e) {
      rnrLineItem.setAmc(null);
    }
  }

  private void calculateNormalizedConsumption() {
    try {
      rnrLineItem.calculateNormalizedConsumption();
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
