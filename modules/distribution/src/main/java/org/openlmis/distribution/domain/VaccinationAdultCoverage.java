package org.openlmis.distribution.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class VaccinationAdultCoverage extends VaccinationCoverage {

  private static final List<String> validProductVials = Collections.unmodifiableList(asList("Tetanus"));

  List<AdultCoverageLineItem> adultCoverageLineItems = new ArrayList<>();

  public VaccinationAdultCoverage(FacilityVisit facilityVisit, Facility facility,
                                  List<TargetGroupProduct> adultTargetGroupProducts, List<ProductVial> adultProductVial) {
    super(facilityVisit, facility, adultProductVial, validProductVials);

    List<String> validDemographicGroups = Collections.unmodifiableList(
      asList("Pregnant Women", "MIF 15-49 years - Community", "MIF 15-49 years - Students", "MIF 15-49 years - Workers",
        "Students not MIF", "Workers not MIF", "Other not MIF"));

    createAdultCoverageLineItems(facilityVisit, facility, adultTargetGroupProducts, validDemographicGroups);
  }

  private void createAdultCoverageLineItems(FacilityVisit facilityVisit, Facility facility,
                                            List<TargetGroupProduct> adultTargetGroupProducts, List<String> demographicGroups) {
    for (String demographicGroup : demographicGroups) {
      TargetGroupProduct targetGroupForLineItem = getTargetGroupForLineItem(adultTargetGroupProducts, demographicGroup);
      this.adultCoverageLineItems.add(new AdultCoverageLineItem(facilityVisit, facility, targetGroupForLineItem, demographicGroup));
    }
  }
}
