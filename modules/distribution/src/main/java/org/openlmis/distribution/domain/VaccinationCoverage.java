package org.openlmis.distribution.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.openlmis.core.domain.Facility;

import java.util.ArrayList;
import java.util.List;

/**
 *  VaccinationCoverage is a base class for VaccinationChildCoverage and VaccinationAdultCoverage
 *  containing list of OpenedVialLineItem.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VaccinationCoverage {

  protected List<OpenedVialLineItem> openedVialLineItems = new ArrayList<>();

  public VaccinationCoverage(FacilityVisit facilityVisit,
                             Facility facility, List<ProductVial> productVials,
                             List<String> validProductVials) {
    createOpenedVialLineItems(facilityVisit, facility, productVials, validProductVials);
  }

  private void createOpenedVialLineItems(FacilityVisit facilityVisit,
                                         Facility facility, List<ProductVial> productVials,
                                         List<String> validProductVials) {

    ProductVial productVial;

    for (final String productVialName : validProductVials) {
      productVial = (ProductVial) CollectionUtils.find(productVials, new Predicate() {
        @Override
        public boolean evaluate(Object o) {
          return ((ProductVial) o).getVial().equalsIgnoreCase(productVialName);
        }
      });
      this.openedVialLineItems.add(new OpenedVialLineItem(facilityVisit, facility, productVial, productVialName));
    }
  }


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
