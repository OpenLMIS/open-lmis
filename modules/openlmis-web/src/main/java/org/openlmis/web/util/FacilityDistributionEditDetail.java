package org.openlmis.web.util;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonSerialize(include = NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FacilityDistributionEditDetail {

  private Long parentDataScreenId;
  private String parentDataScreen;
  private String parentProperty;

  private Long dataScreenId;
  private String dataScreen;

  private String editedItem;

  private Object originalValue;
  private Object previousValue;
  private Object newValue;

  private boolean conflict;

  public String getDataScreenUI() {
    switch (dataScreen) {
      case "FacilityVisit":
      case "Facilitator":
        return "visit-info";
      case "EpiInventoryLineItem":
        return "epi-inventory";
      case "RefrigeratorReading":
      case "RefrigeratorProblem":
        return "refrigerator-data";
      case "EpiUseLineItem":
        return "epi-use";
      case "VaccinationFullCoverage":
        return "full-coverage";
      case "ChildCoverageLineItem":
        return "child-coverage";
      case "OpenedVialLineItem":
        switch (parentDataScreen) {
          case "VaccinationAdultCoverage":
            return "adult-coverage";
          case "VaccinationChildCoverage":
            return "child-coverage";
          default:
            return "";
        }
      case "AdultCoverageLineItem":
        return "adult-coverage";
      default:
        return "";
    }
  }

  public void setDataScreenUI() {
    // nothing to do
  }
}
