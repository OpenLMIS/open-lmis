/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.model;

import lombok.Data;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.rnr.domain.*;

import java.util.List;

import static org.openlmis.rnr.domain.ProgramRnrTemplate.QUANTITY_DISPENSED;
import static org.openlmis.rnr.domain.ProgramRnrTemplate.STOCK_IN_HAND;

@Data
public class PrintRnrLineItem {

  private LineItem lineItem;

  public PrintRnrLineItem(LineItem lineItem) {
    this.lineItem = lineItem;
  }


  public void calculate(ProcessingPeriod period, List<? extends Column> rnrColumns, List<LossesAndAdjustmentsType> lossesAndAdjustmentsTypes) {
    ProgramRnrTemplate template = new ProgramRnrTemplate(rnrColumns);
    RnrLineItem rnrLineItem = (RnrLineItem) this.lineItem;
    if (template.columnsCalculated(STOCK_IN_HAND)) calculateStockInHand();
    if (template.columnsCalculated(QUANTITY_DISPENSED)) {
      rnrLineItem.calculateQuantityDispensed();
    }
    calculateNormalizedConsumption();
    calculateAmc(period);
    calculateMaxStockQuantity();
    calculateLossesAndAdjustments(lossesAndAdjustmentsTypes);
    rnrLineItem.calculateOrderQuantity();

    rnrLineItem.calculatePacksToShip();
  }

  private void calculateStockInHand() {
    try {
      ((RnrLineItem) lineItem).calculateStockInHand();
    } catch (NullPointerException e) {
      ((RnrLineItem) lineItem).setStockInHand(null);
    }
  }

  private void calculateMaxStockQuantity() {
    try {
      ((RnrLineItem) lineItem).calculateMaxStockQuantity();
    } catch (NullPointerException e) {
      ((RnrLineItem) lineItem).setMaxStockQuantity(null);
    }
  }

  private void calculateAmc(ProcessingPeriod period) {
    try {
      ((RnrLineItem) lineItem).calculateAmc(period);
    } catch (NullPointerException e) {
      ((RnrLineItem) lineItem).setAmc(null);
    }
  }

  private void calculateNormalizedConsumption() {
    try {
      ((RnrLineItem) lineItem).calculateNormalizedConsumption();
    } catch (NullPointerException e) {
      ((RnrLineItem) lineItem).setNormalizedConsumption(null);
    }
  }

  private void calculateLossesAndAdjustments(List<LossesAndAdjustmentsType> lossesAndAdjustmentsTypes) {
    try {
      ((RnrLineItem) lineItem).calculateTotalLossesAndAdjustments(lossesAndAdjustmentsTypes);
    } catch (NullPointerException e) {
      ((RnrLineItem) lineItem).setTotalLossesAndAdjustments(null);
    }
  }

}
