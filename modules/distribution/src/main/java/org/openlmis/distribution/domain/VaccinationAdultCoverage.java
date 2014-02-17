package org.openlmis.distribution.domain;

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
@EqualsAndHashCode(callSuper = true)
public class VaccinationAdultCoverage extends VaccinationCoverage {

  List<AdultCoverageLineItem> adultCoverageLineItems = new ArrayList<>();

  public VaccinationAdultCoverage(FacilityVisit facilityVisit, Facility facility, List<TargetGroupProduct> adultTargetGroupProducts) {

    List<String> validCategories = Collections.unmodifiableList(
      asList("Pregnant Women", "MIF 15-49 years - Community", "MIF 15-49 years - Students", "MIF 15-49 years - Workers",
        "Students Not MIF", "Workers Not MIF", "Others Not MIF"));

    createAdultCoverageLineItems(facilityVisit, facility, adultTargetGroupProducts, validCategories);
  }

  private void createAdultCoverageLineItems(FacilityVisit facilityVisit, Facility facility, List<TargetGroupProduct> adultTargetGroupProducts, List<String> validCategories) {
    for (String category : validCategories) {
      TargetGroupProduct targetGroupForLineItem = getTargetGroupForLineItem(adultTargetGroupProducts, category);
      this.adultCoverageLineItems.add(new AdultCoverageLineItem(facilityVisit, facility, targetGroupForLineItem, category));
    }
  }
}
