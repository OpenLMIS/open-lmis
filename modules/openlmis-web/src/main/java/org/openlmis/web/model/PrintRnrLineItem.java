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
    rnrLineItem.calculatePacksToShip();
  }

  private void calculateOrderQuantity() {
    rnrLineItem.calculateOrderQuantity();
  }

  private void calculateQuantityDispensed() {
    rnrLineItem.calculateQuantityDispensed();
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

  private void calculateLossesAndAdjustments() {
    try {
      rnrLineItem.calculateTotalLossesAndAdjustments();
    } catch (NullPointerException e) {
      rnrLineItem.setTotalLossesAndAdjustments(null);
    }
  }

}
