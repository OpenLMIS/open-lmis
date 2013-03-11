package org.openlmis.web.model;

import lombok.Data;
import org.openlmis.core.domain.ProcessingPeriod;
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


  public void calculate(ProcessingPeriod period, List<RnrColumn> rnrColumns) {
    ProgramRnrTemplate template = new ProgramRnrTemplate(rnrColumns);
    calculateNormalizedConsumption();
    calculateAmc(period);
    calculateMaxStockQuantity();
    calculateLossesAndAdjustments();
   if (template.columnsCalculated(STOCK_IN_HAND)) calculateStockInHand();
   if (template.columnsCalculated(QUANTITY_DISPENSED)) calculateQuantityDispensed();
    calculateOrderQuantity();

    calculatePacksToShip();
  }

  private void calculatePacksToShip() {
    try {
      rnrLineItem.calculatePacksToShip();
    } catch (Exception e) {
      rnrLineItem.setPacksToShip(null);
    }
  }

  private void calculateOrderQuantity() {
    try {
      rnrLineItem.calculateOrderQuantity();
    } catch (Exception e) {
      rnrLineItem.setCalculatedOrderQuantity(null);
    }
  }

  private void calculateQuantityDispensed() {
    try {
      rnrLineItem.calculateQuantityDispensed();
    } catch (Exception e) {
      rnrLineItem.setQuantityDispensed(null);
    }
  }

  private void calculateStockInHand() {
    try {
      rnrLineItem.calculateStockInHand();
    } catch (Exception e) {
      rnrLineItem.setStockInHand(null);
    }
  }

  private void calculateMaxStockQuantity() {
    try {
      rnrLineItem.calculateMaxStockQuantity();
    } catch (Exception e) {
      rnrLineItem.setMaxStockQuantity(null);
    }
  }

  private void calculateAmc(ProcessingPeriod period) {
    try {
      rnrLineItem.calculateAmc(period);
    } catch (Exception e) {
      rnrLineItem.setAmc(null);
    }
  }

  private void calculateNormalizedConsumption() {
    try {
      rnrLineItem.calculateNormalizedConsumption();
    } catch (Exception e) {
      rnrLineItem.setNormalizedConsumption(null);
    }
  }

  private void calculateLossesAndAdjustments() {
    try {
      rnrLineItem.calculateTotalLossesAndAdjustments();
    } catch (Exception e) {
      rnrLineItem.setTotalLossesAndAdjustments(null);
    }
  }

}
