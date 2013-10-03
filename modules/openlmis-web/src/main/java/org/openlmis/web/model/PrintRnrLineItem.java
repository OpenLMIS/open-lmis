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
