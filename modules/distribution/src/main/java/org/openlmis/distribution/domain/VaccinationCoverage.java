package org.openlmis.distribution.domain;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.List;

public class VaccinationCoverage {


  protected TargetGroupProduct getTargetGroupForLineItem(List<TargetGroupProduct> targetGroupProducts,
                                                         final String vaccination) {

    return (TargetGroupProduct) CollectionUtils.find(targetGroupProducts, new Predicate() {
      @Override
      public boolean evaluate(Object o) {
        return ((TargetGroupProduct) o).getTargetGroupEntity().equalsIgnoreCase(vaccination);
      }
    });
  }
}
