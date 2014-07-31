package org.openlmis.rnr.domain;

import java.util.Comparator;

/**
 *  LineItemComparator represents a comparator for comparing fields of two RnrLineItem.
 */

public class LineItemComparator implements Comparator<RnrLineItem> {

  @Override
  public int compare(RnrLineItem lineItem1, RnrLineItem lineItem2) {
    if (lineItem1.getProductCategoryDisplayOrder() == lineItem2.getProductCategoryDisplayOrder()) {
      if (lineItem1.getProductCategory().equals(lineItem2.getProductCategory())) {
        if (lineItem1.getProductDisplayOrder() == null && lineItem2.getProductDisplayOrder() == null) {
          return lineItem1.getProductCode().compareTo(lineItem2.getProductCode());
        }

        if (lineItem1.getProductDisplayOrder() == lineItem2.getProductDisplayOrder()) {
          return lineItem1.getProductCode().compareTo(lineItem2.getProductCode());
        }

        if (lineItem2.getProductDisplayOrder() == null) return -1;
        if (lineItem1.getProductDisplayOrder() == null) return 1;

        return lineItem1.getProductDisplayOrder() - lineItem2.getProductDisplayOrder();
      }
      return lineItem1.getProductCategory().compareTo(lineItem2.getProductCategory());
    }

    return lineItem1.getProductCategoryDisplayOrder() - lineItem2.getProductCategoryDisplayOrder();
  }
}
